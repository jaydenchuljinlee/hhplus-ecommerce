package com.hhplus.ecommerce.domain.balance

import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.balance.domain.dto.BalanceQuery
import com.hhplus.ecommerce.balance.domain.dto.BalanceTransaction
import com.hhplus.ecommerce.balance.domain.event.IBalanceEventPublisher
import com.hhplus.ecommerce.balance.domain.respository.IBalanceRepository
import com.hhplus.ecommerce.balance.infrastructure.constants.BalancePolicy
import com.hhplus.ecommerce.balance.infrastructure.exception.BalanceLimitExceededException
import com.hhplus.ecommerce.balance.infrastructure.exception.InsufficientBalanceException
import com.hhplus.ecommerce.balance.infrastructure.jpa.entity.BalanceEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
@DisplayName("BalanceService 단위 테스트")
class BalanceServiceTest {

    @Mock private lateinit var balanceRepository: IBalanceRepository
    @Mock private lateinit var balanceEventPublisher: IBalanceEventPublisher

    private lateinit var balanceService: BalanceService

    @BeforeEach
    fun setup() {
        balanceService = BalanceService(balanceRepository, balanceEventPublisher)
    }

    // ─────────────────────────────────────────────────────
    // getBalance
    // ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("getBalance()")
    inner class GetBalance {

        @Test
        @DisplayName("userId로 잔액을 정상 조회한다")
        fun `잔액 조회 성공`() {
            // Given
            val userId = 1L
            val entity = BalanceEntity(id = 1L, userId = userId, balance = 5_000L)
            given(balanceRepository.findByUserId(userId)).willReturn(entity)

            // When
            val result = balanceService.getBalance(BalanceQuery(userId))

            // Then
            assertEquals(userId, result.userId)
            assertEquals(5_000L, result.balance)
        }
    }

    // ─────────────────────────────────────────────────────
    // charge
    // ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("charge()")
    inner class Charge {

        @Test
        @DisplayName("정상 금액을 충전하면 잔액이 증가하고 이벤트가 발행된다")
        fun `충전 성공`() {
            // Given
            val userId = 1L
            val initialBalance = 10_000L
            val chargeAmount = 5_000L
            val entity = BalanceEntity(id = 1L, userId = userId, balance = initialBalance)
            given(balanceRepository.findByUserId(userId)).willReturn(entity)
            given(balanceRepository.insertOrUpdate(entity)).willReturn(entity)

            // When
            val result = balanceService.charge(
                BalanceTransaction(userId = userId, amount = chargeAmount, type = BalanceTransaction.TransactionType.CHARGE)
            )

            // Then
            assertEquals(initialBalance + chargeAmount, result.balance)
            verify(balanceEventPublisher).publishCharge(entity.id, chargeAmount, entity.balance)
        }

        @Test
        @DisplayName("충전 후 잔액이 정확히 MAX(${BalancePolicy.MAX})이면 성공한다")
        fun `MAX 경계값 충전 성공`() {
            // Given
            val userId = 1L
            val initialBalance = 0L
            val chargeAmount = BalancePolicy.MAX.toLong()
            val entity = BalanceEntity(id = 1L, userId = userId, balance = initialBalance)
            given(balanceRepository.findByUserId(userId)).willReturn(entity)
            given(balanceRepository.insertOrUpdate(entity)).willReturn(entity)

            // When
            val result = balanceService.charge(
                BalanceTransaction(userId = userId, amount = chargeAmount, type = BalanceTransaction.TransactionType.CHARGE)
            )

            // Then
            assertEquals(BalancePolicy.MAX.toLong(), result.balance)
        }

        @Test
        @DisplayName("충전 후 잔액이 MAX를 초과하면 BalanceLimitExceededException이 발생한다")
        fun `한도 초과 충전 시 예외 발생`() {
            // Given
            val userId = 1L
            val entity = BalanceEntity(id = 1L, userId = userId, balance = 90_000_000L)
            given(balanceRepository.findByUserId(userId)).willReturn(entity)

            // When & Then
            assertThrows<BalanceLimitExceededException> {
                balanceService.charge(
                    BalanceTransaction(userId = userId, amount = 10_000_001L, type = BalanceTransaction.TransactionType.CHARGE)
                )
            }
        }
    }

    // ─────────────────────────────────────────────────────
    // use
    // ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("use()")
    inner class Use {

        @Test
        @DisplayName("정상 금액을 사용하면 잔액이 감소하고 이벤트가 발행된다")
        fun `사용 성공`() {
            // Given
            val userId = 1L
            val initialBalance = 10_000L
            val useAmount = 3_000L
            val entity = BalanceEntity(id = 1L, userId = userId, balance = initialBalance)
            given(balanceRepository.findByUserId(userId)).willReturn(entity)
            given(balanceRepository.insertOrUpdate(entity)).willReturn(entity)

            // When
            val result = balanceService.use(
                BalanceTransaction(userId = userId, amount = useAmount, type = BalanceTransaction.TransactionType.USE)
            )

            // Then
            assertEquals(initialBalance - useAmount, result.balance)
            verify(balanceEventPublisher).publishUse(entity.id, useAmount, entity.balance)
        }

        @Test
        @DisplayName("사용 후 잔액이 정확히 MIN(${BalancePolicy.MIN})이면 성공한다")
        fun `사용 후 잔액이 MIN 경계값일 때 성공`() {
            // Given
            val userId = 1L
            val initialBalance = 10_000L
            val useAmount = initialBalance - BalancePolicy.MIN  // 남은 잔액 = MIN
            val entity = BalanceEntity(id = 1L, userId = userId, balance = initialBalance)
            given(balanceRepository.findByUserId(userId)).willReturn(entity)
            given(balanceRepository.insertOrUpdate(entity)).willReturn(entity)

            // When
            val result = balanceService.use(
                BalanceTransaction(userId = userId, amount = useAmount, type = BalanceTransaction.TransactionType.USE)
            )

            // Then
            assertEquals(BalancePolicy.MIN.toLong(), result.balance)
        }

        @Test
        @DisplayName("사용 후 잔액이 MIN 미만이면 InsufficientBalanceException이 발생한다")
        fun `잔액 부족 시 예외 발생`() {
            // Given
            val userId = 1L
            val entity = BalanceEntity(id = 1L, userId = userId, balance = 10_000L)
            given(balanceRepository.findByUserId(userId)).willReturn(entity)

            // When & Then
            assertThrows<InsufficientBalanceException> {
                balanceService.use(
                    BalanceTransaction(userId = userId, amount = 9_901L, type = BalanceTransaction.TransactionType.USE)
                )
            }
        }

        @Test
        @DisplayName("MIN(${BalancePolicy.MIN})원 미만 금액 사용 시 InsufficientBalanceException이 발생한다")
        fun `최소 사용 금액 미만 시 예외 발생`() {
            // Given
            val userId = 1L
            val entity = BalanceEntity(id = 1L, userId = userId, balance = 10_000L)
            given(balanceRepository.findByUserId(userId)).willReturn(entity)

            // When & Then
            assertThrows<InsufficientBalanceException> {
                balanceService.use(
                    BalanceTransaction(userId = userId, amount = (BalancePolicy.MIN - 1).toLong(), type = BalanceTransaction.TransactionType.USE)
                )
            }
        }
    }
}
