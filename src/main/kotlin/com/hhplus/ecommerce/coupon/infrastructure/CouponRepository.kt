package com.hhplus.ecommerce.coupon.infrastructure

import com.hhplus.ecommerce.coupon.domain.repository.ICouponRepository
import com.hhplus.ecommerce.coupon.infrastructure.exception.CouponNotFoundException
import com.hhplus.ecommerce.coupon.infrastructure.jpa.CouponPolicyJpaRepository
import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.CouponPolicyEntity
import org.springframework.stereotype.Repository

@Repository
class CouponRepository(
    private val couponPolicyJpaRepository: CouponPolicyJpaRepository
) : ICouponRepository {

    override fun findById(couponPolicyId: Long): CouponPolicyEntity {
        return couponPolicyJpaRepository.findById(couponPolicyId)
            .orElseThrow { CouponNotFoundException() }
    }

    override fun findByIdWithLock(couponPolicyId: Long): CouponPolicyEntity {
        return couponPolicyJpaRepository.findByIdWithLock(couponPolicyId)
            ?: throw CouponNotFoundException()
    }

    override fun save(entity: CouponPolicyEntity): CouponPolicyEntity {
        return couponPolicyJpaRepository.save(entity)
    }
}
