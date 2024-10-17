package com.hhplus.ecommerce.infrastructure.user.jpa

import com.hhplus.ecommerce.infrastructure.user.jpa.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository: JpaRepository<UserEntity, Long> {
}