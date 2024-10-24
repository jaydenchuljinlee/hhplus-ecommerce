package com.hhplus.ecommerce.domain.user.repository

import com.hhplus.ecommerce.infrastructure.user.jpa.entity.UserEntity

interface IUserRepository {
    fun findById(userId: Long): UserEntity
}