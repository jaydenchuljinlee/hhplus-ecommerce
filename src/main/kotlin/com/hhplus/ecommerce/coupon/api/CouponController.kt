package com.hhplus.ecommerce.coupon.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.coupon.api.dto.CouponIssueRequest
import com.hhplus.ecommerce.coupon.api.dto.UserCouponResponse
import com.hhplus.ecommerce.coupon.domain.CouponService
import org.springframework.web.bind.annotation.RestController

@RestController
class CouponController(
    private val couponService: CouponService
) : ICouponController {

    override fun issue(couponPolicyId: Long, request: CouponIssueRequest): CustomApiResponse<UserCouponResponse> {
        val result = couponService.issue(request.userId, couponPolicyId)
        return CustomApiResponse.success(UserCouponResponse.from(result))
    }

    override fun getMyCoupons(userId: Long): CustomApiResponse<List<UserCouponResponse>> {
        val results = couponService.getMyCoupons(userId)
        return CustomApiResponse.success(results.map { UserCouponResponse.from(it) })
    }
}
