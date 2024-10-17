package com.hhplus.ecommerce.infrastructure.user.jpa

import com.hhplus.ecommerce.common.exception.user.UserNotFoundException
import com.hhplus.ecommerce.infrastructure.user.IUserRepository
import com.hhplus.ecommerce.infrastructure.user.jpa.entity.UserEntity
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val userJpaRepository: UserJpaRepository
): IUserRepository {
    override fun findById(userId: Long): UserEntity {
        return userJpaRepository.findById(userId).orElseThrow { UserNotFoundException() }
    }
}