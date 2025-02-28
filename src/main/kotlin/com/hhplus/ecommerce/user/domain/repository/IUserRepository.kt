package com.hhplus.ecommerce.user.domain.repository

import com.hhplus.ecommerce.user.infrastructure.jpa.entity.UserEntity

interface IUserRepository {
    fun findById(userId: Long): UserEntity
}