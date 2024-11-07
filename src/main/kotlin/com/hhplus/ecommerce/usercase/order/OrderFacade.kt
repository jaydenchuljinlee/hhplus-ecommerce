package com.hhplus.ecommerce.usercase.order

import com.hhplus.ecommerce.domain.balance.BalanceService
import com.hhplus.ecommerce.domain.cart.CartService
import com.hhplus.ecommerce.domain.cart.dto.CartDeletion
import com.hhplus.ecommerce.domain.cart.dto.ProductIdCartQuery
import com.hhplus.ecommerce.domain.order.OrderService
import com.hhplus.ecommerce.domain.product.ProductService
import com.hhplus.ecommerce.domain.product.dto.DecreaseProductDetailStock
import com.hhplus.ecommerce.domain.user.UserService
import com.hhplus.ecommerce.usercase.order.dto.OrderCreation
import com.hhplus.ecommerce.usercase.order.dto.OrderInfo
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