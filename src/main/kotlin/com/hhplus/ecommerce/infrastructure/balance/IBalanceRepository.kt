package com.hhplus.ecommerce.infrastructure.balance

import com.hhplus.ecommerce.infrastructure.balance.jpa.entity.BalanceEntity

interface IBalanceRepository {
    fun findByUserIdWithLock(userId: Long): BalanceEntity

    fun findByUserId(userId: Long): BalanceEntity

    fun insertOrUpdate(entity: BalanceEntity): BalanceEntity
}