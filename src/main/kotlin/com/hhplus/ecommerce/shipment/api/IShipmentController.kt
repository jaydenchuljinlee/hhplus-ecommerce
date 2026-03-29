package com.hhplus.ecommerce.shipment.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import com.hhplus.ecommerce.shipment.api.dto.ShipmentResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("shipment")
interface IShipmentController {

    @Tag(name = "배송 기능")
    @Operation(summary = "배송 조회 API", description = "주문 ID로 배송 현황을 조회합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "배송 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "404", description = "배송 정보 없음", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping("{orderId}")
    fun getShipment(@PathVariable orderId: Long): CustomApiResponse<ShipmentResponse>
}
