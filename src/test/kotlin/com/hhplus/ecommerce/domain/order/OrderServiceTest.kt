package com.hhplus.ecommerce.domain.order

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.order.domain.dto.OrderCompleteCommand
import com.hhplus.ecommerce.order.domain.dto.OrderCreationCommand
import com.hhplus.ecommerce.order.domain.dto.OrderDeletionCommand
import com.hhplus.ecommerce.order.domain.dto.OrderDetailCreationCommand
import com.hhplus.ecommerce.order.domain.dto.OrderQuery
import com.hhplus.ecommerce.order.domain.dto.OrderStockConfirmCommand
import com.hhplus.ecommerce.order.domain.dto.OrderStockFailCommand
import com.hhplus.ecommerce.order.domain.repository.IOrderRepository
import com.hhplus.ecommerce.order.infrastructure.exception.InvalidOrderStatusException
import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
@DisplayName("OrderService 단위 테스트")
class OrderServiceTest {

    @Mock private lateinit var orderRepository: IOrderRepository

    private lateinit var orderService: OrderService

    @BeforeEach
    fun setup() {
        orderService = OrderService(orderRepository)
    }

    private fun orderEntity(
        id: Long = 1L,
        userId: Long = 1L,
        status: OrderStatus = OrderStatus.REQUESTED,
        totalPrice: Long = 10_000L,
        totalQuantity: Int = 2,
    ) = OrderEntity(id = id, userId = userId, totalPrice = totalPrice, totalQuantity = totalQuantity, status = status)

    // ─────────────────────────────────────────────────────
    // order
    // ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("order()")
    inner class Order {

        @Test
        @DisplayName("주문 생성 시 REQUESTED 상태로 저장되고 결과를 반환한다")
        fun `주문 생성 성공`() {
            // Given
            val command = OrderCreationCommand(
                userId = 1L,
                details = listOf(
                    OrderDetailCreationCommand(productId = 10L, quantity = 1, price = 5_000L),
                    OrderDetailCreationCommand(productId = 20L, quantity = 1, price = 5_000L),
                )
            )
            val savedEntity = orderEntity(status = OrderStatus.REQUESTED, totalPrice = 10_000L, totalQuantity = 2)
            given(orderRepository.insertOrUpdate(org.mockito.ArgumentMatchers.any())).willReturn(savedEntity)

            // When
            val result = orderService.order(command)

            // Then
            assertEquals(OrderStatus.REQUESTED, result.status)
            assertEquals(1L, result.userId)
        }
    }

    // ─────────────────────────────────────────────────────
    // getOrder
    // ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("getOrder()")
    inner class GetOrder {

        @Test
        @DisplayName("orderId와 status로 주문을 정상 조회한다")
        fun `주문 조회 성공`() {
            // Given
            val orderId = 1L
            val entity = orderEntity(id = orderId, status = OrderStatus.REQUESTED)
            given(orderRepository.findByIdAndStatus(orderId, OrderStatus.REQUESTED)).willReturn(entity)

            // When
            val result = orderService.getOrder(OrderQuery(orderId = orderId, status = OrderStatus.REQUESTED))

            // Then
            assertEquals(orderId, result.orderId)
            assertEquals(OrderStatus.REQUESTED, result.status)
        }
    }

    // ─────────────────────────────────────────────────────
    // confirmStock
    // ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("confirmStock()")
    inner class ConfirmStock {

        @Test
        @DisplayName("REQUESTED 상태에서 confirmStock 호출 시 STOCK_CONFIRMED로 전이된다")
        fun `재고 확보 완료 상태 전이 성공`() {
            // Given
            val orderId = 1L
            val entity = orderEntity(id = orderId, status = OrderStatus.REQUESTED)
            given(orderRepository.findByIdAndStatus(orderId, OrderStatus.REQUESTED)).willReturn(entity)
            given(orderRepository.insertOrUpdate(entity)).willReturn(entity)

            // When
            orderService.confirmStock(OrderStockConfirmCommand(orderId))

            // Then
            assertEquals(OrderStatus.STOCK_CONFIRMED, entity.status)
            verify(orderRepository).insertOrUpdate(entity)
        }

        @Test
        @DisplayName("REQUESTED가 아닌 상태에서 confirmStock 호출 시 InvalidOrderStatusException 발생")
        fun `잘못된 상태에서 재고 확보 완료 호출 시 예외 발생`() {
            // Given: 실제로는 STOCK_CONFIRMED 상태인 엔티티를 REQUESTED로 조회된 것처럼 반환 (데이터 불일치 시나리오)
            val orderId = 1L
            val entity = orderEntity(id = orderId, status = OrderStatus.STOCK_CONFIRMED)
            given(orderRepository.findByIdAndStatus(orderId, OrderStatus.REQUESTED)).willReturn(entity)

            // When & Then
            assertThrows<InvalidOrderStatusException> {
                orderService.confirmStock(OrderStockConfirmCommand(orderId))
            }
        }
    }

