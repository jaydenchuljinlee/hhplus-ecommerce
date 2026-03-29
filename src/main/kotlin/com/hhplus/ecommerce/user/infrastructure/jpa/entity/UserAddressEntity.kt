package com.hhplus.ecommerce.user.infrastructure.jpa.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_address")
class UserAddressEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "alias", nullable = false)
    var alias: String,

    @Column(name = "receiver_name", nullable = false)
    var receiverName: String,

    @Column(name = "phone", nullable = false)
    var phone: String,

    @Column(name = "address", nullable = false)
    var address: String,

    @Column(name = "zip_code", nullable = false)
    var zipCode: String,

    @Column(name = "is_default", nullable = false)
    var isDefault: Boolean = false,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun update(alias: String, receiverName: String, phone: String, address: String, zipCode: String) {
        this.alias = alias
        this.receiverName = receiverName
        this.phone = phone
        this.address = address
        this.zipCode = zipCode
        this.updatedAt = LocalDateTime.now()
    }

    fun setDefault() {
        this.isDefault = true
        this.updatedAt = LocalDateTime.now()
    }

    fun unsetDefault() {
        this.isDefault = false
        this.updatedAt = LocalDateTime.now()
    }
}
