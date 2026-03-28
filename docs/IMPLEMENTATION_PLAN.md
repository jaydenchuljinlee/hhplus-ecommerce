# Implementation Plan

> ARCHITECTURE_REVIEW.md 기반 단계별 구현 계획서
> 각 Phase는 독립적으로 완료 가능하며, Phase 내 Step은 순서대로 진행

---

## Phase 1. 즉시 수정 (버그 & 코드 품질)

> 비즈니스 로직에 영향 없이 바로 수정 가능한 항목

### Step 1-1. 결제 금액 계산 버그 수정 ✅

`PaymentFacade.pay()`에서 `order.totalQuantity * order.totalPrice` → `order.totalPrice`로 변경

- [x] `PaymentFacade.pay()` 금액 계산 수정 — `BalanceTransaction.amount`, `CreationPaymentCommand.price` 양쪽 수정
- [x] `PaymentFacadeTest` 테스트도 동일 오류 수정 (`totalPrice` 계산식, mock 금액)
- [x] 테스트 통과 확인 (`BUILD SUCCESSFUL`)
- [ ] 금액 계산 검증 단위 테스트 추가 (Step 7-1에서 통합 진행)

**대상 파일**:
- `payment/usecase/PaymentFacade.kt`

---

### Step 1-2. ApiControllerAdvice 파라미터 타입 수정 ✅

`handleControllerException(e: ServiceException)` → `(e: ControllerException)`

- [x] 파라미터 타입 `ServiceException` → `ControllerException` 수정
- [x] logger 포맷 `"ControllerException: $e"` → `"ControllerException: {}", e` 통일 (다른 핸들러와 동일하게)
- [x] 컴파일 확인 (`BUILD SUCCESSFUL`)
- [ ] 각 핸들러가 올바른 예외 타입을 수신하는지 단위 테스트 추가 (Step 7-1에서 통합 진행)

**대상 파일**:
- `common/advice/ApiControllerAdvice.kt`

---

### Step 1-3. 디버그 코드 및 미사용 코드 정리 ✅

- [x] `BalanceService.use()` 내 `println("잔액 정보 => ...")` → `logger.debug("{}", balanceEntity)`로 교체 + `LoggerFactory` 추가
- [x] `OrderFacade`에서 주석 처리된 코드 블록(캐시 갱신, 장바구니 삭제) 삭제
- [x] `OrderFacade`에서 미사용 import 제거 (`CartService`, `CartDeletion`, `ProductIdCartQuery`, `PaymentKafkaProperties`, `ProductService`, `DecreaseProductDetailStock`)
- [x] 컴파일 확인 (`BUILD SUCCESSFUL`)
- [ ] 전체 프로젝트 미사용 import 정리 (IDE 일괄 정리 권장)

**대상 파일**:
- `balance/domain/BalanceService.kt`
- `order/usecase/OrderFacade.kt`

---

### Step 1-4. 패키지 네이밍 오타 수정 〔IDE 리팩토링으로 직접 처리〕

> IntelliJ Shift+F6 Rename 리팩토링으로 진행 — import 자동 갱신

- [ ] `common/anotation/` → `common/annotation/` (7개 파일)
- [ ] `common/schedular/` → `common/scheduler/` (2개 파일)
- [ ] `balance/domain/respository/` → `balance/domain/repository/` (6개 파일)
- [ ] `docs/mornitoring/` → `docs/monitoring/` (디렉토리명)
- [ ] 변경 후 전체 빌드 확인 (`./gradlew build`)

**대상 파일**: 패키지 하위 전체 파일 + 해당 패키지를 import하는 모든 파일

---

### Step 1-5. OrderEntity.removeOf() 예외 안전성 ✅

- [x] `filter { it.productId == productId }[0]` → `firstOrNull() ?: throw OrderDetailNotFoundException(...)` 로 변경
- [x] `OrderDetailNotFoundException` 클래스 신규 추가 (`OrderRepositoryException` 상속으로 기존 예외 계층에 맞춤)
- [x] 컴파일 확인 (`BUILD SUCCESSFUL`)

