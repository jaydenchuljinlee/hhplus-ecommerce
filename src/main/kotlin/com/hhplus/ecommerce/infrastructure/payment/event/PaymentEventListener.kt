package com.hhplus.ecommerce.infrastructure.payment.event

import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryDocument
import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryMongoRepository
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class PaymentEventListener(
    private val paymentHistoryMongoRepository: PaymentHistoryMongoRepository
) {
    private val logger = LoggerFactory.getLogger(PaymentEventListener::class.java)

    @Async
    @EventListener
    fun listen(event: PaymentHistoryDocument) {
        try {
            logger.info("PAY:EVENT:LISTENER:$event", )

            // 에러가 발생해도 잘 동작하는지 테스트하기 위함
            if (event.id == "ERROR") {
                throw IllegalArgumentException("ID가 올바르지 않습니다.")
            }

            // 외부 MongoDB에 이력 데이터를 저장
            paymentHistoryMongoRepository.save(event)
        } catch (e: Exception) {
            logger.error("PAY:EVENT:LISTENER:ERROR:${event.id}", e)
        }
    }
}