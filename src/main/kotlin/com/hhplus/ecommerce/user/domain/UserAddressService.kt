package com.hhplus.ecommerce.user.domain

import com.hhplus.ecommerce.user.domain.dto.UserAddressResult
import com.hhplus.ecommerce.user.domain.repository.IUserAddressRepository
import com.hhplus.ecommerce.user.infrastructure.jpa.entity.UserAddressEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAddressService(
    private val userAddressRepository: IUserAddressRepository
) {
    @Transactional
    fun add(
        userId: Long,
        alias: String,
        receiverName: String,
        phone: String,
        address: String,
        zipCode: String,
        isDefault: Boolean
    ): UserAddressResult {
        if (isDefault) {
            unsetCurrentDefault(userId)
        }
        val entity = UserAddressEntity(
            userId = userId,
            alias = alias,
            receiverName = receiverName,
            phone = phone,
            address = address,
            zipCode = zipCode,
            isDefault = isDefault
        )
        return UserAddressResult.from(userAddressRepository.save(entity))
    }

    @Transactional
    fun update(
        addressId: Long,
        alias: String,
        receiverName: String,
        phone: String,
        address: String,
        zipCode: String,
        isDefault: Boolean
    ): UserAddressResult {
        val entity = userAddressRepository.findById(addressId)
        if (isDefault && !entity.isDefault) {
            unsetCurrentDefault(entity.userId)
        }
        entity.update(alias, receiverName, phone, address, zipCode)
        if (isDefault) entity.setDefault() else entity.unsetDefault()
        return UserAddressResult.from(userAddressRepository.save(entity))
    }

    @Transactional
    fun delete(addressId: Long) {
        val entity = userAddressRepository.findById(addressId)
        userAddressRepository.delete(entity)
    }

    @Transactional(readOnly = true)
    fun getList(userId: Long): List<UserAddressResult> =
        userAddressRepository.findAllByUserId(userId).map { UserAddressResult.from(it) }

    private fun unsetCurrentDefault(userId: Long) {
        val current = userAddressRepository.findDefaultByUserId(userId)
        current?.let {
            it.unsetDefault()
            userAddressRepository.save(it)
        }
    }
}
