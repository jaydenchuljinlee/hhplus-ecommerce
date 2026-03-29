package com.hhplus.ecommerce.user.domain.repository

import com.hhplus.ecommerce.user.infrastructure.jpa.entity.UserAddressEntity

interface IUserAddressRepository {
    fun findById(id: Long): UserAddressEntity
    fun findAllByUserId(userId: Long): List<UserAddressEntity>
    fun findDefaultByUserId(userId: Long): UserAddressEntity?
    fun save(entity: UserAddressEntity): UserAddressEntity
    fun delete(entity: UserAddressEntity)
}
