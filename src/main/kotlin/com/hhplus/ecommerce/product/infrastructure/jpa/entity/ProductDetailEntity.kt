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

    @Column(name = "created_at") @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at") @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var delYn: StateYn = StateYn.N
) {
    fun decrease(amount: Int) {
        val newQuantity = quantity - amount

        require(newQuantity >= 0) { throw OutOfStockException() }

        quantity = newQuantity
    }

}