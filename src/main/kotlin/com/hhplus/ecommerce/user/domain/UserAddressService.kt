package com.hhplus.ecommerce.user.domain

import com.hhplus.ecommerce.user.domain.dto.UserAddressResult
import com.hhplus.ecommerce.user.domain.repository.IUserAddressRepository
import com.hhplus.ecommerce.user.infrastructure.jpa.entity.UserAddressEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 사용자 배송지 주소 도메인 서비스
 */
@Service
class UserAddressService(
    private val userAddressRepository: IUserAddressRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(UserAddressService::class.java)
        private const val MAX_ADDRESS_COUNT = 10
    }

    /** 배송지 추가 */
    @Transactional
    fun addAddress(
        userId: Long,
        alias: String,
        receiverName: String,
        phone: String,
        address: String,
        detailAddress: String?,
        zipCode: String,
        isDefault: Boolean = false
    ): UserAddressResult {
        val existingAddresses = userAddressRepository.findByUserId(userId)
        require(existingAddresses.size < MAX_ADDRESS_COUNT) {
            "배송지는 최대 $MAX_ADDRESS_COUNT 개까지 등록할 수 있습니다."
        }

        // 첫 번째 주소이거나 기본 배송지 요청 시 기본 배송지로 설정
        val shouldBeDefault = isDefault || existingAddresses.isEmpty()

        // 기존 기본 배송지 해제
        if (shouldBeDefault) {
            existingAddresses.filter { it.isDefault }.forEach { it.unsetDefault() }
            userAddressRepository.saveAll(existingAddresses.filter { it.isDefault })
        }

        val entity = userAddressRepository.save(
            UserAddressEntity(
                userId = userId,
                alias = alias,
                receiverName = receiverName,
                phone = phone,
                address = address,
                detailAddress = detailAddress,
                zipCode = zipCode,
                isDefault = shouldBeDefault
            )
        )
        logger.info("ADDRESS:ADDED userId={}, addressId={}", userId, entity.id)
        return UserAddressResult.from(entity)
    }

    /** 배송지 수정 */
    @Transactional
    fun updateAddress(
        addressId: Long,
        userId: Long,
        alias: String,
        receiverName: String,
        phone: String,
        address: String,
        detailAddress: String?,
        zipCode: String
    ): UserAddressResult {
        val entity = userAddressRepository.findById(addressId)
        require(entity.userId == userId) { "본인의 배송지만 수정할 수 있습니다." }
        entity.update(alias, receiverName, phone, address, detailAddress, zipCode)
        userAddressRepository.save(entity)
        return UserAddressResult.from(entity)
    }

    /** 기본 배송지 변경 */
    @Transactional
    fun setDefaultAddress(addressId: Long, userId: Long): UserAddressResult {
        val addresses = userAddressRepository.findByUserId(userId)
        addresses.filter { it.isDefault }.forEach { it.unsetDefault() }

        val target = addresses.firstOrNull { it.id == addressId }
            ?: userAddressRepository.findById(addressId).also {
                require(it.userId == userId) { "본인의 배송지만 기본으로 설정할 수 있습니다." }
            }
        target.setAsDefault()
        userAddressRepository.saveAll(addresses)
        return UserAddressResult.from(target)
    }

    /** 배송지 삭제 */
    @Transactional
    fun deleteAddress(addressId: Long, userId: Long) {
        val entity = userAddressRepository.findById(addressId)
        require(entity.userId == userId) { "본인의 배송지만 삭제할 수 있습니다." }
        userAddressRepository.delete(entity)
        logger.info("ADDRESS:DELETED userId={}, addressId={}", userId, addressId)
    }

    /** 사용자 배송지 목록 조회 */
    @Transactional(readOnly = true)
    fun getAddresses(userId: Long): List<UserAddressResult> {
        return userAddressRepository.findByUserId(userId).map { UserAddressResult.from(it) }
    }
}
