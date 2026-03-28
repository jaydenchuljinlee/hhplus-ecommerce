package com.hhplus.ecommerce.balance.infrastructure.jpa.entity

import com.hhplus.ecommerce.common.enums.StateYn
import com.hhplus.ecommerce.balance.infrastructure.exception.BalanceLimitExceededException
import com.hhplus.ecommerce.balance.infrastructure.exception.InsufficientBalanceException
import com.hhplus.ecommerce.balance.infrastructure.constants.BalancePolicy
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity @Table(name = "balance")
data class BalanceEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @Column(name = "user_id")
    var userId: Long,
    @Column(name = "balance")
    var balance: Long = 0,
    @Column(name = "created_at") @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at") @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var delYn: StateYn = StateYn.N
) {

    fun charge(amount: Long) {
        val newBalance = amount + balance

        require(newBalance <= BalancePolicy.MAX) { throw BalanceLimitExceededException() }

        balance = newBalance
    }

    fun use(amount: Long) {
        require(amount >= BalancePolicy.MIN) { throw InsufficientBalanceException("최소 사용 금액은 ${BalancePolicy.MIN}원 입니다.") }

        val newBalance = balance - amount

        validateRemainingBalance(newBalance)

        balance = newBalance
    }

    /**
     * 차감 후 남은 잔액이 최소 유지 금액(BalancePolicy.MIN) 이상인지 검증
     * @param remainingBalance 차감 후 예상 잔액
     */
    fun validateRemainingBalance(remainingBalance: Long) {
        require(remainingBalance >= BalancePolicy.MIN) { throw InsufficientBalanceException("잔액이 부족합니다. (최소 유지 잔액: ${BalancePolicy.MIN}원)") }
    }
}