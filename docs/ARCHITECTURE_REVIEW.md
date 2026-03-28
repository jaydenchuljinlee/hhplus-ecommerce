# Architecture & Design Review Checklist

> 프로젝트 구조 및 설계에 대한 보완 체크리스트

---

## 0. 주문~결제 비즈니스 흐름 검토 (Order-to-Payment Business Flow)

### 0-1. 현재 흐름 요약

```
[클라이언트]
  │
  ├─ POST /order ──→ OrderFacade.order()
  │   ├─ 1) 사용자 존재 확인
  │   ├─ 2) 잔액 충분한지 검증 (validateBalanceToUse)
  │   ├─ 3) 주문 생성 (status=REQUESTED)
  │   ├─ 4) Outbox 이벤트 발행 → Kafka → 재고 차감
  │   └─ 응답: 주문 ID (status=REQUESTED)
  │
  │  ⏳ (비동기) OrderProductStockKafkaConsumer
  │   ├─ 상품별 재고 차감 시도
  │   ├─ 성공: 정상 처리
  │   └─ 실패: OrderStockFail 이벤트 → 해당 주문 상세 삭제
  │
  ├─ POST /payment ──→ PaymentFacade.pay()
  │   ├─ 1) 주문 조회 (status=REQUESTED)
  │   ├─ 2) 잔액 차감 (balanceService.use) ← @RedisLock 내부 커밋
  │   ├─ 3) 결제 생성 (paymentService.pay) ← 자체 @Transactional
  │   ├─ 4) 주문 확정 (orderComplete → CONFIRMED)
  │   └─ 응답: 결제 정보
```

### 0-2. 치명적 설계 결함 (Critical)

- [ ] **[BUG] 결제 금액 계산 오류** — `PaymentFacade.pay()`에서 결제 금액을 `order.totalQuantity * order.totalPrice`로 계산함. 그런데 `OrderCreationCommand.toEntity()`에서 `totalPrice = details.map { it.quantity * it.price }.sum()`으로 이미 **총 금액(합계)**을 저장하고 있음. 따라서 현재 로직은 `총수량 × 총금액`이 되어 실제보다 훨씬 큰 금액이 청구됨
  ```
  예시: 상품A(수량2, 단가1000) + 상품B(수량3, 단가2000)
  totalPrice = (2×1000) + (3×2000) = 8000  ← 실제 총 금액
  totalQuantity = 2 + 3 = 5
  결제 금액 = 5 × 8000 = 40,000  ← 잘못된 금액 (정상: 8,000)
  ```
  **수정**: `order.totalPrice`만 사용해야 함

- [ ] **[RACE CONDITION] 결제가 재고 확인 전에 완료될 수 있음** — 주문 생성 후 재고 차감은 Kafka를 통해 **비동기**로 진행되는데, 결제는 주문 상태가 `REQUESTED`이기만 하면 즉시 가능. 클라이언트가 주문 직후 결제를 호출하면 재고가 확보되지 않은 상태에서 결제가 완료됨
  ```
  시나리오:
  t1: POST /order → 주문 생성 (REQUESTED) + 재고 차감 이벤트 발행
  t2: POST /payment → REQUESTED 상태 주문 발견 → 잔액 차감 → 결제 완료 → CONFIRMED
  t3: Kafka Consumer → 재고 부족 발견 → OrderDetail 삭제 시도
      → 그러나 주문은 이미 CONFIRMED, 결제도 완료됨
  ```
  **해결 방향**: 재고 확보 후 결제 가능한 상태로 전환하는 중간 상태(예: `STOCK_CONFIRMED`) 도입, 또는 재고 차감을 동기 처리

- [ ] **[DATA INCONSISTENCY] PaymentFacade에 트랜잭션 경계 없음** — `PaymentFacade.pay()`에 `@Transactional`이 없고, 내부 각 단계가 독립적으로 커밋됨
  ```
  balanceService.use()   → @RedisLock 내부 TransactionTemplate으로 즉시 커밋 ✓
  paymentService.pay()   → 자체 @Transactional로 커밋 ✓
  orderService.orderComplete() → 자체 @Transactional로 커밋 ✓
  ```
  2번에서 실패하면: 잔액만 차감되고 결제 미생성, 주문은 REQUESTED 유지
  3번에서 실패하면: 잔액 차감 + 결제 생성됐지만 주문은 CONFIRMED 안됨
  **해결 방향**: 보상 트랜잭션(Saga) 패턴 도입, 또는 `@RedisLock`의 트랜잭션 전파 전략 재설계

### 0-3. 주요 설계 문제 (Major)

