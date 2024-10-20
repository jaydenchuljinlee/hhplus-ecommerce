package com.hhplus.ecommerce.domain.balance.respository

import com.hhplus.ecommerce.infrastructure.balance.jpa.entity.BalanceEntity

interface IBalanceRepository {
    fun findByUserIdWithLock(userId: Long): BalanceEntity

    fun findByUserId(userId: Long): BalanceEntity

    fun insertOrUpdate(entity: BalanceEntity): BalanceEntity
}