**대상 파일**:
- `order/infrastructure/jpa/entity/OrderEntity.kt`
- `order/infrastructure/exception/OrderDetailNotFoundException.kt` (신규)

---

### Step 1-6. BalanceEntity 메서드 명확화 ✅

- [x] `validateToUse(amount)` → `validateRemainingBalance(remainingBalance)` 리네이밍
- [x] 파라미터명 `amount` → `remainingBalance`로 변경
- [x] 호출부(`BalanceEntity.use()`, `BalanceService.validateBalanceToUse()`) 일괄 수정
- [x] `validateRemainingBalance()`에 KDoc 주석 추가 (파라미터 의미, BalancePolicy.MIN 기준 명시)
- [x] 에러 메시지에 `BalancePolicy.MIN` 값 직접 포함 (하드코딩 제거)
- [x] 컴파일 확인 (`BUILD SUCCESSFUL`)

**대상 파일**:
- `balance/infrastructure/jpa/entity/BalanceEntity.kt`
- `balance/domain/BalanceService.kt`
- `balance/infrastructure/constants/BalancePolicy.kt`

---

## Phase 2. 주문~결제 비즈니스 흐름 재설계 (핵심)

> 프로젝트에서 가장 중요한 변경. 재고 확인과 결제 사이의 정합성 문제 해결

### Step 2-1. 주문 상태 모델 확장 ✅

현재 `REQUESTED → CONFIRMED → CANCELED`에서 재고 확인 단계를 반영하도록 확장

- [x] `OrderStatus` enum 확장 — `STOCK_CONFIRMED`, `STOCK_FAILED` 추가 (각 상태에 주석 포함)
- [x] `OrderEntity`에 상태 전이 메서드 추가 — `confirmStock()`, `failStock()`, `confirm()`, `cancel()`
- [x] 각 상태 전이에 유효성 검증 추가 — 허용되지 않는 전이 시 `InvalidOrderStatusException` 발생
- [x] `InvalidOrderStatusException` 신규 생성 (`OrderRepositoryException` 상속)
- [x] `OrderService`에 `confirmStock()`, `failStock()` 메서드 추가
- [x] `OrderService.orderComplete()` — `STOCK_CONFIRMED` 조회 + `entity.confirm()` 호출로 변경
- [x] Command DTO 추가 — `OrderStockConfirmCommand`, `OrderStockFailCommand`
- [x] 컴파일 확인 (`BUILD SUCCESSFUL`)

**대상 파일**:
- `order/common/OrderStatus.kt`
- `order/infrastructure/jpa/entity/OrderEntity.kt`

---

### Step 2-2. 재고 차감 Consumer에서 주문 상태 갱신 ✅

재고 차감 완료 시 주문 상태를 `STOCK_CONFIRMED`로 전환하여 결제 가능 상태로 만듦

- [x] 전략 결정: **방안 B (부분 허용)** — 전체 실패 시 `STOCK_FAILED`, 1개 이상 성공 시 실패 상품만 제거 + `STOCK_CONFIRMED`
- [x] `@Transactional` 을 전체 `listen()`에 추가하지 않음 — `decreaseStock()`이 각자 독립 트랜잭션이라 외부 트랜잭션 추가 시 OutOfStockException 발생 시 전체 rollback-only 문제 발생
- [x] 루프 후 `failedCount == totalCount` 분기로 `failStock()` / `confirmStock()` 호출
- [x] `OrderService` 주입 추가, `sendStockFailEvent()` private 메서드로 추출
- [x] `OrderStockFailKafkaConsumer`에 `@Transactional` 추가 + 로그 개선
- [x] 컴파일 확인 (`BUILD SUCCESSFUL`)

**대상 파일**:
- `product/infrastructure/event/OrderProductStockKafkaConsumer.kt`
- `order/domain/OrderService.kt`
- `order/domain/dto/` (필요 시 신규 Command DTO)

---

### Step 2-3. PaymentFacade 결제 조건 변경 ✅

결제는 `STOCK_CONFIRMED` 상태의 주문만 허용하도록 변경

