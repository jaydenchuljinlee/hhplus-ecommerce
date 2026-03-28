package com.hhplus.ecommerce.balance.domain.event

/**
 * 잔액 이벤트 발행 인터페이스 (Domain)
 *
 * Domain 서비스가 Infrastructure(Kafka, MongoDB, ObjectMapper)에 직접 의존하지 않도록
 * 이벤트 발행 책임을 추상화한다.
 * 구현체는 Infrastructure 레이어 [com.hhplus.ecommerce.balance.infrastructure.event.BalanceEventPublisher]에 위치한다.
 */
interface IBalanceEventPublisher {
    /**
     * 잔액 충전 이벤트 발행
     * @param balanceId 잔액 레코드 ID
     * @param amount    충전 금액
     * @param balance   충전 후 잔액
     */
    fun publishCharge(balanceId: Long, amount: Long, balance: Long)

    /**
     * 잔액 사용 이벤트 발행
     * @param balanceId 잔액 레코드 ID
     * @param amount    사용 금액
     * @param balance   사용 후 잔액
     */
    fun publishUse(balanceId: Long, amount: Long, balance: Long)
}
