package com.hhplus.ecommerce.product.infrastructure.jpa.querydsl

import com.hhplus.ecommerce.product.infrastructure.dto.BestSellingProduct

interface IProductQueryDsl {
    fun findTop5BestSellingProductsLast3Days(): List<BestSellingProduct>
}