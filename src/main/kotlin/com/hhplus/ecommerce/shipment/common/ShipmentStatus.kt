package com.hhplus.ecommerce.shipment.common

enum class ShipmentStatus {
    PREPARING,        // 배송 준비중
    SHIPPED,          // 발송 완료
    IN_TRANSIT,       // 배송중
    DELIVERED,        // 배송 완료
    RETURN_REQUESTED, // 반품 요청
    RETURNED          // 반품 완료
}
