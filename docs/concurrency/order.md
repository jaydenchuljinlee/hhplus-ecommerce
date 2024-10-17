## 주문 동시성 테스트 -> <b>여러 사용자가 동시에 한 살품을 주문하는 경우</b>

> 주문에 동시성 제어 코드

```kotlin
// 재고 관련 Repository Lock

interface ProductDetailJpaRepository: JpaRepository<ProductDetailEntity, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  fun findByProductId(productId: Long): Optional<ProductDetailEntity>
}

// 주문 Facade 

@Transactional
fun order(info: OrderCreation): OrderInfo {
  // 상품의 재고 ID 기반 lock 획득
  val productDetail = productService.getProductDetailByIdWithLock(info.toProductDetailQuery())

  // 사용자의 잔액 검사. 실제 차감은 하지 않는다. -> 결제에서 차감
  val user = userService.getUserById(info.toUserQuery())
  balanceService.validateBalanceToUse(info.toBalanceTransaction())

  val productDetailItem = DecreaseProductDetailStock(
    id = productDetail.productDetailId,
    amount = info.quantity,
    stock = productDetail.quantity
  )

  // 상품 재고 차감. SRP 원칙에 따라 유효성 검사는 Entity에서 수행. 
  productService.decreaseStock(productDetailItem)

  val order = orderService.order(info.toOrderCreationCommand())

  val cartQuery = ProductIdCartQuery(info.productId)

  val cart = cartService.getCartByProduct(cartQuery)

  // 장바구니 존재 여부를 검사하고, 주문 처리 시 장바구니에서 제거
  if (cart != null) {
    val cartDeletion = CartDeletion(cart.cartId)
    cartService.delete(cartDeletion)
  }

  val result = OrderInfo(
    orderId = order.orderId,
    userId = user.userId,
    productId = info.productId,
    quantity = info.quantity,
    price = info.price,
    status = order.status
  )

  return result
}


```
---

> 동시성 테스트

```kotlin
// OrderConcurrencyTest.class

@DisplayName("재고가 하나 남은 상품에 대해 동시 신청을 하게되면, 1명만 성공한다")
@Test
fun concurrencyTest() {
  val successUserIds = Collections.synchronizedList(mutableListOf<Long>())
  val errorUserIds = Collections.synchronizedList(mutableListOf<Long>())
  val totalRequests = 5

  // ...

  // 5개의 요청을 비동기로 생성하여 실행
  for (i in 1..totalRequests) {
    executorService.submit {
      try {
        // 모든 스레드가 준비될 때까지 대기
        readyLatch.await()

        // 요청마다 같은 상품 ID를 사용하여 LectureCommandData 생성
        val command = OrderCreation(i.toLong(), 1L, 1, 100)

        // 주문 신청
        orderFacade.order(command)

        successUserIds.add(i.toLong())
      } catch (e: Exception) {
        errorUserIds.add(i.toLong())
        println("$i -> ${e.message}")
      } finally {
        // 요청 완료 시 Latch 카운트 감소
        completeLatch.countDown()
      }
    }
  }

  // ...

  val productDetail = productRepository.findById(1).get()

  assertEquals(productDetail.quantity, 0) // 수량이 남았는지 검사
  assertEquals(successUserIds.size, 1) // 첫 번째 요청은 성공한다
  assertEquals(errorUserIds.size, 4) // 두 번째 요청은 실패한다
}
```

---