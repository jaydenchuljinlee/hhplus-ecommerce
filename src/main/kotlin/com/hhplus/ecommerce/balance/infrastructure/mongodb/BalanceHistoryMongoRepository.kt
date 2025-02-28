package com.hhplus.ecommerce.balance.infrastructure.mongodb

import org.springframework.data.mongodb.repository.MongoRepository

interface BalanceHistoryMongoRepository: MongoRepository<BalanceHistoryDocument, String> {
}