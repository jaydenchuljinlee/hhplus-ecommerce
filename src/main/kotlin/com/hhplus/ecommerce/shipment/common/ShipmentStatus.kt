package com.hhplus.ecommerce.shipment.common

/**
 * 배송 상태
 *
 * PREPARING        : 출고 준비 중
 * SHIPPED          : 배송 시작 (택배사 접수)
 * IN_TRANSIT       : 배송 중
 * DELIVERED        : 배송 완료
 * RETURN_REQUESTED : 반품 신청
 * RETURNED         : 반품 완료
 */
enum class ShipmentStatus {
    PREPARING,
    SHIPPED,
    IN_TRANSIT,
    DELIVERED,
    RETURN_REQUESTED,
    RETURNED
}
