package com.hhplus.ecommerce.user.domain

import com.hhplus.ecommerce.user.domain.dto.UserQuery
import com.hhplus.ecommerce.user.domain.dto.UserResult
import com.hhplus.ecommerce.user.domain.repository.IUserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: IUserRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(UserService::class.java)
    }

    fun getUserById(item: UserQuery): UserResult {
        val entity = userRepository.findById(item.userId)
        return UserResult.from(entity)
    }

    /**
     * 결제 완료 후 누적 구매 금액을 반영하고 등급을 재산정한다.
     */
    @Transactional
    fun addPurchaseAmount(userId: Long, amount: Long) {
        val entity = userRepository.findById(userId)
        val prevGrade = entity.grade
        entity.addPurchaseAmount(amount)
        userRepository.save(entity)

        if (entity.grade != prevGrade) {
            logger.info("USER:GRADE_UP userId={}, {} → {}", userId, prevGrade, entity.grade)
        }
    }
}