package com.hhplus.ecommerce.payment.infrastructure.mongodb

import org.springframework.data.mongodb.repository.MongoRepository

interface PaymentHistoryMongoRepository: MongoRepository<PaymentHistoryDocument, String> {
}