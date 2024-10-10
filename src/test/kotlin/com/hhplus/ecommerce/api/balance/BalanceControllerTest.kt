package com.hhplus.ecommerce.api.balance

import com.hhplus.ecommerce.api.balance.controller.BalanceController
import com.hhplus.ecommerce.api.balance.dto.BalanceRequest
import com.hhplus.ecommerce.common.exception.balance.BalanceLimitExceededException
import com.hhplus.ecommerce.common.exception.balance.BalanceNotFoundException
import com.hhplus.ecommerce.common.exception.user.UserNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class BalanceControllerTest {

    private lateinit var balanceController: BalanceController

    @BeforeEach
    fun before() {
        balanceController = BalanceController()
    }

    @DisplayName("success: 잔액 충전 API")
    @Test
    fun successBalanceCharge() {
        val request = BalanceRequest.Charge(0L, 1000)

        val response = balanceController.charge(request)

        assertEquals(response.userId, 0L)
        assertEquals(response.amount, 2000)
        assertEquals(response.transactionType, "CHARGE")
    }

    @DisplayName("success: 잔액 조회 API")
    @Test
    fun successBalanceGet() {
        val request = BalanceRequest.View(0L)

        val response = balanceController.getBalance(request)

        assertEquals(response.userId, 0L)
        assertEquals(response.balance, 1000)
    }

    @DisplayName("사용자 정보가 존재하지 않으면, UssrNotFoundException 을 반환한다.")
    @Test
    fun userNotfoundException() {
        val request = BalanceRequest.Charge(1L, 1000)

        val exception = assertThrows<UserNotFoundException> {
            balanceController.charge(request)
        }

        assertEquals(exception.message, "사용자 정보가 존재하지 않습니다.")
    }

    @DisplayName("잔액 정보가 존재하지 않으면, UssrNotFoundException 을 반환한다.")
    @Test
    fun balanceNotfoundException() {
        val request = BalanceRequest.Charge(2L, 1000)

        val exception = assertThrows<BalanceNotFoundException> {
            balanceController.charge(request)
        }

        assertEquals(exception.message, "잔액 정보가 존재하지 않습니다.")
    }

    @DisplayName("잔액 충전 금액이 기준값을 초과하면, BalanceLimitExceededException이 발생한다.")
    @Test
    fun balanceLimitExceededException() {
        val request = BalanceRequest.Charge(0L, 100_000_001)

        val exception = assertThrows<BalanceLimitExceededException> {
            balanceController.charge(request)
        }

        assertEquals(exception.message, "잔액 충전 범위를 초과하셨습니다.")
    }

    @DisplayName("(잔액 충전 금액 + 기존 잔액)이 기준값을 초과하면, BalanceLimitExceededException이 발생한다.")
    @Test
    fun balanceLimitExceededException2() {
        val request = BalanceRequest.Charge(0L, 99_999_001)

        val exception = assertThrows<BalanceLimitExceededException> {
            balanceController.charge(request)
        }

        assertEquals(exception.message, "잔액 충전 범위를 초과하셨습니다.")
    }
}