# 빅테크 커머스 보완 구현 계획

> 분석 기준: 현재 구조 vs 빅테크 커머스 갭 분석 결과
> 구현 순서: P0 → P1 → P2 → P3

---

## 진행 상태 요약

| ID | 항목 | 우선순위 | 상태 |
|----|------|---------|------|
| P0-1 | 재고 Soft Reserve (예약재고) | 🔴 P0 | ✅ 완료 |
| P0-2 | PaymentSaga 상태 추적 | 🔴 P0 | ✅ 완료 |
| P1-1 | 쿠폰 도메인 (발급/사용) | 🟡 P1 | ✅ 완료 |
| P1-2 | 배송 도메인 + 주문 상태 확장 | 🟡 P1 | ✅ 완료 |
| P1-3 | 알림 도메인 (Kafka 연동) | 🟡 P1 | ✅ 완료 |
| P2-1 | 사용자 등급 + 주소록 | 🟠 P2 | ✅ 완료 |
| P2-2 | 복합 결제 지원 (잔액+포인트) | 🟠 P2 | ⬜ 미착수 |
| P3-1 | API 버저닝 (/api/v1/) | 🔵 P3 | ⬜ 미착수 |
| P3-2 | 이벤트 스키마 버저닝 | 🔵 P3 | ⬜ 미착수 |
| P3-3 | Rate Limiting | 🔵 P3 | ⬜ 미착수 |

---

## P0-1. 재고 Soft Reserve (예약재고) 🔴

**문제**: 주문 생성 시점에 재고를 점유하지 않아 동시 주문 시 초과 판매(oversell) 가능

**구현 내용**:
- [ ] `ProductDetailEntity`에 `reservedQuantity` 필드 추가
- [ ] `ProductDetailEntity`에 `reserve()` / `commit()` / `release()` 메서드 추가
- [ ] `availableQuantity` (= quantity - reservedQuantity) 프로퍼티 추가
- [ ] `StockReservationEntity` 신규 생성 (orderId, productDetailId, quantity, expiredAt, status)
- [ ] `StockReservationStatus` enum (RESERVED / COMMITTED / RELEASED / EXPIRED)
- [ ] `IStockReservationRepository` 인터페이스 + 구현체
- [ ] `StockReservationService` 도메인 서비스
- [ ] `OrderProductStockKafkaConsumer` 수정: `decreaseStock()` → `reserve()`
- [ ] `PaymentFacade` 수정: 결제 완료 시 `commit()` 호출
- [ ] `StockReservationExpiryScheduler`: 만료된 예약 재고 자동 반납 (매 5분)

**대상 파일**:
- `product/infrastructure/jpa/entity/ProductDetailEntity.kt`
- `product/infrastructure/jpa/entity/StockReservationEntity.kt` (신규)
- `product/domain/StockReservationService.kt` (신규)
- `product/infrastructure/event/OrderProductStockKafkaConsumer.kt`
- `payment/usecase/PaymentFacade.kt`
- `common/schedular/StockReservationExpiryScheduler.kt` (신규)

---

## P0-2. PaymentSaga 상태 추적 🔴

**문제**: 환불 실패 시 잔액 영구 손실 위험, 보상 트랜잭션 추적 불가

**구현 내용**:
- [ ] `PaymentSagaEntity` 신규 생성 (orderId, userId, sagaStatus, balanceTransactionId, paymentId, failReason)
- [ ] `PaymentSagaStatus` enum (STARTED / BALANCE_DEDUCTED / PAYMENT_CREATED / ORDER_CONFIRMED / COMPENSATING / COMPENSATION_FAILED / COMPLETED / FAILED)
- [ ] `IPaymentSagaRepository` 인터페이스 + 구현체
- [ ] `PaymentFacade` 리팩터링: 각 단계별 Saga 상태 기록
- [ ] 보상 트랜잭션 실패 시 COMPENSATION_FAILED 상태로 저장 + WARN 로그 (수동 처리 큐)
- [ ] `PaymentSagaRecoveryScheduler`: COMPENSATION_FAILED 상태 주기적 알림

**대상 파일**:
- `payment/infrastructure/jpa/entity/PaymentSagaEntity.kt` (신규)
- `payment/domain/repository/IPaymentSagaRepository.kt` (신규)
- `payment/infrastructure/PaymentSagaRepository.kt` (신규)
- `payment/usecase/PaymentFacade.kt`
- `common/schedular/PaymentSagaRecoveryScheduler.kt` (신규)

---

## P1-1. 쿠폰 도메인 🟡

**문제**: 쿠폰/할인 기능 완전 부재

**구현 내용**:
- [ ] `CouponPolicyEntity` (name, type, discountValue, minOrderAmount, maxDiscountAmount, totalQuantity, issuedQuantity, startAt, expiredAt)
- [ ] `CouponType` enum (FIXED / RATE / FREE_SHIPPING)
- [ ] `UserCouponEntity` (userId, couponPolicyId, status, issuedAt, usedAt, orderId)
- [ ] `UserCouponStatus` enum (ISSUED / USED / EXPIRED)
- [ ] `CouponService` 도메인 서비스: `issue()` @RedisLock 선착순 발급, `use()`, `cancel()`
- [ ] `ICouponRepository` / `IUserCouponRepository` 인터페이스 + 구현체
- [ ] `CouponController` + `ICouponController` API (발급 POST, 조회 GET)
- [ ] `CouponExpiredScheduler`: 만료 쿠폰 상태 자동 업데이트

