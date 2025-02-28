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
class BalanceEntity(
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
        require(amount >= BalancePolicy.MIN) { throw InsufficientBalanceException("최소 사용 금액은 100원 입니다.") }

        val newBalance = balance - amount

        validateToUse(newBalance)

        balance = newBalance
     }

    fun validateToUse(amount: Long) {
        require(amount >= BalancePolicy.MIN) { throw InsufficientBalanceException() }
    }
}