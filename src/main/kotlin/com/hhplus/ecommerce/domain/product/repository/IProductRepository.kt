package com.hhplus.ecommerce.domain.product.repository

import com.hhplus.ecommerce.infrastructure.product.dto.BestSellingProduct
import com.hhplus.ecommerce.infrastructure.product.jpa.entity.ProductDetailEntity
import com.hhplus.ecommerce.infrastructure.product.jpa.entity.ProductEntity

interface IProductRepository {
    fun findByProductId(productId: Long): ProductDetailEntity

    fun findById(productId: Long): ProductEntity
    fun findTopFiveLastThreeDays(): List<BestSellingProduct>

    fun insertOrUpdate(entity: ProductEntity): ProductEntity
    fun decreaseStock(productDetailId: Long, amount: Int): ProductDetailEntity
}