**대상 파일**:
- `coupon/` 패키지 전체 신규 생성

---

## P1-2. 배송 도메인 + 주문 상태 확장 🟡

**문제**: 배송 추적 불가, 반품/환불 상태 없음

**구현 내용**:
- [ ] `OrderStatus` 확장: PAYMENT_PENDING / PAYMENT_CONFIRMED / PREPARING / READY_TO_SHIP / SHIPPED / IN_TRANSIT / DELIVERED / PURCHASE_CONFIRMED / RETURN_REQUESTED / RETURNED / REFUND_PENDING / REFUNDED
- [ ] `ShipmentEntity` (orderId, carrierId, trackingNumber, status, estimatedAt, deliveredAt)
- [ ] `ShipmentStatus` enum (PREPARING / SHIPPED / IN_TRANSIT / DELIVERED / RETURN_REQUESTED / RETURNED)
- [ ] `ShipmentService` 도메인 서비스
- [ ] `IShipmentRepository` 인터페이스 + 구현체
- [ ] `ShipmentController` API (배송 조회 GET)
- [ ] `PurchaseConfirmScheduler`: 배송 완료 7일 후 자동 구매 확정

**대상 파일**:
- `order/common/OrderStatus.kt`
- `shipment/` 패키지 전체 신규 생성

---

## P1-3. 알림 도메인 (Kafka 연동) 🟡

**문제**: 주문/결제 상태 변경 시 고객 알림 없음

**구현 내용**:
- [ ] `NotificationChannel` enum (SMS / EMAIL / PUSH)
- [ ] `NotificationEvent` (userId, type, channel, title, body, orderId?)
- [ ] `NotificationType` enum (ORDER_PLACED / PAYMENT_CONFIRMED / SHIPPED / DELIVERED / STOCK_ALERT)
- [ ] `NotificationService` (send 메서드 — 실제 발송은 stub)
- [ ] `INotificationEventPublisher` 인터페이스 + 구현체
- [ ] Kafka topic `NOTIFICATION` 추가
- [ ] `NotificationKafkaConsumer`: 알림 이벤트 수신 및 처리
- [ ] `OrderFacade` / `PaymentFacade`에서 상태 변경 시 알림 이벤트 발행

**대상 파일**:
- `notification/` 패키지 전체 신규 생성

---

## P2-1. 사용자 등급 + 주소록 🟠

**문제**: 사용자 도메인 정보 부족

**구현 내용**:
- [ ] `UserGrade` enum (NORMAL / SILVER / GOLD / VIP / VVIP)
- [ ] `UserEntity`에 grade, point, totalPurchaseAmount, status 필드 추가
- [ ] `UserAddressEntity` (userId, alias, receiverName, phone, address, zipCode, isDefault)
- [ ] `UserAddressService` + `IUserAddressRepository`
- [ ] `UserAddressController` API (주소 추가/수정/삭제/조회)
- [ ] 결제 완료 시 totalPurchaseAmount 업데이트 + 등급 재산정 로직

**대상 파일**:
- `user/` 패키지 확장

---

## P2-2. 복합 결제 지원 (잔액 + 포인트) 🟠

**문제**: CREDIT_CARD 단일 결제만 지원

**구현 내용**:
- [ ] `PaymentBreakdown` DTO (balanceAmount, pointAmount, cardAmount, couponDiscount)
- [ ] `PaymentRequest`에 breakdown 필드 추가
- [ ] `PaymentFacade` 수정: breakdown에 따라 잔액/포인트/카드 분기 처리
- [ ] `PayMethod` 확장: BALANCE / POINT / MIXED 추가
- [ ] 복합 결제 시 각 수단별 독립 보상 트랜잭션 처리

**대상 파일**:
- `payment/` 패키지 관련 파일들

---

## P3-1. API 버저닝 (/api/v1/) 🔵

**문제**: API 버전 관리 없음

**구현 내용**:
- [ ] 모든 `@RequestMapping` 경로에 `/api/v1` 프리픽스 추가
- [ ] `WebMvcConfig`에서 공통 prefix 설정

---

## P3-2. 이벤트 스키마 버저닝 🔵

**문제**: Outbox 이벤트 페이로드 스키마 변경 시 Consumer 깨짐 위험

**구현 내용**:
- [ ] `OutboxEventInfo`에 `eventType: String`, `schemaVersion: String` 필드 추가
- [ ] 각 이벤트 발행부에서 타입/버전 명시
- [ ] Consumer에서 버전 검증 로직 추가

---

## P3-3. Rate Limiting 🔵

**문제**: API 남용 방지 장치 없음

**구현 내용**:
- [ ] `bucket4j-spring-boot-starter` 의존성 추가
- [ ] Redis 기반 Rate Limiter 설정
- [ ] 결제 API: 사용자별 분당 10회 제한
- [ ] 주문 API: 사용자별 분당 20회 제한
- [ ] `RateLimitExceededException` → 429 Too Many Requests
