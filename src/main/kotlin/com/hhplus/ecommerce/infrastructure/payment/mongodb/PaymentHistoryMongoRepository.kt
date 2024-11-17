package com.hhplus.ecommerce.infrastructure.payment.mongodb

import org.springframework.data.mongodb.repository.MongoRepository

interface PaymentHistoryMongoRepository: MongoRepository<PaymentHistoryDocument, String> {
}