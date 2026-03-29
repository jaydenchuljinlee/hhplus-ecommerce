package com.hhplus.ecommerce.common.schedular

import com.hhplus.ecommerce.payment.domain.PaymentSagaService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PaymentSagaRecoveryScheduler(
    private val paymentSagaService: PaymentSagaService
) {
    private val logger = LoggerFactory.getLogger(PaymentSagaRecoveryScheduler::class.java)

    @Scheduled(cron = "0 */5 * * * *")
    fun recoverFailedSagas() {
        val failedSagas = paymentSagaService.findAllCompensationFailed()
        if (failedSagas.isNotEmpty()) {
            failedSagas.forEach {
                logger.warn("수동 처리 필요 - sagaId=${it.id}, orderId=${it.orderId}, userId=${it.userId}, failReason=${it.failReason}")
            }
        }
    }
}
