## 결제 동시성 테스트 -> <b>사용자가 동시에 여러 결제를 하는 경우</b>

> 동시성 제어 코드

```kotlin
// 잔액에 대한 사용자 User ID 기반 Lock 제어
interface BalanceJpaRepository: JpaRepository<BalanceEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT b FROM BalanceEntity b WHERE b.userId = :userId")
    fun findByUserIdWithLock(@Param("userId") userId: Long): Optional<BalanceEntity>
    
// PaymentFacade
@Transactional
fun pay(dto: PaymentCreation): PaymentInfo {
  val balanceQuery = BalanceQuery(dto.userId)
  // 잔액에 대한 사용자 ID 기반의 Lock 획득
  val balance = balanceService.getBalanceWithLock(balanceQuery)

  val orderQuery = OrderQuery(
    orderId = dto.orderId,
    status = "ORDER_REQUEST"
  )
  val order = orderService.getOrder(orderQuery)

  val balanceToUseCommand = BalanceTransaction(
    userId = balance.userId,
    amount = order.quantity * order.price,
    type = BalanceTransaction.TransactionType.USE
  )

  // 내부 유효성 검사 후, 잔액 차감 진행
  balanceService.use(balanceToUseCommand)

  val paymentCreation = CreationPaymentCommand(
    orderId = dto.orderId,
    userId = dto.userId,
    price = order.quantity * order.price,
  )

  val result = paymentService.pay(paymentCreation)

  return PaymentInfo.from(result)
}

```

---

> 동시성 테스트 코드

```kotlin
@DisplayName("같은 사용자가 동시에 주문을 하면 잔액 정합성이 일치해야 한다")
    @Test
    fun concurrencyDuplicationTest() {
        val totalRequests = 3

        val original = balanceRepository.findByUserId(1)

        // ...

        // 5개의 요청을 비동기로 생성하여 실행
        for (i in 1..totalRequests) {
            executorService.submit {
                try {
                    // 모든 스레드가 준비될 때까지 대기
                    readyLatch.await()

                    val orderCommand = OrderCreation(1, i.toLong(), 1, 100)

                    // 주문 신청
                    val order = orderFacade.order(orderCommand)

                    val payCommand = PaymentCreation(
                        orderId = order.orderId,
                        userId = 1
                    )

                    // 결제
                    paymentFacade.pay(payCommand)

                } catch (e: Exception) {
                    println("$i -> ${e.message}")
                } finally {
                    // 요청 완료 시 Latch 카운트 감소
                    completeLatch.countDown()
                }
            }
        }

       // ...

        val newOne = balanceRepository.findByUserId(1)

        val histories = balanceHistoryJpaRepository.findAll()

        assertEquals(histories.size, 3) // 잔액 차감 히스토리 내역 일치 검사
        assertEquals(original.balance - newOne.balance, 300) // 잔액 정합성 검사

    }
```

---