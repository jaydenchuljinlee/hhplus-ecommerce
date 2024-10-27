package com.hhplus.ecommerce.api.balance.dto

import com.hhplus.ecommerce.api.balance.controller.exception.InsufficientBalanceAmountException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BalanceChargeRequestTest {
    @DisplayName("충전 금액이 0보다 작으면, InsufficientBalanceAmountException이 발생한다.")
    @Test
    fun invalidAmount() {
        // Given When
        val exception = assertThrows<InsufficientBalanceAmountException> {
            BalanceChargeRequest(1, -1,)
        }

        // Then
        assertEquals(exception.message, "충전 금액은 0보다 커야합니다.")
    }
}