- [x] `PaymentFacade.pay()`에서 주문 조회 조건을 `REQUESTED` → `STOCK_CONFIRMED`로 변경
- [x] 미사용 `@Transactional` import 제거
- [x] `REQUESTED` 상태에서 결제 시도 시 `OrderNotFoundException`(404)으로 자동 거부됨 — 별도 검증 불필요
- [x] `PaymentFacadeTest` — `orderQuery.status`, `orderResult.status` 모두 `STOCK_CONFIRMED`로 업데이트
- [x] 테스트 통과 확인 (`BUILD SUCCESSFUL`)
- [ ] Race Condition 시나리오 동시성 테스트 추가 (Step 7-3에서 통합 진행)

**대상 파일**:
- `payment/usecase/PaymentFacade.kt`

---

### Step 2-4. PaymentFacade 트랜잭션 정합성 확보 ✅

`balanceService.use()` → `paymentService.pay()` → `orderService.orderComplete()` 간 원자성 보장

- [x] 전략 결정: **방안 B (보상 트랜잭션)**
  - 방안 A 불가: `@RedisLock`은 Lock 안에서 커밋하는 구조 — `@Transactional`을 추가하면 Lock 해제 전 커밋 안 됨 → 다른 스레드가 갱신 전 잔액을 읽는 문제 발생
  - 방안 C는 별도 인프라 설계 필요 (장기 과제)
- [x] `balanceService.use()` 이후 try-catch 적용
  - 성공: `paymentService.pay()` → `orderService.orderComplete()` → 반환
  - 실패: `balanceService.charge(refundCommand)`로 잔액 환불 후 예외 재전파
- [x] 환불 자체 실패 시 `[수동 처리 필요]` 에러 로그로 식별 가능
- [x] KDoc으로 트랜잭션 전략 및 단계별 실패 처리 설명 추가
- [x] `paymentService.pay()` 실패 시 잔액 환불 테스트 추가
- [x] `orderService.orderComplete()` 실패 시 잔액 환불 테스트 추가
- [x] 테스트 3개 통과 확인 (`BUILD SUCCESSFUL`)
- [ ] 동시성 환경에서의 정합성 테스트 (Step 7-3에서 통합 진행)

**대상 파일**:
- `payment/usecase/PaymentFacade.kt`
- `common/anotation/aspect/RedisLockAspect.kt` (방안 A 선택 시)
- `balance/domain/BalanceService.kt` (보상 로직 추가 시)

---

### Step 2-5. OrderFacade 잔액 사전 검증 처리 ✅

TOCTOU 문제 해소

- [x] `OrderFacade.order()`에서 `validateBalanceToUse()` 호출 제거
  - 이유: 주문 시점과 결제 시점이 분리되어 있어 검증에 실효성 없음
  - 결제 시점(`PaymentFacade.pay()`)에서 `balanceService.use()` 내 실제 차감 시 검증으로 충분
  - hold 메커니즘은 별도 인프라 설계 필요 → Phase 3 이후 장기 과제로 보류
- [x] `OrderFacade`에서 미사용 `BalanceService` 의존성 제거
- [x] `OrderCreation`에서 미사용 `toBalanceTransaction()` 메서드 및 import 제거
- [x] `BalanceService`에서 호출부 없어진 `validateBalanceToUse()` 메서드 제거
- [x] 컴파일 확인 (`BUILD SUCCESSFUL`)

**대상 파일**:
- `order/usecase/OrderFacade.kt`
- `order/usecase/dto/OrderCreation.kt`
- `balance/domain/BalanceService.kt`

---

## Phase 3. 이벤트 & 메시징 안정화

### Step 3-1. Outbox 이벤트 리스너 실행 순서 보장 ✅

- [x] `OutboxEventListener.handleOutboxEvent()`에 `@Order(1)` 추가
- [x] `OutboxEventListener.publish()`에 `@Order(2)` 추가
- [x] `@EventListener` → `@TransactionalEventListener(phase = BEFORE_COMMIT)` 변경
  - 트랜잭션 안에서만 동작하도록 제한 → 외부 트랜잭션과 동일한 커밋/롤백 단위로 묶임
  - `@EventListener`는 트랜잭션 유무 관계없이 즉시 실행되어 정합성 보장 불가
