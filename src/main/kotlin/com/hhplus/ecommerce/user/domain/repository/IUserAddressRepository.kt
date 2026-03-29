package com.hhplus.ecommerce.user.domain.repository

import com.hhplus.ecommerce.user.infrastructure.jpa.entity.UserAddressEntity

interface IUserAddressRepository {
    fun save(entity: UserAddressEntity): UserAddressEntity
    fun findById(addressId: Long): UserAddressEntity
    fun findByUserId(userId: Long): List<UserAddressEntity>
    fun findDefaultByUserId(userId: Long): UserAddressEntity?
    fun delete(entity: UserAddressEntity)
    fun saveAll(entities: List<UserAddressEntity>): List<UserAddressEntity>
}
