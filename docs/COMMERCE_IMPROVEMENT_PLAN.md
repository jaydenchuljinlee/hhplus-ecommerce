# 커머스 보완 구현 계획

> 분석 기준: 현재 구조 vs 빅테크 커머스 갭 분석 결과
> 구현 순서: P0 → P1 → P2 → P3

---

## 진행 상태 요약

| ID | 항목 | 우선순위 | 상태 |
|----|------|---------|------|
| P0-1 | 재고 Soft Reserve (예약재고) | P0 | ⬜ 미착수 |
| P0-2 | PaymentSaga 상태 추적 | P0 | ⬜ 미착수 |
| P1-1 | 쿠폰 도메인 (발급/사용) | P1 | ⬜ 미착수 |
| P1-2 | 배송 도메인 + 주문 상태 확장 | P1 | ⬜ 미착수 |
| P1-3 | 알림 도메인 (Kafka 연동) | P1 | ⬜ 미착수 |
| P2-1 | 사용자 등급 + 주소록 | P2 | ⬜ 미착수 |
| P2-2 | 복합 결제 지원 (잔액+포인트) | P2 | ⬜ 미착수 |
| P3-1 | API 버저닝 (/api/v1/) | P3 | ⬜ 미착수 |
| P3-2 | 이벤트 스키마 버저닝 | P3 | ⬜ 미착수 |
| P3-3 | Rate Limiting | P3 | ⬜ 미착수 |

---

## P0-1. 재고 Soft Reserve (예약재고)

### 문제

주문 생성 시점에 재고를 점유하지 않아 동시 주문 시 초과 판매(oversell) 가능.
현재 흐름: `OrderFacade.order()` → Kafka 이벤트 → `OrderProductStockKafkaConsumer` → `decreaseStock()`.
주문 생성과 재고 차감 사이에 시간 갭이 존재하여 동일 재고를 여러 주문이 가져갈 수 있다.

### 현재 코드 분석

- `ProductDetailEntity.kt`: `quantity` 필드만 존재, `decrease()` 메서드로 직접 차감
- `OrderProductStockKafkaConsumer.kt`: Kafka 메시지 수신 후 `productService.decreaseStock()` 호출
- `PaymentFacade.kt`: 결제 완료 시 재고 관련 처리 없음

### 구현 상세

#### 1. ProductDetailEntity 확장

```kotlin
// product/infrastructure/jpa/entity/ProductDetailEntity.kt
- reservedQuantity: Int = 0 필드 추가
- availableQuantity: Int get() = quantity - reservedQuantity (계산 프로퍼티)
- reserve(amount: Int): availableQuantity 검증 후 reservedQuantity += amount
- commit(amount: Int): reservedQuantity -= amount, quantity -= amount (실제 차감)
- release(amount: Int): reservedQuantity -= amount (예약 해제, 재고 복원)
```

#### 2. StockReservation 엔티티 (신규)

```kotlin
// product/infrastructure/jpa/entity/StockReservationEntity.kt
@Entity @Table(name = "stock_reservation")
class StockReservationEntity(
    id: Long,
    orderId: Long,
    productDetailId: Long,
    quantity: Int,
    status: StockReservationStatus,  // RESERVED / COMMITTED / RELEASED / EXPIRED
    expiredAt: LocalDateTime,        // 생성시각 + 30분
    createdAt: LocalDateTime
)
```

#### 3. 서비스 계층

```kotlin
// product/domain/StockReservationService.kt
class StockReservationService(
    private val productRepository: IProductRepository,
    private val stockReservationRepository: IStockReservationRepository
) {
    fun reserve(orderId: Long, productDetailId: Long, quantity: Int): StockReservationEntity
    fun commit(orderId: Long): Unit           // 결제 완료 시 호출
    fun release(orderId: Long): Unit          // 주문 취소 시 호출
    fun expireOverdue(): Int                  // 만료된 예약 정리
}
```

#### 4. 흐름 변경

**Before**: 주문 → (Kafka) → 재고 차감
**After**:
1. 주문 → (Kafka) → `reserve()` (예약재고 점유)
2. 결제 완료 → `commit()` (실제 차감 확정)
3. 결제 실패/타임아웃 → `release()` (예약 해제)
4. 스케줄러 → 만료된 예약 자동 `release()`

#### 5. 대상 파일