- [x] `@Async publish()`에 `@Transactional(propagation = REQUIRES_NEW)` 추가
  - 비동기 별도 스레드에는 트랜잭션 컨텍스트 없음 → `updateStatus()` JPA 연산을 위한 신규 트랜잭션 시작 필요
- [x] SLF4J 로거 포맷 통일 (`"$event"` 문자열 보간 → `"{}", event`)
- [x] 트랜잭션 없이 이벤트 발행되는 경우(Kafka Consumer 내부) 확인
  - `OrderProductStockKafkaConsumer.sendStockFailEvent()`는 `applicationEventPublisher` 대신 `outboxEventService.insertOrUpdate()` + `kafkaProducer.sendOutboxEvent()`를 직접 호출하므로 영향 없음
- [x] KDoc으로 단계별 처리 이유 명시
- [x] 컴파일 확인 (`BUILD SUCCESSFUL`)

**대상 파일**:
- `outboxevent/infrastructure/event/OutboxEventListener.kt`

---

### Step 3-2. Kafka Consumer 멱등성 보장 ✅

- [x] `PaymentKafkaConsumer` 멱등성 적용
  - 진입 시 outboxEvent 1회 조회 후 SUCCESS면 스킵, entity 재사용으로 중복 조회 제거
  - SLF4J 로거 포맷 통일
- [x] `BalanceKafkaConsumer` 멱등성 적용
  - 동일 패턴 적용, entity 재사용
  - SLF4J 로거 포맷 통일
- [x] `OrderStockFailKafkaConsumer` 멱등성 적용
  - 성공 시 SUCCESS 상태 업데이트 누락 버그 수정
  - SLF4J 로거 포맷 통일
- [x] `OrderProductStockKafkaConsumer` 멱등성 적용
  - `OutboxEventRepository` 주입 추가 (기존에는 미주입)
  - 처리 완료 후 SUCCESS 업데이트 누락 버그 수정
  - 전체 try-catch로 감싸고 FAILED 상태 업데이트 추가
  - SLF4J 로거 포맷 통일 (private 메서드 포함)
- [x] 컴파일 확인 (`BUILD SUCCESSFUL`)
- [ ] 동일 메시지 중복 수신 시나리오 테스트 (Step 7-3에서 통합 진행)

**대상 파일**:
- `payment/infrastructure/event/PaymentKafkaConsumer.kt`
- `balance/infrastructure/event/BalanceKafkaConsumer.kt`
- `product/infrastructure/event/OrderProductStockKafkaConsumer.kt`
- `order/infrastructure/event/OrderStockFailKafkaConsumer.kt`

---

### Step 3-3. Kafka Consumer 에러 핸들링 & DLQ ✅

- [x] `KafkaConsumerConfig.kt` 신규 생성
  - `ConcurrentKafkaListenerContainerFactory` 커스텀 빈 등록 (Spring Boot 기본 팩토리 대체)
  - `DefaultErrorHandler` with `ExponentialBackOff` 설정
- [x] 재시도 정책 정의 — ExponentialBackOff
  - 초기 대기: 2초, 배수: 2.0 (2s → 4s → 8s)
  - 최대 단일 간격: 10초, 최대 경과 시간: 20초 (약 3회 재시도)
- [x] DLT 라우팅 설정 — `DeadLetterPublishingRecoverer`
  - 네이밍 규칙: `{원본 토픽}.DLT` (예: `PAY_HISTORY.DLT`, `BALANCE_HISTORY.DLT`)
  - 파티션: -1 (Kafka 프로듀서 기본 라우팅)
  - 라우팅 시 WARN 로그로 식별 가능 (topic, key, dltTopic, error 포함)
