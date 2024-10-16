package com.hhplus.ecommerce.infrastructure.product

import com.hhplus.ecommerce.infrastructure.product.dto.BestSellingProduct
import com.hhplus.ecommerce.infrastructure.product.jpa.entity.ProductDetailEntity
import com.hhplus.ecommerce.infrastructure.product.jpa.entity.ProductEntity

interface IProductRepository {
    fun findByProductIdWithLock(productId: Long): ProductDetailEntity
    fun findById(productId: Long): ProductEntity
    fun insertOrUpdate(entity: ProductEntity): ProductEntity
    fun findTopFiveLastThreeDays(): List<BestSellingProduct>

    fun decreaseStock(productDetailId: Long, amount: Int): ProductDetailEntity
}