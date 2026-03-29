package com.hhplus.ecommerce.coupon.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.coupon.api.dto.CouponIssueRequest
import com.hhplus.ecommerce.coupon.api.dto.UserCouponResponse
import com.hhplus.ecommerce.coupon.domain.CouponService
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
class CouponController(
    private val couponService: CouponService
) : ICouponController {

    override fun issue(request: CouponIssueRequest): ResponseEntity<CustomApiResponse<UserCouponResponse>> {
        val result = couponService.issue(request.toCommand())
        return ResponseEntity.status(201).body(CustomApiResponse.created(UserCouponResponse.from(result)))
    }

    override fun getUserCoupons(@Min(1) userId: Long): ResponseEntity<CustomApiResponse<List<UserCouponResponse>>> {
        val results = couponService.getUserCoupons(userId).map { UserCouponResponse.from(it) }
        return ResponseEntity.ok(CustomApiResponse.success(results))
    }
}