- [x] KDoc으로 재시도 정책 및 DLT 토픽 목록 명시
- [x] 컴파일 확인 (`BUILD SUCCESSFUL`)
- [ ] DLT Consumer 구현 (DLT 메시지 재처리/알림) — 장기 과제
- [ ] 재시도 초과 시나리오 통합 테스트 (Step 7-3에서 통합 진행)

**대상 파일**:
- `common/config/KafkaConsumerConfig.kt` (신규)

---

### Step 3-4. Outbox Scheduler 재시도 상한 처리 ✅

- [x] `IOutboxEventRepository`에 `findExhaustedEvents()` 추가 (재시도 상한 초과 이벤트 조회)
- [x] `OutboxEventJpaRepository`에 `retryCnt > maxCnt` JPQL 쿼리 추가
- [x] `OutboxEventRepository`에 `findExhaustedEvents()` 구현 추가
- [x] `OutboxEventService.processFailedOutbox()` — 재시도 가능/불가 이벤트 분리 처리
  - 재시도 가능 (retryCnt ≤ MAX_CNT): retryCnt 증가 + Kafka 재발행
  - 상한 초과 (retryCnt > MAX_CNT): `[수동 처리 필요]` WARN 로그 기록
- [x] 스케줄러 버그 수정 (토픽이 서로 교체되어 있던 오류)
  - `BalanceOutboxScheduler`: `PAY_HISTORY` → `BALANCE_HISTORY`, 메서드명 수정
  - `PaymentOutboxScheduler`: `BALANCE_HISTORY` → `PAY_HISTORY`, 미사용 `KafkaProducer` 의존성 제거, 메서드명 수정
- [x] 누락 스케줄러 신규 추가
  - `ProductStockOutboxScheduler` — `PRODCUT_STOCK` 토픽 재시도
  - `OrderOutboxScheduler` — `ORDER_STOCK_FAIL` 토픽 재시도
- [x] 컴파일 확인 (`BUILD SUCCESSFUL`)
- [ ] WARN 로그 기반 Prometheus Alert 설정 — 장기 과제 (모니터링 인프라 필요)

**대상 파일**:
- `outboxevent/domain/IOutboxEventRepository.kt`
- `outboxevent/infrastructure/jpa/OutboxEventJpaRepository.kt`
- `outboxevent/infrastructure/OutboxEventRepository.kt`
- `outboxevent/domain/OutboxEventService.kt`
- `common/schedular/BalanceOutboxScheduler.kt`
- `common/schedular/PaymentOutboxScheduler.kt`
- `common/schedular/ProductStockOutboxScheduler.kt` (신규)
- `common/schedular/OrderOutboxScheduler.kt` (신규)

---

## Phase 4. 아키텍처 레이어 분리

### Step 4-1. Domain 서비스에서 Infrastructure 의존성 제거 ✅

Domain 서비스가 순수 비즈니스 로직만 담당하도록 Outbox 이벤트 발행 로직을 분리

- [x] DIP 패턴 적용: Domain 인터페이스 정의, Infrastructure 구현체 분리
  - `@RedisLock` 내부 `TransactionTemplate` 트랜잭션 안에서 이벤트가 발행되어야 하므로
    Facade 분리 대신 Service 내에서 인터페이스 호출 방식 선택
    (Facade 방식은 트랜잭션 커밋 후 호출되어 `@TransactionalEventListener(BEFORE_COMMIT)` 미동작)
- [x] `balance/domain/event/IBalanceEventPublisher.kt` 신규 생성 (Domain 인터페이스)
- [x] `balance/infrastructure/event/BalanceEventPublisher.kt` 신규 생성 (Infrastructure 구현체)
  - `BalanceHistoryDocument`, `OutboxEventInfo` 생성 및 `ApplicationEventPublisher` 발행 담당
  - `TransactionType` enum 내부 정의로 "CHARGE"/"USE" 하드코딩 제거
