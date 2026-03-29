package com.hhplus.ecommerce.user.infrastructure.jpa.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

/**
 * 사용자 배송지 주소 엔티티
 */
@Entity
@Table(
    name = "user_address",
    indexes = [Index(name = "idx_user_address_user_id", columnList = "user_id")]
)
class UserAddressEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    /** 주소 별칭 (ex. 집, 회사) */
    @Column(name = "alias", nullable = false, length = 30)
    var alias: String,

    /** 수령인 이름 */
    @Column(name = "receiver_name", nullable = false, length = 50)
    var receiverName: String,

    /** 수령인 연락처 */
    @Column(name = "phone", nullable = false, length = 20)
    var phone: String,

    /** 기본 주소 (도로명 또는 지번) */
    @Column(name = "address", nullable = false, length = 200)
    var address: String,

    /** 상세 주소 */
    @Column(name = "detail_address", length = 100)
    var detailAddress: String? = null,

    /** 우편번호 */
    @Column(name = "zip_code", nullable = false, length = 10)
    var zipCode: String,

    /** 기본 배송지 여부 */
    @Column(name = "is_default", nullable = false)
    var isDefault: Boolean = false,

    @Column(name = "created_at") @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at") @LastModifiedDate
    var updatedAt: LocalDateTime? = null
) {
    fun update(
        alias: String,
        receiverName: String,
        phone: String,
        address: String,
        detailAddress: String?,
        zipCode: String
    ) {
        this.alias = alias
        this.receiverName = receiverName
        this.phone = phone
        this.address = address
        this.detailAddress = detailAddress
        this.zipCode = zipCode
        this.updatedAt = LocalDateTime.now()
    }

    fun setAsDefault() {
        this.isDefault = true
        this.updatedAt = LocalDateTime.now()
    }

    fun unsetDefault() {
        this.isDefault = false
        this.updatedAt = LocalDateTime.now()
    }
}
