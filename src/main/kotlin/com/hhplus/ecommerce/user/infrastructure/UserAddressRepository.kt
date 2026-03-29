package com.hhplus.ecommerce.user.infrastructure

import com.hhplus.ecommerce.user.domain.repository.IUserAddressRepository
import com.hhplus.ecommerce.user.infrastructure.exception.UserAddressNotFoundException
import com.hhplus.ecommerce.user.infrastructure.jpa.UserAddressJpaRepository
import com.hhplus.ecommerce.user.infrastructure.jpa.entity.UserAddressEntity
import org.springframework.stereotype.Repository

@Repository
class UserAddressRepository(
    private val userAddressJpaRepository: UserAddressJpaRepository
) : IUserAddressRepository {

    override fun save(entity: UserAddressEntity): UserAddressEntity =
        userAddressJpaRepository.save(entity)

    override fun findById(addressId: Long): UserAddressEntity =
        userAddressJpaRepository.findById(addressId)
            .orElseThrow { UserAddressNotFoundException() }

    override fun findByUserId(userId: Long): List<UserAddressEntity> =
        userAddressJpaRepository.findByUserId(userId)

    override fun findDefaultByUserId(userId: Long): UserAddressEntity? =
        userAddressJpaRepository.findByUserIdAndIsDefault(userId, true)

    override fun delete(entity: UserAddressEntity) =
        userAddressJpaRepository.delete(entity)

    override fun saveAll(entities: List<UserAddressEntity>): List<UserAddressEntity> =
        userAddressJpaRepository.saveAll(entities)
}
