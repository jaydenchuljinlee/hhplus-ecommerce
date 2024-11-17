package com.hhplus.ecommerce.infrastructure.balance.mongodb

import org.springframework.data.mongodb.repository.MongoRepository

interface BalanceHistoryMongoRepository: MongoRepository<BalanceHistoryDocument, String> {
}