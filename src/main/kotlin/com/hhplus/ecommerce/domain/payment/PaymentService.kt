package com.hhplus.ecommerce.domain.payment

import com.hhplus.ecommerce.domain.payment.dto.CreationPaymentCommand
import com.hhplus.ecommerce.domain.payment.dto.ExternalCallRequest
import com.hhplus.ecommerce.domain.payment.dto.PaymentResult
import com.hhplus.ecommerce.infrastructure.payment.IPaymentHistoryRepository
import com.hhplus.ecommerce.infrastructure.payment.IPaymentRepository
import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentEntity
import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentHistoryEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentRepository: IPaymentRepository,
    private val paymentHistoryRepository: IPaymentHistoryRepository,
    private val externalApiService: ExternalApiService
) {
    @Transactional
    fun pay(dto: CreationPaymentCommand): PaymentResult {
        val paymentEntity = PaymentEntity(
            orderId = dto.orderId,
            status = "PAYMENT_COMPLETED",
            price = dto.price
        )

        val entity = paymentRepository.insertOrUpdate(paymentEntity)

        val paymentHistoryEntity = PaymentHistoryEntity(
            paymentId = entity.id,
            userId = dto.userId,
            price = entity.price,
            status = entity.status
        )

        paymentHistoryRepository.insertOrUpdate(paymentHistoryEntity)

        // 외부 연동
        val externalRequest = ExternalCallRequest(
            payId = entity.id,
            price = entity.price,
            userId = dto.userId,
        )
        // externalApiService.call(externalRequest)

        return PaymentResult.from(entity)
    }
}