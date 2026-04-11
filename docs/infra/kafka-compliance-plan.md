# Kafka 구현 정합성 개선 계획

> 브랜치: `feature/kafka-compliance`
> 대상: Resume Q&A Q63~Q71 답변과 실제 코드 간 불일치 해소

---

## 현황 요약

| Q | 주제 | 상태 |
|---|---|---|
| Q63 | 주문 ID Partition Key | ❌ 미구현 |
| Q64 | eventId 기반 멱등성 검증 | ❌ 미구현 |
| Q65 | 지수 백오프 재시도 + DLQ | ❌ 미구현 |
| Q66 | Rebalancing 튜닝 설정 | ⚪ 이론 질문 (설정만 추가) |
| Q67 | Exactly-Once (Outbox + 멱등성) | 🔶 부분 (Outbox O, 멱등성 X) |
| Q68 | manual commit / commitSync | ❌ 미구현 (auto commit 중) |
| Q69 | acks=all + min.insync.replicas=2 | ❌ 미구현 (기본값 acks=1) |
| Q70 | Kafka Consumer Lag 모니터링 | 🔶 부분 (Prometheus 있음, Lag 전용 미설정) |
| Q71 | Outbox 패턴 | ✅ 구현 완료 |

---

## 구현 계획 (우선순위 순)

### STEP 1 — Producer 설정 (Q63, Q69)

**변경 파일**: `application-local.yml`, `KafkaProducer.kt`

#### Q69: acks=all + min.insync.replicas 설정

```yaml
# application-local.yml
spring:
  kafka:
    producer:
      acks: all
      properties:
        max.in.flight.requests.per.connection: 1
        enable.idempotence: true
```

- `acks=all`: Leader + 모든 ISR 수신 확인 후 성공 처리
- `enable.idempotence=true`: Producer 재전송 시 중복 발행 방지
- `max.in.flight.requests.per.connection=1`: 순서 보장

> 단일 브로커 로컬 환경에서는 `min.insync.replicas`를 별도 브로커 설정으로 관리하므로 yml에는 미포함. 면접 시 "운영 환경에서는 min.insync.replicas=2 브로커 설정과 함께 사용"으로 답변.

#### Q63: 주문 ID를 Partition Key로 설정

```kotlin
// KafkaProducer.kt
fun sendOutboxEvent(event: OutboxEventInfo) {
    kafkaTemplate.send(event.topic, event.partitionKey, event)
}
```

- `OutboxEventInfo`에 `partitionKey` 필드 추가
- 각 이벤트 생성 시점에 orderId를 partitionKey로 주입
- 동일 주문의 이벤트가 동일 파티션에 라우팅되어 순서 보장

**영향 범위**: `OutboxEventInfo`, `KafkaProducer`, 각 Facade/Service의 OutboxEvent 생성 지점

---

### STEP 2 — Consumer 설정 (Q66, Q68)

**변경 파일**: `application-local.yml`, 각 `*KafkaConsumer.kt`

#### Q68: manual commit 전환

```yaml
# application-local.yml
spring:
  kafka:
    consumer:
      enable-auto-commit: false
    listener:
      ack-mode: manual_immediate
```

```kotlin
// 각 Consumer listen() 메서드 시그니처 변경
@KafkaListener(...)
fun listen(event: OutboxEventInfo, ack: Acknowledgment) {
    try {
        // ... 처리 로직
        ack.acknowledge()  // 성공 시에만 offset commit
    } catch (e: Exception) {
        // ack 호출 안 함 → 재처리 대상
    }
}
```

- 대상 Consumer: `PaymentKafkaConsumer`, `BalanceKafkaConsumer`, `OrderStockFailKafkaConsumer`, `NotificationKafkaConsumer`, `OrderProductStockKafkaConsumer`
- `manual_immediate`: acknowledge() 호출 즉시 commit (commitSync 동작)

#### Q66: Rebalancing 튜닝 설정

```yaml
# application-local.yml
spring:
  kafka:
    consumer:
      properties:
        session.timeout.ms: 30000        # Consumer 장애 감지 시간 (기본 45s → 단축)
        heartbeat.interval.ms: 10000     # Heartbeat 주기 (session.timeout의 1/3)
        max.poll.interval.ms: 300000     # 처리 시간 한계 (기본값 유지)
        max.poll.records: 500            # 한 번에 가져올 최대 레코드 수
```

---

### STEP 3 — 멱등성 검증 (Q64, Q67)

**변경 파일**: `OutboxEventRepository`, 각 `*KafkaConsumer.kt`

#### Q64: eventId 기반 중복 처리 방지

```kotlin
// 각 Consumer listen() 메서드에 중복 체크 추가
@KafkaListener(...)
fun listen(event: OutboxEventInfo, ack: Acknowledgment) {
    // 1. 이미 처리된 eventId인지 확인
    val outboxEvent = outboxEventRepository.findById(event.id)
    if (outboxEvent.status == OutboxEventStatus.SUCCESS) {
        logger.warn("DUPLICATE event detected, skipping. eventId=${event.id}")
        ack.acknowledge()  // offset은 전진시킴
        return
    }

    // 2. 비즈니스 로직 처리
    try {
        // ...
        ack.acknowledge()
    } catch (e: Exception) {
        // ...
    }
}
```

