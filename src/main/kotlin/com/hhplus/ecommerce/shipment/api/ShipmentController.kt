package com.hhplus.ecommerce.shipment.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.shipment.api.dto.ShipmentResponse
import com.hhplus.ecommerce.shipment.domain.ShipmentService
import org.springframework.web.bind.annotation.RestController

@RestController
class ShipmentController(
    private val shipmentService: ShipmentService
) : IShipmentController {

    override fun getShipment(orderId: Long): CustomApiResponse<ShipmentResponse> {
        val result = shipmentService.getShipment(orderId)
        return CustomApiResponse.success(ShipmentResponse.from(result))
    }
}
