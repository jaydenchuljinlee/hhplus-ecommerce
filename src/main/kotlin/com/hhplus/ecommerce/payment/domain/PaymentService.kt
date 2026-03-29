package com.hhplus.ecommerce.payment.domain

import com.hhplus.ecommerce.payment.domain.dto.CreationPaymentCommand
import com.hhplus.ecommerce.payment.domain.dto.PaymentResult
import com.hhplus.ecommerce.payment.domain.event.IPaymentEventPublisher
import com.hhplus.ecommerce.payment.domain.repository.IPaymentRepository
import com.hhplus.ecommerce.payment.common.PayMethod
import com.hhplus.ecommerce.payment.common.PayStatus
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentRepository: IPaymentRepository,
    private val paymentEventPublisher: IPaymentEventPublisher,
) {
    @Transactional
    fun pay(dto: CreationPaymentCommand): PaymentResult {
        val paymentEntity = PaymentEntity(
            orderId = dto.orderId,
            status = PayStatus.PAID,
            payMethod = dto.payMethod,
            price = dto.price
        )

        val entity = paymentRepository.insertOrUpdate(paymentEntity)

        val result = PaymentResult(
            paymentId = entity.id,
            userId = dto.userId,
            orderId = entity.orderId,
            status = entity.status,
            price = entity.price,
        )

        paymentEventPublisher.publishPay(result)

        return result
    }
}
