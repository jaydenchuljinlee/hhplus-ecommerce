package com.hhplus.ecommerce.domain.user

import com.hhplus.ecommerce.domain.user.dto.UserQuery
import com.hhplus.ecommerce.domain.user.dto.UserResult
import com.hhplus.ecommerce.infrastructure.user.UserRepository
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