package com.hhplus.ecommerce.infrastructure.user

import com.hhplus.ecommerce.infrastructure.user.jpa.entity.UserEntity

interface IUserRepository {
    fun findById(userId: Long): UserEntity
}