| 파일 | 작업 |
|------|------|
| `product/infrastructure/jpa/entity/ProductDetailEntity.kt` | `reservedQuantity`, `reserve()`, `commit()`, `release()` 추가 |
| `product/infrastructure/jpa/entity/StockReservationEntity.kt` | **신규** |
| `product/domain/StockReservationService.kt` | **신규** |
| `product/domain/repository/IStockReservationRepository.kt` | **신규** |
| `product/infrastructure/StockReservationRepository.kt` | **신규** |
| `product/infrastructure/jpa/StockReservationJpaRepository.kt` | **신규** |
| `product/infrastructure/event/OrderProductStockKafkaConsumer.kt` | `decreaseStock()` → `reserve()` 변경 |
| `payment/usecase/PaymentFacade.kt` | 결제 완료 시 `commit()` 호출 추가 |
| `common/schedular/StockReservationExpiryScheduler.kt` | **신규** - 매 5분 만료 예약 정리 |

---

## P0-2. PaymentSaga 상태 추적

### 문제

`PaymentFacade.pay()`에서 잔액 차감 → 결제 생성 → 주문 확인 흐름 중 실패 시 보상 트랜잭션이 없다.
예: 잔액 차감 후 결제 생성 실패 → 잔액 영구 손실.
현재 `@Transactional`이 있지만, 외부 서비스 호출이나 Kafka 이벤트와의 정합성은 보장 불가.

### 현재 코드 분석

```kotlin
// PaymentFacade.kt - 현재 흐름
1. orderService.getOrder(REQUESTED)
2. balanceService.use()        // 잔액 차감
3. paymentService.pay()        // 결제 생성 (PAID)
4. orderService.orderComplete() // 주문 CONFIRMED
// → 3 실패 시 2는 이미 커밋 (RedisLock이 별도 트랜잭션)
```

### 구현 상세

#### 1. PaymentSaga 엔티티

```kotlin
// payment/infrastructure/jpa/entity/PaymentSagaEntity.kt
@Entity @Table(name = "payment_saga")
class PaymentSagaEntity(
    id: Long,
    orderId: Long,
    userId: Long,
    sagaStatus: PaymentSagaStatus,
    balanceTransactionId: Long? = null,
    paymentId: Long? = null,
    failReason: String? = null,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime
)
```

#### 2. PaymentSagaStatus enum

```kotlin
enum class PaymentSagaStatus {
    STARTED,              // Saga 시작
    BALANCE_DEDUCTED,     // 잔액 차감 완료
    PAYMENT_CREATED,      // 결제 레코드 생성 완료
    ORDER_CONFIRMED,      // 주문 상태 CONFIRMED
    STOCK_COMMITTED,      // 재고 확정 (P0-1 연동)
    COMPENSATING,         // 보상 트랜잭션 진행 중
    COMPENSATION_FAILED,  // 보상 실패 (수동 처리 필요)
    COMPLETED,            // 전체 완료
    FAILED                // 실패
}
```

#### 3. PaymentFacade 리팩터링

```kotlin
// 변경 후 흐름
fun pay(dto: PaymentCreation): PaymentInfo {
    // 1. Saga 시작
    val saga = sagaRepository.save(PaymentSagaEntity(STARTED))

    try {
        // 2. 잔액 차감
        balanceService.use(...)
        saga.update(BALANCE_DEDUCTED)

        // 3. 결제 생성
        val payment = paymentService.pay(...)
        saga.update(PAYMENT_CREATED, paymentId = payment.id)

        // 4. 주문 확인
        orderService.orderComplete(...)
        saga.update(ORDER_CONFIRMED)

        // 5. 재고 확정 (P0-1)
        stockReservationService.commit(orderId)
        saga.update(STOCK_COMMITTED)

        saga.update(COMPLETED)
    } catch (e: Exception) {
        compensate(saga, e)  // 보상 트랜잭션
    }
}

private fun compensate(saga: PaymentSagaEntity, cause: Exception) {
    saga.update(COMPENSATING, failReason = cause.message)
    try {
        when (saga.sagaStatus) {
            STOCK_COMMITTED -> { /* 재고 release */ }
            ORDER_CONFIRMED -> { /* 주문 취소 + 재고 release */ }
            PAYMENT_CREATED -> { /* 결제 취소 + 주문 취소 */ }
            BALANCE_DEDUCTED -> { /* 잔액 환불 */ }
        }
        saga.update(FAILED)
    } catch (compensationError: Exception) {
        saga.update(COMPENSATION_FAILED)
        logger.warn("SAGA 보상 실패: sagaId=${saga.id}, cause=$compensationError")
    }
}
```

#### 4. Recovery 스케줄러

```kotlin
// common/schedular/PaymentSagaRecoveryScheduler.kt
@Scheduled(cron = "0 */5 * * * *")  // 매 5분
fun recoverFailedSagas() {
    val failedSagas = sagaRepository.findByStatus(COMPENSATION_FAILED)
    failedSagas.forEach {
        logger.warn("수동 처리 필요: sagaId=${it.id}, orderId=${it.orderId}")
    }
}
```