- [ ] **잔액 검증의 실효성 없음 (TOCTOU)** — `OrderFacade`에서 `validateBalanceToUse()`로 잔액을 확인하지만, 실제 차감은 별도 API 호출인 `POST /payment`에서 수행됨. 두 시점 사이에 다른 주문/충전이 발생하여 잔액이 변경될 수 있으므로 이 검증은 의미가 없음. 오히려 잔액이 부족한데도 주문이 생성되어 사용자 혼란을 줄 수 있음
  - 제거하거나, 주문 시 잔액을 "홀드(hold)"하는 메커니즘 도입

- [ ] **재고 차감 실패 시 부분 주문 처리의 모호성** — `OrderProductStockKafkaConsumer`에서 상품을 순회하며 개별 차감하고, 특정 상품이 재고 부족이면 해당 주문 상세만 삭제함. 그러나:
  - 사용자에게 일부 상품이 삭제되었다는 알림 없음
  - 이미 결제가 완료된 상태에서 주문 상세가 삭제되면 결제 금액과 주문 금액이 불일치
  - 모든 상품이 실패하면 `delYn = Y`로 소프트 삭제되지만, 결제는 이미 완료 상태

- [ ] **재고 차감 Consumer에 트랜잭션 없음** — `OrderProductStockKafkaConsumer.listen()`에 `@Transactional`이 없음. `forEach` 루프에서 상품A 차감 성공 후 상품B에서 일반 예외(OutOfStock이 아닌)가 발생하면 상품A의 차감은 이미 커밋된 상태. 부분 차감 상태가 됨

- [ ] **주문 상태 모델의 부족** — 현재 상태: `REQUESTED → CONFIRMED → CANCELED`. 비동기 재고 확인 구조에 비해 상태가 부족함. 다음 상태 추가 고려:
  ```
  REQUESTED → STOCK_CHECKING → STOCK_CONFIRMED → PAYMENT_PENDING → CONFIRMED
                              → PARTIALLY_FAILED (일부 재고 부족)
                              → STOCK_FAILED (전체 재고 부족)
  ```

### 0-4. 구조적 개선 사항 (Improvement)

- [ ] **주문-결제 분리 구조의 정합성 전략 명확화** — 현재 주문(POST /order)과 결제(POST /payment)가 분리된 2-step 구조인데, 이 사이에 발생하는 비동기 재고 차감과의 정합성 보장 전략이 부재. 다음 중 하나를 선택하고 일관되게 적용 필요:
  - **방안 1 (동기 재고 차감)**: 주문 시 재고를 동기적으로 차감하고, 결제 실패 시 보상으로 재고 복구
  - **방안 2 (Saga 패턴)**: 주문→재고확인→결제→확정 각 단계를 이벤트로 연결하고, 실패 시 역방향 보상 이벤트 발행
  - **방안 3 (재고 예약)**: 주문 시 재고를 "예약" 상태로 차감하고, 결제 완료 시 확정, 타임아웃 시 자동 해제

- [ ] **Outbox 이벤트 리스너의 `@EventListener` vs `@TransactionalEventListener` 혼용** — `OutboxEventListener`에서:
  - `handleOutboxEvent()`: `@EventListener` → 트랜잭션 내에서 즉시 실행, Outbox 저장
  - `publish()`: `@TransactionalEventListener(AFTER_COMMIT)` → 커밋 후 Kafka 발행

  문제: 동일한 `OutboxEventInfo` 이벤트에 대해 두 리스너가 모두 호출됨. `@EventListener`가 먼저 호출되어 DB에 저장하고, 커밋 후 `@TransactionalEventListener`이 Kafka로 발행하는 구조는 의도적이나, 이 두 리스너의 실행 순서가 Spring 문서상 보장되지 않으므로 명시적 `@Order` 지정 필요

- [ ] **OrderEntity.removeOf() 예외 안전성** — `filter { it.productId == productId }[0]`은 해당 상품이 없으면 `IndexOutOfBoundsException` 발생. `firstOrNull()` + null 처리 또는 `first()` + 의미 있는 예외로 변경

- [ ] **BalanceEntity.validateToUse()의 검증 기준 혼동** — `validateToUse(amount: Long)`가 `amount >= BalancePolicy.MIN`(100원)을 체크하는데, 여기서 `amount`는 "차감 후 남은 잔액"임. 즉 잔액이 100원 미만이 되면 사용 불가한데, 메서드 이름과 파라미터명이 이를 직관적으로 표현하지 못함. `validateRemainingBalance(remaining: Long)` 등으로 명확화

---

## 1. 보안 (Security)

