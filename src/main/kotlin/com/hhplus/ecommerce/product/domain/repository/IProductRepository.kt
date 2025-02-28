package com.hhplus.ecommerce.product.domain.repository

import com.hhplus.ecommerce.product.infrastructure.dto.BestSellingProduct
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductDetailEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductEntity

interface IProductRepository {
    fun findByProductId(productId: Long): ProductDetailEntity

    fun findById(productId: Long): ProductEntity
    fun findTopFiveLastThreeDays(): List<BestSellingProduct>

    fun insertOrUpdate(entity: ProductEntity): ProductEntity
    fun decreaseStock(productDetailId: Long, amount: Int): ProductDetailEntity
}