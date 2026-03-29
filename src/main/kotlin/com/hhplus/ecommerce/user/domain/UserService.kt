package com.hhplus.ecommerce.user.domain

import com.hhplus.ecommerce.user.domain.dto.UserQuery
import com.hhplus.ecommerce.user.domain.dto.UserResult
import com.hhplus.ecommerce.user.domain.repository.IUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: IUserRepository
) {
    fun getUserById(item: UserQuery): UserResult {
        val entity = userRepository.findById(item.userId)
        return UserResult.from(entity)
    }

    @Transactional
    fun addPurchaseAmount(userId: Long, amount: Long) {
        val entity = userRepository.findById(userId)
        entity.addPurchaseAmount(amount)
        userRepository.save(entity)
    }

    @Transactional
    fun usePoint(userId: Long, amount: Long) {
        val entity = userRepository.findById(userId)
        entity.usePoint(amount)
        userRepository.save(entity)
    }

    @Transactional
    fun chargePoint(userId: Long, amount: Long) {
        val entity = userRepository.findById(userId)
        entity.chargePoint(amount)
        userRepository.save(entity)
    }
}