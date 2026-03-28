package com.hhplus.ecommerce.payment.domain.event

import com.hhplus.ecommerce.payment.domain.dto.PaymentResult

/**
 * 결제 이벤트 발행 인터페이스 (Domain)
 *
 * Domain 서비스가 Infrastructure(Kafka, MongoDB, ObjectMapper)에 직접 의존하지 않도록
 * 이벤트 발행 책임을 추상화한다.
 * 구현체는 Infrastructure 레이어 [com.hhplus.ecommerce.payment.infrastructure.event.PaymentEventPublisher]에 위치한다.
 */
interface IPaymentEventPublisher {
    /**
     * 결제 완료 이벤트 발행
     * @param result 결제 처리 결과
     */
    fun publishPay(result: PaymentResult)
}
