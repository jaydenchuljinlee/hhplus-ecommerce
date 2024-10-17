package com.hhplus.ecommerce.api.balance

import com.hhplus.ecommerce.api.balance.controller.BalanceController
import com.hhplus.ecommerce.api.balance.dto.BalanceChargeRequest
import com.hhplus.ecommerce.api.balance.dto.BalanceViewRequest
import com.hhplus.ecommerce.domain.balance.BalanceService
import com.hhplus.ecommerce.domain.balance.dto.BalanceQuery
import com.hhplus.ecommerce.domain.balance.dto.BalanceResult
import com.hhplus.ecommerce.domain.balance.dto.BalanceTransaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class BalanceControllerTest {
    @Mock
    private lateinit var balanceService: BalanceService

    private lateinit var balanceController: BalanceController

    @BeforeEach
    fun before() {
        balanceController = BalanceController(balanceService)
    }

    @DisplayName("success: 잔액 충전 API")
    @Test
    fun successBalanceCharge() {
        // Given
        val param = BalanceTransaction(1, 1000L, BalanceTransaction.TransactionType.CHARGE)

        val result = BalanceResult(
            userId = 1,
            balance = 1000
        )

        BDDMockito.given(balanceService.charge(param)).willReturn(result)

        val request = BalanceChargeRequest(1L, 1000)

        val response = balanceController.charge(request).data!!

        assertEquals(response.userId, result.userId)
        assertEquals(response.amount, result.balance)
    }

    @DisplayName("success: 잔액 조회 API")
    @Test
    fun successBalanceGet() {
        // Given
        val query = BalanceQuery(userId = 0)
        val result = BalanceResult(
            userId = 0,
            balance = 1000
        )

        BDDMockito.given(balanceService.getBalance(query)).willReturn(result)

        val request = BalanceViewRequest(0L)

        val response = balanceController.getBalance(request).data!!

        assertEquals(response.userId, 0L)
        assertEquals(response.balance, 1000)
    }

//    @DisplayName("사용자 정보가 존재하지 않으면, UssrNotFoundException 을 반환한다.")
//    @Test
//    fun userNotfoundException() {
//        val request = BalanceChargeRequest(1L, 1000)
//
//        val exception = assertThrows<UserNotFoundException> {
//            balanceController.charge(request)
//        }
//
//        assertEquals(exception.message, "사용자 정보가 존재하지 않습니다.")
//    }
//
//    @DisplayName("잔액 정보가 존재하지 않으면, UssrNotFoundException 을 반환한다.")
//    @Test
//    fun balanceNotfoundException() {
//        val request = BalanceChargeRequest(2L, 1000)
//
//        val exception = assertThrows<BalanceNotFoundException> {
//            balanceController.charge(request)
//        }
//
//        assertEquals(exception.message, "잔액 정보가 존재하지 않습니다.")
//    }
//
//    @DisplayName("잔액 충전 금액이 기준값을 초과하면, BalanceLimitExceededException이 발생한다.")
//    @Test
//    fun balanceLimitExceededException() {
//        val request = BalanceChargeRequest(0L, 100_000_001)
//
//        val exception = assertThrows<BalanceLimitExceededException> {
//            balanceController.charge(request)
//        }
//
//        assertEquals(exception.message, "잔액 충전 범위를 초과하셨습니다.")
//    }
//
//    @DisplayName("(잔액 충전 금액 + 기존 잔액)이 기준값을 초과하면, BalanceLimitExceededException이 발생한다.")
//    @Test
//    fun balanceLimitExceededException2() {
//        val request = BalanceChargeRequest(0L, 99_999_001)
//
//        val exception = assertThrows<BalanceLimitExceededException> {
//            balanceController.charge(request)
//        }
//
//        assertEquals(exception.message, "잔액 충전 범위를 초과하셨습니다.")
//    }
}