    // ─────────────────────────────────────────────────────
    // failStock
    // ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("failStock()")
    inner class FailStock {

        @Test
        @DisplayName("REQUESTED 상태에서 failStock 호출 시 STOCK_FAILED로 전이된다")
        fun `재고 확보 실패 상태 전이 성공`() {
            // Given
            val orderId = 1L
            val entity = orderEntity(id = orderId, status = OrderStatus.REQUESTED)
            given(orderRepository.findByIdAndStatus(orderId, OrderStatus.REQUESTED)).willReturn(entity)
            given(orderRepository.insertOrUpdate(entity)).willReturn(entity)

            // When
            orderService.failStock(OrderStockFailCommand(orderId))

            // Then
            assertEquals(OrderStatus.STOCK_FAILED, entity.status)
        }

        @Test
        @DisplayName("REQUESTED가 아닌 상태에서 failStock 호출 시 InvalidOrderStatusException 발생")
        fun `잘못된 상태에서 재고 확보 실패 호출 시 예외 발생`() {
            // Given: 실제로는 CANCELED 상태인 엔티티를 REQUESTED로 조회된 것처럼 반환 (데이터 불일치 시나리오)
            val orderId = 1L
            val entity = orderEntity(id = orderId, status = OrderStatus.CANCELED)
            given(orderRepository.findByIdAndStatus(orderId, OrderStatus.REQUESTED)).willReturn(entity)

            // When & Then
            assertThrows<InvalidOrderStatusException> {
                orderService.failStock(OrderStockFailCommand(orderId))
            }
        }
    }

    // ─────────────────────────────────────────────────────
    // orderComplete
    // ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("orderComplete()")
    inner class OrderComplete {

        @Test
        @DisplayName("STOCK_CONFIRMED 상태에서 orderComplete 호출 시 CONFIRMED로 전이된다")
        fun `주문 확정 상태 전이 성공`() {
            // Given
            val orderId = 1L
            val entity = orderEntity(id = orderId, status = OrderStatus.STOCK_CONFIRMED)
            given(orderRepository.findByIdAndStatus(orderId, OrderStatus.STOCK_CONFIRMED)).willReturn(entity)
            given(orderRepository.insertOrUpdate(entity)).willReturn(entity)

            // When
            orderService.orderComplete(OrderCompleteCommand(orderId))

            // Then
            assertEquals(OrderStatus.CONFIRMED, entity.status)
            verify(orderRepository).insertOrUpdate(entity)
        }

        @Test
        @DisplayName("STOCK_CONFIRMED가 아닌 상태에서 orderComplete 호출 시 InvalidOrderStatusException 발생")
        fun `잘못된 상태에서 주문 확정 호출 시 예외 발생`() {
            // Given: 실제로는 REQUESTED 상태인 엔티티를 STOCK_CONFIRMED로 조회된 것처럼 반환 (데이터 불일치 시나리오)
            val orderId = 1L
            val entity = orderEntity(id = orderId, status = OrderStatus.REQUESTED)
            given(orderRepository.findByIdAndStatus(orderId, OrderStatus.STOCK_CONFIRMED)).willReturn(entity)

            // When & Then
            assertThrows<InvalidOrderStatusException> {
                orderService.orderComplete(OrderCompleteCommand(orderId))
            }
        }
    }

    // ─────────────────────────────────────────────────────
    // deleteOrderDetail
    // ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("deleteOrderDetail()")
    inner class DeleteOrderDetail {

        @Test
        @DisplayName("orderId와 productId로 주문 상세 삭제를 호출한다")
        fun `주문 상세 삭제 호출 성공`() {
            // Given
            val orderId = 1L
            val productId = 10L

            // When
            orderService.deleteOrderDetail(OrderDeletionCommand(orderId = orderId, productId = productId))

            // Then
            verify(orderRepository).deleteOrderDetail(orderId, productId)
        }
    }
}
