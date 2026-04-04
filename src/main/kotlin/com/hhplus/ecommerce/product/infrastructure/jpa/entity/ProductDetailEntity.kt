package com.hhplus.ecommerce.product.infrastructure.jpa.entity

import com.hhplus.ecommerce.common.enums.StateYn
import com.hhplus.ecommerce.product.infrastructure.exception.OutOfStockException
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity @Table(name = "product_detail")
class ProductDetailEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @Column(name = "product_id")
    var productId: Long,
    @Column(name = "product_option_id")
    var productOptionId: Long,
    @Column(name = "quantity")
    var quantity: Int,

    @Column(name = "reserved_quantity")
    var reservedQuantity: Int = 0,

    @Column(name = "created_at") @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at") @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var delYn: StateYn = StateYn.N
) {
    val availableQuantity: Int
        get() = quantity - reservedQuantity

    fun decrease(amount: Int) {
        val newQuantity = quantity - amount

        require(newQuantity >= 0) { throw OutOfStockException() }

        quantity = newQuantity
    }

    fun reserve(amount: Int) {
        if (amount > availableQuantity) throw OutOfStockException()
        reservedQuantity += amount
    }

    fun commit(amount: Int) {
        require(amount <= reservedQuantity) { "commit 수량($amount)이 예약 수량($reservedQuantity)을 초과합니다." }
        reservedQuantity -= amount
        quantity -= amount
    }

    fun decreaseQuantity(amount: Int) {
        require(amount <= quantity) { "감소 수량($amount)이 실재고($quantity)를 초과합니다." }
        quantity -= amount
    }

    fun release(amount: Int) {
        require(amount <= reservedQuantity) { "release 수량($amount)이 예약 수량($reservedQuantity)을 초과합니다." }
        reservedQuantity -= amount
    }

}