- [x] `BalanceService` 인프라 의존성 제거: `ObjectMapper`, `BalanceKafkaProperties`, `ApplicationEventPublisher`, `BalanceHistoryDocument`, `OutboxEventInfo`
- [x] `payment/domain/event/IPaymentEventPublisher.kt` 신규 생성 (Domain 인터페이스)
- [x] `payment/infrastructure/event/PaymentEventPublisher.kt` 신규 생성 (Infrastructure 구현체)
- [x] `PaymentService` 인프라 의존성 제거: `ObjectMapper`, `PaymentKafkaProperties`, `ApplicationEventPublisher`, `PaymentHistoryDocument`, `OutboxEventInfo`
- [x] 기존 테스트 영향 없음 확인 (`@Mock` 방식이라 생성자 변경 무관)
- [x] 컴파일 확인 (`BUILD SUCCESSFUL`)

**대상 파일**:
- `balance/domain/BalanceService.kt`
- `balance/domain/event/IBalanceEventPublisher.kt` (신규)
- `balance/infrastructure/event/BalanceEventPublisher.kt` (신규)
- `payment/domain/PaymentService.kt`
- `payment/domain/event/IPaymentEventPublisher.kt` (신규)
- `payment/infrastructure/event/PaymentEventPublisher.kt` (신규)

---

### Step 4-2. DTO 변환 계층 정리

- [ ] 각 도메인별 DTO 계층 현황 정리 (API DTO → Usecase DTO → Domain DTO)
- [ ] 불필요한 중간 DTO 식별 및 병합
  - API DTO와 Usecase DTO가 동일 구조인 경우 통합
  - 변환 메서드가 단순 필드 복사인 경우 통합 검토
- [ ] 변환 책임 일관성 확보 (companion object `from()` 또는 확장 함수 통일)
- [ ] 빌드 확인

**대상 파일**: 각 도메인의 `api/dto/`, `usecase/dto/`, `domain/dto/`

---

## Phase 5. 보안 강화

### Step 5-1. JWT Secret Key 외부화

- [ ] `application.yml`에서 하드코딩된 secretKey를 환경변수 참조로 변경
  ```yaml
  jwt:
    secretKey: ${JWT_SECRET_KEY:default-dev-only-secret-key}
  ```
- [ ] `.env.example` 파일 생성 (환경변수 템플릿)
- [ ] Docker Compose에 환경변수 전달 설정 추가
- [ ] 기존 JWT 관련 테스트가 테스트용 키로 동작하는지 확인

**대상 파일**:
- `src/main/resources/application.yml`
- `docker-compose.yml`
- 프로젝트 루트: `.env.example` (신규)

---

### Step 5-2. Actuator 엔드포인트 제한

