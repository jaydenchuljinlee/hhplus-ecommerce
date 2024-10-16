package com.hhplus.ecommerce.usercase.order.dto

import com.hhplus.ecommerce.domain.balance.dto.BalanceTransaction
import com.hhplus.ecommerce.domain.order.dto.OrderCreationCommand
import com.hhplus.ecommerce.domain.product.dto.ProductDetailQuery
import com.hhplus.ecommerce.domain.user.dto.UserQuery

data class OrderCreation(
    var userId: Long,
    var productId: Long,
    var quantity: Int,
    var price: Long,
) {
    fun toProductDetailQuery(): ProductDetailQuery {
        return ProductDetailQuery(
            productId = productId,
        )
    }

    fun toUserQuery(): UserQuery {
        return UserQuery(
            userId = userId,
        )
    }

    fun toBalanceTransaction(): BalanceTransaction {
        return BalanceTransaction(
            userId = userId,
            amount = quantity * price,
            type = BalanceTransaction.TransactionType.USE
        )
    }

    fun toOrderCreationCommand(): OrderCreationCommand {
        return OrderCreationCommand(
            userId = userId,
            productId = productId,
            quantity = quantity,
            price = price,
        )
    }
}