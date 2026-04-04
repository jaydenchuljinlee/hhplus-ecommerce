package com.hhplus.ecommerce.product.domain.repository

interface IRedisStockRepository {
    fun reserve(productDetailId: Long, quantity: Int): Boolean
    fun release(productDetailId: Long, quantity: Int)
    fun getAvailableStock(productDetailId: Long): Int
    fun initializeStock(productDetailId: Long, availableQuantity: Int)
    fun saveReservationInfo(orderId: Long, productDetailId: Long, quantity: Int)
    fun getReservationInfo(orderId: Long): Map<Long, Int>
    fun removeReservationInfo(orderId: Long)
}
