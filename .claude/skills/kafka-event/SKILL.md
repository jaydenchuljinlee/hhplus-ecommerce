---
name: kafka-event
description: Kafka 이벤트 (Producer/Consumer) 및 Outbox 패턴 구현 시 프로젝트 컨벤션에 맞게 생성
---

# Kafka 이벤트 & Outbox 패턴 컨벤션

Kafka 기반 이벤트를 추가할 때 이 규칙을 따른다.

## 전체 흐름

```
Service → publishEvent(OutboxEventInfo)
  → OutboxEventListener.handleOutboxEvent() [BEFORE_COMMIT: DB 저장, status=INIT]
  → OutboxEventListener.publish()           [AFTER_COMMIT: Kafka 발행, status=PUBLISH]
  → KafkaConsumer.listener()                [수신 처리, status=SUCCESS/FAILED]
```

## 1. Kafka Properties 설정

```kotlin
// application.yml
hhplus:
  kafka:
    {domain}:
      group-id: OUTBOX_{DOMAIN}
      topic: {DOMAIN}_{ACTION}

// Properties 클래스
@ConfigurationProperties(prefix = "hhplus.kafka.{domain}")
data class {Domain}KafkaProperties(
    val groupId: String,
    val topic: String
)
```

## 2. 도메인 이벤트 발행 (Service에서)

```kotlin
// Service 내에서 이벤트 발행
val outboxEvent = OutboxEventInfo(
    id = UUID.randomUUID(),
    groupId = {domain}KafkaProperties.groupId,
    topic = {domain}KafkaProperties.topic,
    payload = objectMapper.writeValueAsString(payload)
)
applicationEventPublisher.publishEvent(outboxEvent)
```

## 3. 도메인 이벤트 → Outbox 변환 (Handler)

도메인 이벤트를 직접 발행하고, Handler에서 OutboxEvent로 변환하는 패턴도 사용:

```kotlin
@Component
class {Domain}OutboxEventHandler(
    private val {domain}KafkaProperties: {Domain}KafkaProperties,
    private val objectMapper: ObjectMapper,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    @EventListener
    fun handle(event: {Domain}ChangedEvent) {
        val document = {Domain}HistoryDocument(/* event 데이터 변환 */)
        val outboxEvent = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = {domain}KafkaProperties.groupId,
            topic = {domain}KafkaProperties.topic,
            payload = objectMapper.writeValueAsString(document)
        )
        applicationEventPublisher.publishEvent(outboxEvent)
    }
}
```

## 4. Kafka Consumer 패턴

```kotlin
@Component
class {Domain}KafkaConsumer(
    private val {domain}HistoryMongoRepository: {Domain}HistoryMongoRepository,
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger({Domain}KafkaConsumer::class.java)

    @KafkaListener(
        groupId = "\${hhplus.kafka.{domain}.group-id}",
        topics = ["\${hhplus.kafka.{domain}.topic}"]
    )
    fun listener(event: OutboxEventInfo) {
        try {
            val payload = objectMapper.readValue(event.payload, {Domain}HistoryDocument::class.java)
            logger.info("{DOMAIN}:KAFKA:CONSUMER: $payload")

            // 처리 로직
            {domain}HistoryMongoRepository.save(payload)

            // Outbox 상태 업데이트
            val outboxEvent = outboxEventRepository.findById(event.id)
            outboxEvent.updateStatus(OutboxEventStatus.SUCCESS)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        } catch(e: Exception) {
            logger.error("{DOMAIN}:KAFKA:CONSUMER:ERROR", e)
            val outboxEvent = outboxEventRepository.findById(event.id)
            outboxEvent.updateStatus(OutboxEventStatus.FAILED)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        }
    }
}
```

## 5. Spring Event Listener 패턴 (비동기)

```kotlin
@Component
class {Domain}SpringEventListener(
    private val {domain}Repository: {Domain}Repository
) {
    @Async
    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun listen(event: {Domain}Document) {
        try {
            logger.info("{DOMAIN}:EVENT:LISTENER: $event")
            {domain}Repository.save(event)
        } catch (e: Exception) {
            logger.error("{DOMAIN}:EVENT:LISTENER:ERROR:${event.id}", e)
        }
    }
}
```

## 핵심 규칙

- Outbox 상태: INIT → PUBLISH → SUCCESS/FAILED
- 재시도 최대 횟수: 3회 (MAX_CNT)
- Consumer에서 반드시 try-catch로 예외 처리
- Consumer에서 반드시 Outbox 상태 업데이트
- 로그 형식: `{DOMAIN}:KAFKA:CONSUMER:{상태}` 또는 `{DOMAIN}:EVENT:LISTENER:{상태}`
