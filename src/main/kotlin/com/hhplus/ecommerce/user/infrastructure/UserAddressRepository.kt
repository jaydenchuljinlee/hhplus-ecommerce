package com.hhplus.ecommerce.user.infrastructure

import com.hhplus.ecommerce.user.domain.repository.IUserAddressRepository
import com.hhplus.ecommerce.user.infrastructure.exception.UserAddressNotFoundException
import com.hhplus.ecommerce.user.infrastructure.jpa.UserAddressJpaRepository
import com.hhplus.ecommerce.user.infrastructure.jpa.entity.UserAddressEntity
import org.springframework.stereotype.Repository

@Repository
class UserAddressRepository(
    private val jpaRepository: UserAddressJpaRepository
) : IUserAddressRepository {

    override fun findById(id: Long): UserAddressEntity =
        jpaRepository.findById(id).orElseThrow { UserAddressNotFoundException("주소를 찾을 수 없습니다. id=$id") }

    override fun findAllByUserId(userId: Long): List<UserAddressEntity> =
        jpaRepository.findAllByUserId(userId)

    override fun findDefaultByUserId(userId: Long): UserAddressEntity? =
        jpaRepository.findByUserIdAndIsDefault(userId, true)

    override fun save(entity: UserAddressEntity): UserAddressEntity =
        jpaRepository.save(entity)

    override fun delete(entity: UserAddressEntity) =
        jpaRepository.delete(entity)
}
