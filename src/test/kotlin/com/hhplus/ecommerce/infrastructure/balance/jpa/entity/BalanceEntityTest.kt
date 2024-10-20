package com.hhplus.ecommerce.infrastructure.balance.jpa.entity

import com.hhplus.ecommerce.infrastructure.balance.exception.BalanceLimitExceededException
import com.hhplus.ecommerce.infrastructure.balance.exception.InsufficientBalanceException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class BalanceEntityTest {
    @DisplayName("기준 잔액 이상의 충전은 BalanceLimitExceededException을 발생한다.")
    @Test
    fun chargeBalanceLimitExceeded() {
        // Given
        val balanceEntity = BalanceEntity(1, 1, 90_000_000)

        // When
        val exception = assertThrows<BalanceLimitExceededException> {
            balanceEntity.charge(10_000_001)
        }

        // Then
        assertEquals(exception.message, "잔액 충전 범위를 초과하셨습니다.")

    }

    @DisplayName("최소 사용 금액보다 낮은 잔액 사용은 InsufficientBalanceException을 발생한다.")
    @Test
    fun useBalanceInsufficient() {
        // Given
        val balanceEntity = BalanceEntity(1, 1, 10_000)

        // When
        val exception = assertThrows<InsufficientBalanceException> {
            balanceEntity.use(99)
        }

        assertEquals(exception.message, "최소 사용 금액은 100원 입니다.")
    }

    @DisplayName("기존 금액 + 사용 금액 < 0 이면 InsufficientBalanceException을 발생한다.")
    @Test
    fun useBalanceNegative() {
        // Given
        val balanceEntity = BalanceEntity(1, 1, 10_000)

        // When
        val exception = assertThrows<InsufficientBalanceException> {
            balanceEntity.use(10_001)
        }

        assertEquals(exception.message, "잔액이 부족합니다.")
    }
}