package com.hhplus.ecommerce.order.usecase.dto

import com.hhplus.ecommerce.balance.domain.dto.BalanceTransaction
import com.hhplus.ecommerce.order.domain.dto.OrderCreationCommand
import com.hhplus.ecommerce.product.domain.dto.ProductDetailQuery
import com.hhplus.ecommerce.user.domain.dto.UserQuery

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