- [ ] 노출 엔드포인트를 필요한 것만 화이트리스트로 제한
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: health,metrics,prometheus
  ```
- [ ] 프로필별 차등 적용 (dev: 전체, prod: 제한)
- [ ] `/actuator` 경로에 접근 제한 설정 (IP 또는 인증)

**대상 파일**:
- `src/main/resources/application.yml`
- `src/main/resources/application-local.yml`
- `src/main/resources/application-dev.yml`

---

### Step 5-3. 입력값 검증 (Bean Validation) 추가

- [ ] `build.gradle.kts`에 `spring-boot-starter-validation` 의존성 확인/추가
- [ ] Request DTO에 Bean Validation 어노테이션 추가
  ```kotlin
  data class OrderCreationRequest(
      @field:NotNull @field:Min(1) val userId: Long,
      @field:NotEmpty val details: List<DetailCreationRequest>
  )
  ```
- [ ] Controller 메서드 파라미터에 `@Valid` 추가
- [ ] `ApiControllerAdvice`에 `MethodArgumentNotValidException` 핸들러 추가
- [ ] 잘못된 입력값으로 API 호출 시 적절한 400 응답 반환 테스트

**대상 파일**:
- `build.gradle.kts`
- `order/api/dto/OrderCreationRequest.kt`
- `payment/api/dto/PaymentRequest.kt`
- `balance/api/dto/` (관련 Request DTOs)
- `cart/api/dto/` (관련 Request DTOs)
- `common/advice/ApiControllerAdvice.kt`

---

## Phase 6. 예외 처리 보강

### Step 6-1. 글로벌 예외 핸들러 보강

- [ ] `ApiControllerAdvice`에 다음 핸들러 추가:
  - `MethodArgumentNotValidException` → 400 (검증 실패 상세 메시지 포함)
  - `HttpMessageNotReadableException` → 400 (잘못된 JSON 포맷)
  - `MethodArgumentTypeMismatchException` → 400 (타입 불일치)
  - `NoHandlerFoundException` → 404
- [ ] 프로덕션 환경에서 스택 트레이스가 응답에 포함되지 않도록 확인
- [ ] 에러 응답 포맷 일관성 확보 (모두 `CustomErrorResponse` 사용)

**대상 파일**:
- `common/advice/ApiControllerAdvice.kt`

---

### Step 6-2. 예외 계층 HTTP 상태 코드 재매핑

- [ ] 현재 매핑 검토:
  | 예외 | 현재 | 개선안 |
  |------|------|--------|
  | ControllerException | 400 | 400 (유지) |
  | ServiceException | 422 | 422 (유지) |
  | FacadeException | 422 | 409 Conflict (비즈니스 규칙 충돌) |
  | RepositoryException | 500 | 500 (유지) |
  | InsufficientBalanceException | 422 (상위) | 409 Conflict |
  | OutOfStockException | 422 (상위) | 409 Conflict |
  | NotFoundException 계열 | 500 (상위) | 404 Not Found |
- [ ] NotFoundException 계열(`OrderNotFoundException`, `UserNotFoundException` 등)에 대한 전용 핸들러 추가 → 404
- [ ] 변경 후 기존 테스트 업데이트

**대상 파일**:
- `common/advice/ApiControllerAdvice.kt`
- 각 도메인 `infrastructure/exception/` 하위 예외 클래스들

---

## Phase 7. 테스트 보강

### Step 7-1. 도메인 서비스 단위 테스트

- [ ] `BalanceService` 단위 테스트
  - 충전 성공 / 충전 한도 초과 / 사용 성공 / 잔액 부족
- [ ] `OrderService` 단위 테스트
  - 주문 생성 / 주문 상태 전이 / 주문 상세 삭제
- [ ] `PaymentService` 단위 테스트
  - 결제 생성 / 결제 상태 확인
- [ ] `ProductService` 단위 테스트
  - 재고 차감 / 재고 부족 시 예외

**대상 파일**: `src/test/kotlin/com/hhplus/ecommerce/` 하위 각 도메인 테스트

---

### Step 7-2. 엣지 케이스 & 경계값 테스트

- [ ] 잔액 정확히 100원(MIN)일 때 사용 시도
- [ ] 잔액 정확히 MAX 금액일 때 충전 시도
- [ ] 재고 0개 상품 주문
- [ ] 동일 상품을 여러 개 주문 상세에 중복 포함
- [ ] 주문 상세가 빈 리스트인 주문 생성 시도
- [ ] 존재하지 않는 사용자/주문/상품 ID로 요청

---

### Step 7-3. 주문~결제 통합 시나리오 테스트

- [ ] 정상 흐름: 주문 → 재고 확인 → 결제 → 확정
- [ ] 재고 부족: 주문 → 재고 실패 → 결제 불가
- [ ] 잔액 부족: 주문 → 재고 확인 → 결제 실패 (잔액 부족)
- [ ] Race Condition: 재고 확인 전 결제 시도 → 거부
- [ ] 동시 결제: 동일 주문에 대한 동시 결제 요청 → 하나만 성공

---

### Step 7-4. Kafka 이벤트 E2E 테스트

- [ ] Outbox 저장 → Kafka 발행 → Consumer 수신 → 처리 완료 전체 흐름
- [ ] Kafka Consumer 메시지 중복 수신 시 멱등성 검증
- [ ] Consumer 예외 발생 시 DLQ 라우팅 확인
- [ ] TestContainers(Kafka) 활용

---

## Phase 8. 인프라 & 운영 안정화

### Step 8-1. DDL 전략 프로필별 분리

- [ ] `application.yml` (공통): `ddl-auto` 설정 제거
- [ ] `application-local.yml`: `ddl-auto: create` (로컬 개발용)
- [ ] `application-dev.yml`: `ddl-auto: update`
- [ ] `application-test.yml`: `ddl-auto: create` (테스트 격리)
- [ ] 프로덕션 프로필 추가: `application-prod.yml` → `ddl-auto: validate`
- [ ] Flyway/Liquibase 도입은 별도 이슈로 관리 (장기)

**대상 파일**:
- `src/main/resources/application.yml`
- `src/main/resources/application-local.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-test.yml`
- `src/main/resources/application-prod.yml` (신규)

---

### Step 8-2. Redis 장애 Fallback 전략

- [ ] `RedisLockAspect`에서 Redis 연결 실패 시 처리 전략 구현
  ```kotlin
  // 방안: DB 비관적 락으로 폴백
  return try {
      lockSupporter.withLock(key) { ... }
  } catch (e: RedisConnectionFailureException) {
      logger.warn("Redis 연결 실패, DB Lock으로 폴백: $key")
      dbLockFallback(joinPoint)
  }
  ```
- [ ] 또는 서킷 브레이커 패턴 적용 (Resilience4j)
- [ ] Redis 장애 시나리오 테스트 (TestContainers에서 Redis 중단 시뮬레이션)

**대상 파일**:
- `common/anotation/aspect/RedisLockAspect.kt`
- `common/config/` (Resilience4j 설정 시)

---

### Step 8-3. Docker Compose Health Check

- [ ] MySQL healthcheck 추가
  ```yaml
  healthcheck:
    test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
    interval: 10s
    retries: 5
  ```
- [ ] Redis healthcheck 추가
- [ ] Kafka healthcheck 추가
- [ ] Spring App의 `depends_on`에 `condition: service_healthy` 설정

**대상 파일**:
- `docker-compose.yml`

---

## Phase 9. API 설계 개선

### Step 9-1. RESTful 규약 준수

- [ ] `GET /balance` → Request Body 대신 Query Parameter 사용
  ```kotlin
  @GetMapping("/balance")
  fun getBalance(@RequestParam userId: Long): CustomApiResponse<BalanceViewResponse>
  ```
- [ ] 응답 HTTP 상태 코드 정리:
  - POST 생성 성공: `201 Created`
  - DELETE 성공: `204 No Content`
  - 조회 성공: `200 OK` (유지)

**대상 파일**:
- `balance/api/BalanceController.kt`
- `balance/api/dto/BalanceViewRequest.kt`
- 각 Controller의 응답 상태 코드

---

### Step 9-2. RedisLock 전략 enum 정리

- [ ] 현재 모든 전략이 동일한 `pubSubLockSupporter`를 사용하므로:
  - 사용하지 않는 전략 enum 값 제거 (SIMPLE, SPIN, FAIR)
  - 또는 각 전략에 맞는 `IRedisLockSupporter` 구현체 추가
- [ ] `@RedisLock` 어노테이션에서 `strategy` 파라미터 제거 또는 유지 결정
- [ ] 빌드 확인

**대상 파일**:
- `common/anotation/aspect/enums/RedisLockStrategy.kt`
- `common/anotation/aspect/RedisLockAspect.kt`
- `common/anotation/RedisLock.kt`

---

## 진행 상태 요약

| Phase | 설명 | 상태 |
|-------|------|------|
| **Phase 1** | 즉시 수정 (버그 & 코드 품질) | [ ] 미착수 |
| **Phase 2** | 주문~결제 비즈니스 흐름 재설계 | [ ] 미착수 |
| **Phase 3** | 이벤트 & 메시징 안정화 | [ ] 미착수 |
| **Phase 4** | 아키텍처 레이어 분리 | [ ] 미착수 |
| **Phase 5** | 보안 강화 | [ ] 미착수 |
| **Phase 6** | 예외 처리 보강 | [ ] 미착수 |
| **Phase 7** | 테스트 보강 | [ ] 미착수 |
| **Phase 8** | 인프라 & 운영 안정화 | [ ] 미착수 |
| **Phase 9** | API 설계 개선 | [ ] 미착수 |
