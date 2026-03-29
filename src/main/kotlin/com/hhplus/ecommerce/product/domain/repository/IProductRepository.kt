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

    /** Soft Reserve: 가용 재고 기준으로 예약 점유 */
    fun reserveStock(productDetailId: Long, amount: Int): ProductDetailEntity

    /** Commit: 결제 완료 후 예약 재고를 실제 수량에서 차감 확정 */
    fun commitStock(productDetailId: Long, amount: Int): ProductDetailEntity

    /** Release: 예약 취소 / 만료 시 임시 점유 해제 */
    fun releaseStock(productDetailId: Long, amount: Int): ProductDetailEntity
}