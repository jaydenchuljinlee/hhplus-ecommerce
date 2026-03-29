package com.hhplus.ecommerce.shipment.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import com.hhplus.ecommerce.shipment.api.dto.ShipmentResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/shipment")
interface IShipmentController {

    @Tag(name = "배송 기능")
    @Operation(summary = "배송 조회 API", description = "주문 ID로 배송 정보를 조회하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "배송 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "잘못된 요청", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "404", description = "배송 정보 없음", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping("/order/{orderId}")
    fun getShipmentByOrderId(
        @Parameter(description = "주문 ID", required = true, example = "1")
        @PathVariable @Min(1) orderId: Long
    ): ResponseEntity<CustomApiResponse<ShipmentResponse>>
}
