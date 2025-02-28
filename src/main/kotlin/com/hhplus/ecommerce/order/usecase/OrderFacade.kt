package com.hhplus.ecommerce.order.usecase

import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.cart.domain.CartService
import com.hhplus.ecommerce.cart.domain.dto.CartDeletion
import com.hhplus.ecommerce.cart.domain.dto.ProductIdCartQuery
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.product.domain.ProductService
import com.hhplus.ecommerce.product.domain.dto.DecreaseProductDetailStock
import com.hhplus.ecommerce.user.domain.UserService
import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import com.hhplus.ecommerce.order.usecase.dto.OrderInfo
import org.springframework.stereotype.Component

@Component
class OrderFacade(
    private val userService: UserService,
    private val productService: ProductService,
    private val balanceService: BalanceService,
    private val orderService: OrderService,
    private val cartService: CartService,
) {

    fun order(info: OrderCreation): OrderInfo {
        // 상품 정보 조회
        val productDetail = productService.getProductDetail(info.toProductDetailQuery())

        val user = userService.getUserById(info.toUserQuery())
        balanceService.validateBalanceToUse(info.toBalanceTransaction())

        val productDetailItem = DecreaseProductDetailStock(
            id = productDetail.productDetailId,
            amount = info.quantity,
            stock = productDetail.quantity
        )

        productService.decreaseStock(productDetailItem)
        productService.deleteCache(productDetail.productId)
        val order = orderService.order(info.toOrderCreationCommand())

        val cartQuery = ProductIdCartQuery(info.productId)

        val cart = cartService.getCartByProduct(cartQuery)

        if (cart != null) {
            val cartDeletion = CartDeletion(cart.cartId)
            cartService.delete(cartDeletion)
        }

        // 최근 3일 간의 Top5 캐시 갱신
        productService.refreshTopFiveLastThreeDays()

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

}