package com.hhplus.ecommerce.order.usecase.dto

import com.hhplus.ecommerce.balance.domain.dto.BalanceTransaction
import com.hhplus.ecommerce.order.api.dto.OrderCreationRequest
import com.hhplus.ecommerce.order.domain.dto.OrderCreationCommand
import com.hhplus.ecommerce.product.domain.dto.ProductDetailQuery
import com.hhplus.ecommerce.user.domain.dto.UserQuery

data class OrderCreation(
    var userId: Long,
    var details: List<OrderDetailCreation>
) {
    companion object {
        fun from(userId: Long, details: List<OrderDetailCreation>): OrderCreation {
            return OrderCreation(
                userId = userId,
                details = details
            )
        }
    }

    fun toUserQuery(): UserQuery {
        return UserQuery(
            userId = userId,
        )
    }

    fun toBalanceTransaction(): BalanceTransaction {
        return BalanceTransaction(
            userId = userId,
            amount = details.map { it.quantity * it.price }.sum(),
            type = BalanceTransaction.TransactionType.USE
        )
    }

    fun toOrderCreationCommand(): OrderCreationCommand {
        return OrderCreationCommand(
            userId = userId,
            details = details.map { it.toDetailCreationCommand() }
        )
    }
}