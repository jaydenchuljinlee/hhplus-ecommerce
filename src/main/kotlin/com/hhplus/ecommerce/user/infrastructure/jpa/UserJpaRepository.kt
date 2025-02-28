package com.hhplus.ecommerce.user.infrastructure.jpa

import com.hhplus.ecommerce.user.infrastructure.jpa.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository: JpaRepository<UserEntity, Long> {
}