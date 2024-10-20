package com.hhplus.ecommerce.infrastructure.user

import com.hhplus.ecommerce.infrastructure.user.exception.UserNotFoundException
import com.hhplus.ecommerce.domain.user.repository.IUserRepository
import com.hhplus.ecommerce.infrastructure.user.jpa.UserJpaRepository
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