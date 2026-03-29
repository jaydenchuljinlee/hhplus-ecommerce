package com.hhplus.ecommerce.shipment.infrastructure.exception

import com.hhplus.ecommerce.common.exception.NotFoundException

class ShipmentNotFoundException(message: String = "배송 정보를 찾을 수 없습니다.") : NotFoundException(message)
