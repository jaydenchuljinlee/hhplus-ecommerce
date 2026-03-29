package com.hhplus.ecommerce.user.infrastructure.jpa

import com.hhplus.ecommerce.user.infrastructure.jpa.entity.UserAddressEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserAddressJpaRepository : JpaRepository<UserAddressEntity, Long> {
    fun findAllByUserId(userId: Long): List<UserAddressEntity>
    fun findByUserIdAndIsDefault(userId: Long, isDefault: Boolean): UserAddressEntity?
}