- `OutboxEventStatus.SUCCESS` 상태 확인으로 중복 이벤트 무시
- OutboxEvent 테이블이 이미 존재하므로 추가 테이블 불필요
- Q67 Exactly-Once는 이 멱등성 구현으로 완성

---

### STEP 4 — DLQ + 재시도 (Q65)

**변경 파일**: 신규 `KafkaConfig.kt`, `application-local.yml`

#### Q65: 지수 백오프 재시도 + DLQ

```kotlin
// KafkaConfig.kt (신규)
@Configuration
class KafkaConfig {

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, OutboxEventInfo>
    ): ConcurrentKafkaListenerContainerFactory<String, OutboxEventInfo> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, OutboxEventInfo>()
        factory.consumerFactory = consumerFactory
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE

        // 지수 백오프: 1s → 2s → 4s, 최대 3회
        val backOff = ExponentialBackOff(1000L, 2.0).apply {
            maxElapsedTime = 7000L  // 최대 7초 (3회 시도 합산)
        }

        // 재시도 실패 시 DLQ로 라우팅
        val errorHandler = DefaultErrorHandler(
            DeadLetterPublishingRecoverer(kafkaTemplate),
            backOff
        )
        factory.setCommonErrorHandler(errorHandler)

        return factory
    }
}
```

```yaml
# DLQ Topic 명명 규칙: {원본 Topic}.DLT
# Spring Kafka DefaultErrorHandler가 자동 생성
# 예: PAY_HISTORY.DLT, BALANCE_HISTORY.DLT
```

- DLQ에는 원본 메시지 + 실패 원인 헤더 자동 포함
- DLQ Consumer는 별도 운영 도구로 수동 처리 (이번 구현 범위 외)

---

### STEP 5 — Consumer Lag 모니터링 (Q70)

**변경 파일**: `application-local.yml`

현재 `micrometer-registry-prometheus` 의존성과 Actuator 설정이 이미 존재함.
Spring Kafka는 `spring.kafka.listener.observation-enabled` 설정으로 Consumer Lag 메트릭을 자동 수집함.

```yaml
# application-local.yml
spring:
  kafka:
    listener:
      observation-enabled: true  # Micrometer 연동 활성화
```

- `kafka.consumer.fetch.manager.records-lag-max` 메트릭 자동 노출
- Prometheus → Grafana 연동으로 Consumer Lag 시각화 가능

---

## 파일별 변경 범위

| 파일 | 변경 내용 | STEP |
|---|---|---|
| `application-local.yml` | acks, idempotence, manual commit, timeout 튜닝, observation | 1~5 |
| `KafkaProducer.kt` | partitionKey 파라미터 추가 | 1 |
| `OutboxEventInfo.kt` | partitionKey 필드 추가 | 1 |
| 각 Facade/Service (OutboxEvent 생성 지점) | partitionKey에 orderId 주입 | 1 |
| `PaymentKafkaConsumer.kt` | Acknowledgment 추가, 멱등성 체크 | 2, 3 |
| `BalanceKafkaConsumer.kt` | Acknowledgment 추가, 멱등성 체크 | 2, 3 |
| `OrderStockFailKafkaConsumer.kt` | Acknowledgment 추가, 멱등성 체크 | 2, 3 |
| `NotificationKafkaConsumer.kt` | Acknowledgment 추가, 멱등성 체크 | 2, 3 |
| `OrderProductStockKafkaConsumer.kt` | Acknowledgment 추가, 멱등성 체크 | 2, 3 |
| `KafkaConfig.kt` (신규) | DLQ + 지수 백오프 ErrorHandler 설정 | 4 |

---

## 구현 순서 및 근거

```
STEP 1 (Producer 설정)
  → 코드 변경 없이 yml + 작은 코드 수정으로 효과 큰 항목 먼저 처리

STEP 2 (Consumer manual commit 전환)
  → STEP 3 멱등성 구현의 전제 조건 (ack 제어가 있어야 중복 시 offset 관리 가능)

STEP 3 (멱등성 검증)
  → STEP 2 완료 후 적용. Q64 + Q67 동시 해소

STEP 4 (DLQ)
  → 가장 변경 범위가 큰 항목. STEP 2의 ContainerFactory 설정과 통합

STEP 5 (모니터링)
  → yml 한 줄 추가로 완료 가능한 항목
```

---

## 제외 항목

- **Q66 Rebalancing**: 이론 기반 질문으로 실제 장애를 재현할 수 없음. yml 튜닝 설정 추가로 답변 근거 마련
- **DLQ Consumer**: 운영 도구 성격으로 이번 브랜치 범위 외. "수동 재처리 또는 보상 트랜잭션 Consumer가 처리" 답변 유지
- **Grafana 대시보드 구성**: 인프라 레벨 설정으로 코드 레포 범위 외