- [ ] **JWT Secret Key 하드코딩 제거** — `application.yml`에 `jwt.secretKey`가 평문으로 노출되어 있음. 환경변수(`${JWT_SECRET}`)나 Vault 등 시크릿 관리 도구로 분리 필요
- [ ] **Actuator 엔드포인트 제한** — `management.endpoints.web.exposure.include: "*"`로 모든 엔드포인트가 노출됨. `/health`, `/metrics` 등 필요한 것만 화이트리스트 방식으로 제한하고, 인증/IP 제한 적용 필요
- [ ] **DDL Auto 설정 변경** — `hibernate.ddl-auto: create`는 서버 재시작 시 데이터가 유실됨. 운영/개발 프로필별로 `validate` 또는 `update`로 분리하고, 스키마 변경은 Flyway/Liquibase 등 마이그레이션 도구 도입 고려
- [ ] **입력값 검증 (Bean Validation)** — Controller의 Request DTO에 `@Valid`, `@NotNull`, `@Min` 등 Bean Validation 어노테이션 적용 여부 확인. 현재 검증 로직이 서비스 레이어에만 존재할 경우, API 경계에서의 1차 검증 추가 필요

---

## 2. 트랜잭션 & 동시성 설계 (Transaction & Concurrency)

- [ ] **PaymentFacade 트랜잭션 누락** — `PaymentFacade.pay()`에 `@Transactional`이 없음. `balanceService.use()` 성공 후 `paymentService.pay()` 실패 시 잔액만 차감되고 결제는 미완료되는 부분 정합성 문제 발생 가능. 단, `BalanceService.use()`가 `@RedisLock` 내부에서 별도 `TransactionTemplate`으로 커밋하므로, 단순히 `@Transactional`만 추가하면 해결되지 않음. 보상 트랜잭션(Saga) 또는 트랜잭션 범위 재설계 필요
- [ ] **RedisLock 내부 트랜잭션과 외부 트랜잭션 간 경합** — `@RedisLock` Aspect가 `TransactionTemplate`으로 내부 트랜잭션을 관리하는데, 호출자(`OrderFacade`)가 이미 `@Transactional`을 가지고 있으면 트랜잭션 전파 정책에 따라 예상치 못한 동작 가능. 트랜잭션 전파 전략(`REQUIRES_NEW` 등) 명시적 정의 필요
- [ ] **금액 계산 로직 검증** — `PaymentFacade`에서 `order.totalQuantity * order.totalPrice`로 결제 금액을 계산하는데, 이는 주문 항목별 (수량 x 단가)의 합계가 아니라 총수량 x 총가격이 됨. 주문 상세 항목 기반의 정확한 금액 계산 로직 검토 필요
- [ ] **validateBalanceToUse와 실제 차감 사이의 TOCTOU 문제** — `OrderFacade.order()`에서 `balanceService.validateBalanceToUse()` 호출 후 실제 차감은 `PaymentFacade`에서 별도로 수행됨. 검증 시점과 차감 시점 사이에 다른 요청이 잔액을 변경할 수 있음

---

## 3. 아키텍처 & 레이어 분리 (Architecture & Layer Separation)

- [ ] **Domain 레이어의 Infrastructure 의존성 제거** — `BalanceService`(Domain)가 `BalanceHistoryDocument`(MongoDB Infrastructure)를 직접 참조하고, `ObjectMapper`와 `KafkaProperties`에 의존함. 도메인 서비스는 순수한 비즈니스 로직만 포함하고, 이벤트 발행/외부 시스템 연동은 Application 레이어(Facade)나 별도 이벤트 핸들러로 분리 필요
- [ ] **Outbox 이벤트 발행 책임 분리** — 현재 `BalanceService`, `PaymentService` 등 도메인 서비스가 직접 Outbox 이벤트를 구성하고 발행함. 이벤트 구성 로직을 Facade 또는 전용 EventPublisher로 위임하여 단일 책임 원칙 준수
- [ ] **Controller Interface 활용도 확인** — `IXxxController` 인터페이스가 정의되어 있으나, Swagger 어노테이션 분리 용도 외에 실질적 추상화 가치가 있는지 검토. 불필요하면 제거하여 복잡도 감소
- [ ] **DTO 변환 계층 정리** — API DTO → Usecase DTO → Domain DTO 변환이 다단계로 존재. 변환 로직이 과도하게 분산되어 있으면 유지보수 비용 증가. 필요 최소한의 변환 계층으로 정리

---

## 4. 예외 처리 (Exception Handling)

