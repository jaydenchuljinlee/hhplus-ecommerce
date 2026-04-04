package com.hhplus.ecommerce.order.usecase

import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.product.domain.ProductService
import com.hhplus.ecommerce.product.domain.StockReservationService
import com.hhplus.ecommerce.user.domain.UserService
import com.hhplus.ecommerce.notification.common.NotificationChannel
import com.hhplus.ecommerce.notification.common.NotificationType
import com.hhplus.ecommerce.notification.domain.INotificationEventPublisher
import com.hhplus.ecommerce.notification.domain.dto.NotificationEvent
import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import com.hhplus.ecommerce.order.usecase.dto.OrderInfo
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderFacade(
    private val userService: UserService,
    private val balanceService: BalanceService,
    private val orderService: OrderService,
    private val stockReservationService: StockReservationService,
    private val productService: ProductService,
    private val notificationEventPublisher: INotificationEventPublisher,
) {

    @Transactional
    fun order(info: OrderCreation): OrderInfo {
        val user = userService.getUserById(info.toUserQuery())
        balanceService.validateBalanceToUse(info.toBalanceTransaction())

        val order = orderService.order(info.toOrderCreationCommand())
        val result = OrderInfo.from(order)

        result.details.forEach {
            stockReservationService.reserve(result.orderId, it.productId, it.quantity)
            productService.deleteCache(it.productId)
        }

        notificationEventPublisher.publish(
            NotificationEvent(
                userId = user.userId,
                type = NotificationType.ORDER_PLACED,
                channel = NotificationChannel.PUSH,
                title = "주문이 접수되었습니다.",
                body = "주문 번호 ${result.orderId}번 주문이 정상적으로 접수되었습니다.",
                orderId = result.orderId
            )
        )

        return result
    }

}
