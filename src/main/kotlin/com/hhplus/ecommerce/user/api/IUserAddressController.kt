package com.hhplus.ecommerce.user.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import com.hhplus.ecommerce.user.api.dto.UserAddressRequest
import com.hhplus.ecommerce.user.api.dto.UserAddressResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/user/address")
interface IUserAddressController {

    @Tag(name = "사용자 주소 기능")
    @Operation(summary = "주소 추가")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "주소 추가 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PostMapping
    fun add(@RequestBody request: UserAddressRequest): CustomApiResponse<UserAddressResponse>

    @Tag(name = "사용자 주소 기능")
    @Operation(summary = "주소 수정")
    @PutMapping("{addressId}")
    fun update(
        @PathVariable addressId: Long,
        @RequestBody request: UserAddressRequest
    ): CustomApiResponse<UserAddressResponse>

    @Tag(name = "사용자 주소 기능")
    @Operation(summary = "주소 삭제")
    @DeleteMapping("{addressId}")
    fun delete(@PathVariable addressId: Long): CustomApiResponse<Unit>

    @Tag(name = "사용자 주소 기능")
    @Operation(summary = "주소 목록 조회")
    @GetMapping
    fun getList(@RequestParam userId: Long): CustomApiResponse<List<UserAddressResponse>>
}
