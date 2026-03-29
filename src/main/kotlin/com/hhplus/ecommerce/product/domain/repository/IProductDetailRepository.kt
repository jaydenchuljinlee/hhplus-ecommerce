package com.hhplus.ecommerce.product.domain.repository

import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductDetailEntity

interface IProductDetailRepository {
    fun findById(productDetailId: Long): ProductDetailEntity
    fun findByIdForUpdate(productDetailId: Long): ProductDetailEntity = findById(productDetailId)
    fun save(entity: ProductDetailEntity?): ProductDetailEntity
}
