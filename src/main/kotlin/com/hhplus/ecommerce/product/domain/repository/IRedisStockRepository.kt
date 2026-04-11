package com.hhplus.ecommerce.product.domain.repository

interface IRedisStockRepository {
    // Lua 원자 스크립트 방식 (check-and-decrement 한 번에)
    fun reserve(productDetailId: Long, quantity: Int): Boolean
    fun release(productDetailId: Long, quantity: Int)
    // Redisson 락 방식용 plain 연산 (락이 직렬화 보장)
    fun decreaseStock(productDetailId: Long, quantity: Int)
    fun increaseStock(productDetailId: Long, quantity: Int)
    fun getAvailableStock(productDetailId: Long): Int
    fun initializeStock(productDetailId: Long, availableQuantity: Int)
    fun saveReservationInfo(orderId: Long, productDetailId: Long, quantity: Int)
    fun getReservationInfo(orderId: Long): Map<Long, Int>
    fun removeReservationInfo(orderId: Long)
}
