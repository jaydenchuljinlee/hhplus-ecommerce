package com.hhplus.ecommerce.user.domain

import com.hhplus.ecommerce.user.domain.dto.UserQuery
import com.hhplus.ecommerce.user.domain.dto.UserResult
import com.hhplus.ecommerce.user.infrastructure.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun getUserById(item: UserQuery): UserResult {
        val entity = userRepository.findById(item.userId)
        return UserResult.from(entity)
    }
}