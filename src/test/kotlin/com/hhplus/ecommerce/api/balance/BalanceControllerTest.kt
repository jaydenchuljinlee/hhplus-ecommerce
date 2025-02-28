package com.hhplus.ecommerce.api.balance

import com.hhplus.ecommerce.balance.api.BalanceController
import com.hhplus.ecommerce.balance.api.dto.BalanceChargeRequest
import com.hhplus.ecommerce.balance.api.dto.BalanceViewRequest
import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.balance.domain.dto.BalanceQuery
import com.hhplus.ecommerce.balance.domain.dto.BalanceResult
import com.hhplus.ecommerce.balance.domain.dto.BalanceTransaction
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

}