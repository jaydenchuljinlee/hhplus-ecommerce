package com.hhplus.ecommerce.common.schedular

import com.hhplus.ecommerce.payment.domain.repository.IPaymentSagaRepository
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaStatus
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 보상 트랜잭션 실패 Saga 복구 알림 스케줄러
 *
 * COMPENSATION_FAILED 상태인 Saga를 주기적으로 감지하여
 * 운영자에게 경고 로그를 발행한다. (수동 처리 큐)
 *
 * 실행 주기: 매 10분
 */
@Component
class PaymentSagaRecoveryScheduler(
    private val paymentSagaRepository: IPaymentSagaRepository
) {
    private val logger = LoggerFactory.getLogger(PaymentSagaRecoveryScheduler::class.java)

    @Scheduled(fixedDelay = 10 * 60 * 1000L) // 10분마다 실행
    fun alertCompensationFailed() {
        val failedSagas = paymentSagaRepository.findAllByStatus(PaymentSagaStatus.COMPENSATION_FAILED)

        if (failedSagas.isEmpty()) return

        failedSagas.forEach { saga ->
            logger.warn(
                "[수동 처리 필요] SAGA:COMPENSATION_FAILED — sagaId={}, orderId={}, userId={}, failReason={}",
                saga.id, saga.orderId, saga.userId, saga.failReason
            )
        }

        logger.warn(
            "SAGA:RECOVERY:ALERT — COMPENSATION_FAILED 건수: {}건. 즉시 확인 바랍니다.",
            failedSagas.size
        )
    }
}