- [ ] **ApiControllerAdvice 파라미터 타입 오류** — `handleControllerException(e: ServiceException)`으로 파라미터 타입이 `ServiceException`으로 되어 있어 `ControllerException`을 받지 못함. `e: ControllerException`으로 수정 필요
- [ ] **예외 계층과 HTTP 상태 코드 매핑 재검토** — `ServiceException`과 `FacadeException`이 동일하게 422 반환. 비즈니스 규칙 위반(잔액 부족 등)과 시스템 오류를 구분하여 적절한 HTTP 상태 코드 사용. 예: 비즈니스 규칙 위반 → 409/422, 인증/인가 → 401/403
- [ ] **글로벌 예외 처리 보강** — `MethodArgumentNotValidException`, `HttpMessageNotReadableException` 등 Spring MVC의 일반적인 예외에 대한 핸들러 추가
- [ ] **예외 메시지에 민감 정보 포함 여부 확인** — 에러 응답에 스택 트레이스나 내부 구현 정보가 노출되지 않도록 프로덕션용 에러 응답 포맷 점검

---

## 5. 이벤트 & 메시징 (Event & Messaging)

- [ ] **Kafka Consumer 멱등성 보장** — `PaymentKafkaConsumer`, `BalanceKafkaConsumer` 등에서 동일 메시지가 중복 수신될 경우 멱등하게 처리되는지 확인. Outbox 이벤트의 `id(UUID)`를 활용한 중복 체크 로직 필요
- [ ] **Kafka Consumer 에러 핸들링** — Consumer에서 예외 발생 시 재시도 정책(retry), Dead Letter Queue(DLQ) 처리 전략 수립. 현재 메시지 처리 실패 시 무한 재시도되거나 유실될 수 있음
- [ ] **Outbox Scheduler 실패 재시도 상한** — `OutboxEventEntity`에 `retryCount`가 존재하나, 최대 재시도 횟수 초과 시 FAILED 처리 및 알림 메커니즘 확인
- [ ] **주석 처리된 도메인 이벤트 로직 정리** — `OrderFacade`에 주석 처리된 캐시 갱신, 장바구니 삭제 로직이 존재. TODO로 남길지, 별도 Kafka Consumer로 구현할지 방향 결정 후 정리

---

## 6. 코드 품질 (Code Quality)

- [ ] **디버그용 `println` 제거** — `BalanceService.use()`에 `println("잔액 정보 => ...")`이 남아 있음. Logger 사용으로 교체하거나 제거
- [ ] **RedisLock 전략 분기 개선** — `RedisLockAspect.around()`의 `when` 분기에서 `SIMPLE`, `SPIN`, `FAIR`, `PUB_SUB` 모두 동일한 `pubSubLockSupporter`를 사용. 전략별 구현이 없다면 불필요한 enum 값 제거, 또는 각 전략에 맞는 구현체 추가
- [ ] **패키지 네이밍 오타 수정** — `anotation` → `annotation`, `schedular` → `scheduler`, `mornitoring` → `monitoring`, `respository` → `repository` 등 오타 수정으로 코드 일관성 확보
- [ ] **사용하지 않는 import 정리** — 사용하지 않는 import 및 주입된 미사용 의존성 정리 (예: `OrderFacade`에서 사용하지 않는 서비스들)

---

## 7. 테스트 (Testing)

- [ ] **도메인 서비스 단위 테스트 보강** — 현재 테스트가 Facade(통합) 및 Controller 중심. `BalanceService`, `OrderService` 등 도메인 서비스의 핵심 비즈니스 로직에 대한 단위 테스트 추가
- [ ] **엣지 케이스 테스트** — 잔액 0원 차감, 재고 0개 상품 주문, 동일 상품 중복 주문 등 경계값 테스트 케이스 추가
- [ ] **Kafka 이벤트 발행/소비 통합 테스트** — Outbox → Kafka Producer → Consumer 전체 흐름의 E2E 테스트. TestContainers(Kafka)가 이미 구성되어 있으므로 활용
- [ ] **예외 시나리오 테스트** — 결제 중 잔액 부족, 재고 부족으로 인한 주문 실패, Kafka 메시지 처리 실패 등 실패 시나리오 테스트
- [ ] **테스트 프로필 DDL 전략** — 테스트용 `application-test.yml`에서도 `ddl-auto: create`인지 확인. 테스트 격리를 위한 전략 점검

---

## 8. 인프라 & 운영 (Infrastructure & Operations)

