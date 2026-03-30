# 기술적 개선 사항

## 개요

프로젝트 전반에 대한 코드 리뷰를 통해 발견된 기술적 이슈 및 개선 사항을 정리한 문서입니다.

---

## P0 - Critical (데이터 정합성 직접 위협)

### 1. PaymentFacade 트랜잭션 누락

**파일**: `payment/usecase/PaymentFacade.kt:24`

**문제**: `pay()` 메서드에 `@Transactional`이 없어, 잔액 차감 → 결제 생성 → 주문 상태 변경이 하나의 트랜잭션으로 묶이지 않습니다. 중간 단계에서 실패 시 부분 커밋되어 데이터 정합성이 깨질 수 있습니다.

**영향**:
- 잔액은 차감되었으나 결제 레코드가 생성되지 않는 경우
- 결제는 생성되었으나 주문 상태가 CONFIRMED로 변경되지 않는 경우

**해결**: `@Transactional` 어노테이션 추가

---

### 2. PaymentFacade 금액 계산 오류

**파일**: `payment/usecase/PaymentFacade.kt:33,43`

**문제**: `order.totalQuantity * order.totalPrice`로 금액을 계산하고 있으나, `totalPrice`는 이미 `OrderCreationCommand.toEntity()`에서 `details.map { it.quantity * it.price }.sum()`으로 수량이 반영된 총가격입니다. 따라서 실제 결제 금액이 `총수량 × 총가격`으로 과다 청구됩니다.

**영향**: 사용자에게 실제보다 훨씬 큰 금액이 청구됨

**해결**: `order.totalQuantity * order.totalPrice` → `order.totalPrice`로 변경

---

### 3. BalanceEntity require 오용

**파일**: `balance/infrastructure/jpa/entity/BalanceEntity.kt:31,37,47`

**문제**: `require` 블록 안에서 커스텀 예외를 throw하고 있으나, `require`의 람다는 `lazyMessage`(메시지 생성 함수)이지 실행 블록이 아닙니다. 조건이 false일 때 `IllegalArgumentException`이 throw되며, 블록 내부의 커스텀 예외(`BalanceLimitExceededException`, `InsufficientBalanceException`)는 실제로 throw되지 않습니다.

```kotlin
// 현재 (잘못된 코드)
require(newBalance <= BalancePolicy.MAX) { throw BalanceLimitExceededException() }

// 올바른 코드
if (newBalance > BalancePolicy.MAX) throw BalanceLimitExceededException()
```

**영향**: 클라이언트에서 예상하는 커스텀 예외 대신 `IllegalArgumentException`이 발생하여 적절한 에러 핸들링이 불가능

**해결**: `require` → `if-throw` 패턴으로 변경

---

## P1 - High (기능 오동작 가능성)

### 4. ApiControllerAdvice 파라미터 타입 불일치

**파일**: `common/advice/ApiControllerAdvice.kt:19`

**문제**: `handleControllerException` 메서드가 `@ExceptionHandler(ControllerException::class)`로 선언되어 있지만, 파라미터 타입이 `ServiceException`입니다.

```kotlin
// 현재
@ExceptionHandler(ControllerException::class)
fun handleControllerException(e: ServiceException): ...

// 올바른 코드
@ExceptionHandler(ControllerException::class)
fun handleControllerException(e: ControllerException): ...
```

**영향**: `ControllerException` 발생 시 파라미터 캐스팅 실패 가능성

**해결**: 파라미터 타입을 `ControllerException`으로 수정

---

### 5. Kafka Consumer 부분 실패 미처리

**파일**: `product/infrastructure/event/OrderProductStockKafkaConsumer.kt:43-66`

**문제**: `forEach`로 재고를 하나씩 차감하는데, 일부 항목에서 `OutOfStockException` 외의 예외가 발생하면 이미 차감된 항목은 롤백되지 않습니다. 또한 `@Transactional`이 없어 각 `decreaseStock` 호출이 독립적으로 커밋됩니다.

**영향**: 부분적 재고 차감으로 인한 데이터 불일치

**해결**: 전체 재고 차감을 하나의 트랜잭션으로 묶거나, 개별 항목별 보상 트랜잭션(Saga) 패턴 적용

---

## P2 - Medium (아키텍처 개선)

### 6. Domain 계층의 Infrastructure 의존

**파일**:
- `balance/domain/BalanceService.kt` → `BalanceHistoryDocument` (MongoDB), `ObjectMapper`, `OutboxEventInfo`
- `product/domain/ProductService.kt` → `RedissonClient`

**문제**: Domain Service가 MongoDB 문서, Kafka 이벤트 DTO, RedissonClient 등 Infrastructure 계층의 구체적 구현에 직접 의존하고 있어 클린 아키텍처 원칙을 위반합니다.

**해결**:
- 이벤트 발행 로직을 Facade/Application 계층으로 이동
- 캐시 제어를 Infrastructure 계층의 별도 서비스로 분리
- Domain 계층에서는 순수 비즈니스 로직만 유지

---

### 7. RedisLockAspect 미사용 필드 및 전략 미분화

**파일**: `common/anotation/aspect/RedisLockAspect.kt`

**문제**:
- `SIMPLE`, `SPIN`, `FAIR`, `PUB_SUB` 모든 전략이 동일한 `pubSubLockSupporter`를 사용
- `redissonClient`, `waitTime`, `releaseTime` 필드가 선언만 되고 사용되지 않음

**해결**: 미사용 필드 제거, 전략 패턴 미구현 시 enum 단순화

---

## P3 - Low (코드 클린업)

### 8. build.gradle.kts 중복 의존성

**파일**: `build.gradle.kts:60-61`

`spring-boot-starter-data-jpa`가 두 번 선언되어 있습니다.

### 9. println 디버깅 코드 잔존

**파일**: `balance/domain/BalanceService.kt:77`

```kotlin
println("잔액 정보 => ${balanceEntity.toString()}")
```

프로덕션 코드에 `println`이 남아 있습니다. `logger`를 사용하거나 제거해야 합니다.

### 10. 패키지 이름 오타

**파일**: `balance/domain/respository/` → `repository/`로 수정 필요

### 11. Outbox 재시도 상한 없음

**파일**: `outboxevent/infrastructure/jpa/entity/OutboxEventEntity.kt`

`retryCnt` 필드가 있지만 최대 재시도 횟수를 검증하는 로직이 없어 무한 재시도 가능성이 있습니다.

### 12. 캐시 전략 혼재

**파일**: `product/domain/ProductService.kt`

`@Cacheable`, `@RedisCacheable`(커스텀), `RedissonClient` 직접 조작이 혼재되어 있어 캐시 전략의 일관성이 부족합니다.
