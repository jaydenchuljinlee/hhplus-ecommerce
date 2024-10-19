package com.hhplus.ecommerce.infrastructure.product.jpa.querydsl

import com.hhplus.ecommerce.infrastructure.product.dto.BestSellingProduct

interface IProductQueryDsl {
    fun findTop5BestSellingProductsLast3Days(): List<BestSellingProduct>
}