package com.hhplus.ecommerce.user.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import com.hhplus.ecommerce.user.api.dto.UserAddressRequest
import com.hhplus.ecommerce.user.api.dto.UserAddressResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/user/address")
interface IUserAddressController {

    @Tag(name = "배송지 기능")
    @Operation(summary = "배송지 추가", description = "사용자 배송지를 추가합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "배송지 추가 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "잘못된 요청", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PostMapping
    fun addAddress(
        @Valid @RequestBody request: UserAddressRequest
    ): ResponseEntity<CustomApiResponse<UserAddressResponse>>

    @Tag(name = "배송지 기능")
    @Operation(summary = "배송지 수정", description = "사용자 배송지를 수정합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "배송지 수정 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "잘못된 요청", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "404", description = "배송지 없음", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PutMapping("/{addressId}")
    fun updateAddress(
        @PathVariable @Min(1) addressId: Long,
        @Valid @RequestBody request: UserAddressRequest
    ): ResponseEntity<CustomApiResponse<UserAddressResponse>>

    @Tag(name = "배송지 기능")
    @Operation(summary = "배송지 삭제", description = "사용자 배송지를 삭제합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "배송지 삭제 성공"),
        ApiResponse(responseCode = "404", description = "배송지 없음", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @DeleteMapping("/{addressId}")
    fun deleteAddress(
        @PathVariable @Min(1) addressId: Long,
        @Parameter(description = "사용자 ID", required = true)
        @RequestParam @Min(1) userId: Long
    ): ResponseEntity<Void>

    @Tag(name = "배송지 기능")
    @Operation(summary = "배송지 목록 조회", description = "사용자 배송지 목록을 조회합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "배송지 목록 조회 성공", useReturnTypeSchema = true),
    ])
    @GetMapping
    fun getAddresses(
        @Parameter(description = "사용자 ID", required = true)
        @RequestParam @Min(1) userId: Long
    ): ResponseEntity<CustomApiResponse<List<UserAddressResponse>>>
}
