package com.hhplus.ecommerce.payment.domain

import com.hhplus.ecommerce.payment.common.PaymentSagaStatus
import com.hhplus.ecommerce.payment.domain.repository.IPaymentSagaRepository
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentSagaService(
    private val sagaRepository: IPaymentSagaRepository
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun start(orderId: Long, userId: Long): PaymentSagaEntity {
        val saga = PaymentSagaEntity(orderId = orderId, userId = userId)
        return sagaRepository.save(saga)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateStatus(
        sagaId: Long,
        status: PaymentSagaStatus,
        paymentId: Long? = null,
        failReason: String? = null
    ) {
        val saga = sagaRepository.findByOrderId(sagaId)
            ?: return
        saga.updateStatus(status, paymentId, failReason)
        sagaRepository.save(saga)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateStatusByOrderId(
        orderId: Long,
        status: PaymentSagaStatus,
        paymentId: Long? = null,
        failReason: String? = null
    ) {
        val saga = sagaRepository.findByOrderId(orderId) ?: return
        saga.updateStatus(status, paymentId, failReason)
        sagaRepository.save(saga)
    }

    @Transactional(readOnly = true)
    fun findAllCompensationFailed(): List<PaymentSagaEntity> =
        sagaRepository.findAllByStatus(PaymentSagaStatus.COMPENSATION_FAILED)
}