#### 5. 대상 파일

| 파일 | 작업 |
|------|------|
| `payment/infrastructure/jpa/entity/PaymentSagaEntity.kt` | **신규** |
| `payment/common/PaymentSagaStatus.kt` | **신규** |
| `payment/domain/repository/IPaymentSagaRepository.kt` | **신규** |
| `payment/infrastructure/PaymentSagaRepository.kt` | **신규** |
| `payment/infrastructure/jpa/PaymentSagaJpaRepository.kt` | **신규** |
| `payment/usecase/PaymentFacade.kt` | Saga 패턴으로 리팩터링 |
| `common/schedular/PaymentSagaRecoveryScheduler.kt` | **신규** |

---

## P1-1. 쿠폰 도메인 (발급/사용)

### 문제

쿠폰/할인 기능이 완전히 부재하여 프로모션 운영 불가.

### 구현 상세

#### 1. 엔티티 설계

```kotlin
// CouponPolicyEntity: 쿠폰 정책 (관리자 생성)
- id, name, type(CouponType), discountValue, minOrderAmount
- maxDiscountAmount, totalQuantity, issuedQuantity
- startAt, expiredAt, delYn

// CouponType enum: FIXED(정액) / RATE(정률) / FREE_SHIPPING(무료배송)

// UserCouponEntity: 사용자별 쿠폰 발급 이력
- id, userId, couponPolicyId, status(UserCouponStatus)
- issuedAt, usedAt, orderId(nullable)

// UserCouponStatus enum: ISSUED / USED / EXPIRED
```

#### 2. CouponService

```kotlin
@RedisLock(key = "'coupon:' + #couponPolicyId")
fun issue(userId: Long, couponPolicyId: Long): UserCouponEntity
// - 선착순 발급 (totalQuantity > issuedQuantity 검증)
// - 중복 발급 방지 (userId + couponPolicyId unique)

fun use(userCouponId: Long, orderId: Long): Long  // 할인 금액 반환
fun cancel(userCouponId: Long): Unit               // 주문 취소 시 쿠폰 복원
fun calculateDiscount(policy: CouponPolicyEntity, orderAmount: Long): Long
```

#### 3. API

```
POST /coupon/{couponPolicyId}/issue  - 쿠폰 발급
GET  /coupon/my                      - 내 쿠폰 목록
```

#### 4. 대상 패키지

`coupon/` 전체 신규: `api/`, `domain/`, `infrastructure/`, `usecase/`

---

## P1-2. 배송 도메인 + 주문 상태 확장

### 문제

주문 상태가 REQUESTED/CONFIRMED/CANCELED 3개뿐. 배송, 반품, 환불 추적 불가.

### 구현 상세

#### 1. OrderStatus 확장

```kotlin
enum class OrderStatus {
    // 기존
    REQUESTED,           // 주문 요청
    CONFIRMED,           // 결제 확인

    // 신규
    PREPARING,           // 상품 준비중
    READY_TO_SHIP,       // 발송 대기
    SHIPPED,             // 발송 완료
    IN_TRANSIT,          // 배송중
    DELIVERED,           // 배송 완료
    PURCHASE_CONFIRMED,  // 구매 확정
    RETURN_REQUESTED,    // 반품 요청
    RETURNED,            // 반품 완료
    REFUND_PENDING,      // 환불 대기
    REFUNDED,            // 환불 완료

    // 기존
    CANCELED             // 주문 취소
}
```

#### 2. ShipmentEntity

```kotlin
@Entity @Table(name = "shipment")
class ShipmentEntity(
    id: Long,
    orderId: Long,
    carrierId: String?,         // 배송 업체 코드
    trackingNumber: String?,    // 송장 번호
    status: ShipmentStatus,
    estimatedAt: LocalDateTime?,
    shippedAt: LocalDateTime?,
    deliveredAt: LocalDateTime?,
    createdAt: LocalDateTime
)
```

#### 3. 대상 패키지

- `order/common/OrderStatus.kt` 확장
- `shipment/` 전체 신규: `api/`, `domain/`, `infrastructure/`

---

## P1-3. 알림 도메인 (Kafka 연동)

### 문제

주문/결제/배송 상태 변경 시 고객 알림이 없어 UX 부족.

### 구현 상세

#### 1. 알림 이벤트

```kotlin
enum class NotificationChannel { SMS, EMAIL, PUSH }
enum class NotificationType { ORDER_PLACED, PAYMENT_CONFIRMED, SHIPPED, DELIVERED, STOCK_ALERT }

data class NotificationEvent(
    val userId: Long,
    val type: NotificationType,
    val channel: NotificationChannel,
    val title: String,
    val body: String,
    val orderId: Long? = null
)
```

#### 2. Kafka 연동

