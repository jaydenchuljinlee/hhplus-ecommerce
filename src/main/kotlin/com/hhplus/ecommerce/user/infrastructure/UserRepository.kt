package com.hhplus.ecommerce.user.infrastructure

import com.hhplus.ecommerce.user.infrastructure.exception.UserNotFoundException
import com.hhplus.ecommerce.user.domain.repository.IUserRepository
import com.hhplus.ecommerce.user.infrastructure.jpa.UserJpaRepository
import com.hhplus.ecommerce.user.infrastructure.jpa.entity.UserEntity
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val userJpaRepository: UserJpaRepository
): IUserRepository {
    override fun findById(userId: Long): UserEntity {
        return userJpaRepository.findById(userId).orElseThrow { UserNotFoundException() }
    }
}