- [ ] **DB 마이그레이션 도구 도입** — `ddl-auto: create` + `data-locations: sql/database-data.sql` 조합은 개발 단계에서만 적합. Flyway 또는 Liquibase를 통한 스키마 버전 관리 도입
- [ ] **Redis 장애 시 Fallback 전략** — Redis 서버 다운 시 `@RedisLock` 획득 실패로 모든 잔액 관련 API가 차단됨. 서킷 브레이커 또는 DB 비관적 락으로의 폴백 전략 수립
- [ ] **Health Check 구성** — Docker Compose에서 각 서비스의 healthcheck 설정 확인. 의존 서비스(MySQL, Redis, Kafka) 미기동 시 앱 시작 실패에 대한 처리
- [ ] **프로필별 설정 분리 강화** — `application.yml`의 공통 설정과 `application-{profile}.yml`의 프로필별 설정 간 경계 명확화. 특히 `ddl-auto`, 로그 레벨, Actuator 노출 범위 등

---

## 9. API 설계 (API Design)

- [ ] **RESTful 규약 준수 확인** — `GET /balance`에서 Request Body(`BalanceViewRequest`)를 사용하는 경우 REST 규약 위반. Query Parameter나 Path Variable로 변경
- [ ] **API 버전 관리** — `/api/v1/...` 형태의 버전 프리픽스 도입 여부 검토. 향후 API 변경 시 하위 호환성 유지 전략
- [ ] **Pagination 미지원** — 상품 목록, 주문 목록 등에 페이징 처리가 없음. 데이터 증가 시 성능 문제 발생 가능
- [ ] **응답 코드 일관성** — 생성 성공 시 `201 Created`, 삭제 시 `204 No Content` 등 HTTP 상태 코드가 적절히 사용되는지 확인. 현재 `CustomApiResponse`가 항상 200을 반환하는 구조인지 검토

---

## 10. 성능 & 확장성 (Performance & Scalability)

- [ ] **N+1 쿼리 점검** — 주문-주문상세, 장바구니-장바구니상세 등 연관 관계 조회 시 N+1 문제 발생 여부 확인. `@EntityGraph` 또는 `fetch join` 활용
- [ ] **캐시 무효화 전략** — 상품 정보 캐시(`@RedisCacheable`)의 갱신/무효화 시점 확인. 상품 정보 변경 시 캐시가 즉시 반영되는지, stale 데이터 허용 범위는 적절한지
- [ ] **인덱스 전략 검증** — `docs/index/`에 인덱싱 전략이 문서화되어 있으나, 실제 엔티티의 `@Index` 어노테이션과 일치하는지 확인. 특히 조회 빈도가 높은 `userId`, `orderId`, `productId` 필드
- [ ] **Connection Pool 설정** — HikariCP, Redisson, MongoDB 등 커넥션 풀 사이즈가 예상 트래픽에 적합한지 확인. Redisson은 `min-idle=10, max-pool-size=64`로 설정됨

---

## 우선순위 가이드

| 우선순위 | 카테고리 | 항목 |
|---------|---------|------|
| **P0 (즉시)** | 비즈니스 버그 | 결제 금액 계산 오류 (`totalQuantity * totalPrice`) |
| **P0 (즉시)** | 비즈니스 설계 | 재고 미확보 상태에서 결제 가능한 Race Condition |
| **P0 (즉시)** | 비즈니스 설계 | PaymentFacade 트랜잭션 경계 부재로 인한 데이터 불일치 |
| **P0 (즉시)** | 보안 | JWT Secret 하드코딩, Actuator 노출 |
| **P0 (즉시)** | 버그 | ApiControllerAdvice 파라미터 타입 오류 |
| **P1 (단기)** | 비즈니스 설계 | 주문-결제 간 정합성 전략 수립 (Saga/동기 차감/재고 예약 중 택1) |
| **P1 (단기)** | 비즈니스 설계 | 주문 상태 모델 보강, 잔액 검증 TOCTOU 해소 |
| **P1 (단기)** | 비즈니스 설계 | 재고 차감 Consumer 트랜잭션/부분 실패 처리 |
| **P1 (단기)** | 이벤트 | Kafka Consumer 멱등성, 에러 핸들링 |
| **P1 (단기)** | 아키텍처 | Domain 레이어 Infrastructure 의존성 분리 |
| **P1 (단기)** | 코드 품질 | println 제거, 패키지 오타 수정 |
| **P2 (중기)** | 테스트 | 단위 테스트 보강, 예외 시나리오 테스트 |
| **P2 (중기)** | 인프라 | DB 마이그레이션, Redis Fallback |
| **P2 (중기)** | 구조 개선 | Outbox 리스너 실행 순서, OrderEntity 예외 안전성 |
| **P3 (장기)** | API 설계 | 버전 관리, Pagination |
| **P3 (장기)** | 성능 | N+1 점검, Connection Pool 최적화 |
