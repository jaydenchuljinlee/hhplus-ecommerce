package com.hhplus.ecommerce.shipment.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.shipment.api.dto.ShipmentResponse
import com.hhplus.ecommerce.shipment.domain.ShipmentService
import com.hhplus.ecommerce.shipment.domain.dto.ShipmentResult
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
class ShipmentController(
    private val shipmentService: ShipmentService
) : IShipmentController {

    override fun getShipmentByOrderId(@Min(1) orderId: Long): ResponseEntity<CustomApiResponse<ShipmentResponse>> {
        val shipment = shipmentService.getShipmentByOrderId(orderId)
        val result = ShipmentResult.from(shipment)
        return ResponseEntity.ok(CustomApiResponse.success(ShipmentResponse.from(result)))
    }
}
