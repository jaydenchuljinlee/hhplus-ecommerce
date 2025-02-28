package com.hhplus.ecommerce.balance.domain.respository

import com.hhplus.ecommerce.balance.infrastructure.jpa.entity.BalanceEntity

interface IBalanceRepository {
    fun findByUserIdWithLock(userId: Long): BalanceEntity

    fun findByUserId(userId: Long): BalanceEntity

    fun insertOrUpdate(entity: BalanceEntity): BalanceEntity
}