- 토픽: `NOTIFICATION`
- `NotificationKafkaConsumer`: 이벤트 수신 → `NotificationService.send()` 호출
- `NotificationService.send()`: 현재는 stub (로그 출력), 향후 실제 발송 연동

#### 3. 발행 지점

- `OrderFacade.order()` → ORDER_PLACED
- `PaymentFacade.pay()` → PAYMENT_CONFIRMED
- `ShipmentService` → SHIPPED, DELIVERED

#### 4. 대상 패키지

`notification/` 전체 신규

---

## P2-1. 사용자 등급 + 주소록

### 문제

`UserEntity`에 name, phone만 존재. 등급별 혜택, 배송 주소 관리 불가.

### 구현 상세

#### 1. UserEntity 확장

```kotlin
// 추가 필드
grade: UserGrade = NORMAL,
point: Long = 0,
totalPurchaseAmount: Long = 0,
status: UserStatus = ACTIVE
```

#### 2. UserGrade enum

```kotlin
enum class UserGrade(val minAmount: Long) {
    NORMAL(0), SILVER(100_000), GOLD(500_000), VIP(1_000_000), VVIP(5_000_000)
}
```

#### 3. UserAddressEntity (신규)

```kotlin
@Entity
class UserAddressEntity(
    id: Long, userId: Long, alias: String,
    receiverName: String, phone: String,
    address: String, zipCode: String, isDefault: Boolean
)
```

#### 4. API

```
POST   /user/address      - 주소 추가
PUT    /user/address/{id}  - 주소 수정
DELETE /user/address/{id}  - 주소 삭제
GET    /user/address       - 주소 목록
```

---

## P2-2. 복합 결제 지원 (잔액 + 포인트)

### 문제

`PayMethod`가 CREDIT_CARD, BANK_TRANSFER, POINTS 뿐이며 복합 결제 미지원.

### 구현 상세

#### 1. PaymentBreakdown DTO

```kotlin
data class PaymentBreakdown(
    val balanceAmount: Long = 0,
    val pointAmount: Long = 0,
    val cardAmount: Long = 0,
    val couponDiscount: Long = 0
) {
    val totalAmount: Long get() = balanceAmount + pointAmount + cardAmount
}
```

#### 2. PayMethod 확장

```kotlin
enum class PayMethod {
    CREDIT_CARD, BANK_TRANSFER, POINTS,
    BALANCE, MIXED  // 신규
}
```

#### 3. PaymentFacade 변경

- `PaymentCreation`에 `breakdown` 필드 추가
- 각 결제 수단별 독립적 차감 + 독립적 보상 트랜잭션

---

## P3-1. API 버저닝 (/api/v1/)

### 문제

모든 컨트롤러가 `/order`, `/payment` 등 버전 없는 경로 사용.

### 구현

- `WebMvcConfig`에서 공통 prefix `/api/v1` 설정
- 또는 각 컨트롤러 `@RequestMapping`에 `/api/v1` 추가

---

## P3-2. 이벤트 스키마 버저닝

### 문제

`OutboxEventInfo`에 이벤트 타입/버전 정보 없음. 스키마 변경 시 Consumer 깨짐 위험.

### 구현

```kotlin
data class OutboxEventInfo(
    var id: UUID,
    val groupId: String,
    val topic: String,
    val payload: String,
    val eventType: String = "",      // 신규: e.g. "OrderProductStock"
    val schemaVersion: String = "1"  // 신규: e.g. "1", "2"
)
```

- 각 이벤트 발행부에서 타입/버전 명시
- Consumer에서 지원 버전 검증

---

## P3-3. Rate Limiting

### 문제

API 남용 방지 장치 없음.

### 구현

- `bucket4j-spring-boot-starter` + Redis 기반
- 결제 API: 사용자별 분당 10회
- 주문 API: 사용자별 분당 20회
- `RateLimitExceededException` → 429 응답

---

## 의존성 그래프

```
P0-1 (재고 예약) ←── P0-2 (Saga에서 commit/release 호출)
P0-2 (Saga)     ←── P2-2 (복합 결제 시 Saga 확장)
P1-1 (쿠폰)    ←── P2-2 (쿠폰 할인을 breakdown에 포함)
P1-2 (배송)     ←── P1-3 (배송 상태 변경 시 알림 발행)
P2-1 (등급)     ←── P1-1 (등급별 쿠폰 발급 조건)
```

## 구현 순서 (권장)

1. **P0-1** → P0-2 (P0-1의 commit/release를 Saga에서 사용)
2. **P1-2** → P1-1 → P1-3 (배송 → 쿠폰 → 알림 순)
3. **P2-1** → P2-2
4. **P3-1** → P3-2 → P3-3
