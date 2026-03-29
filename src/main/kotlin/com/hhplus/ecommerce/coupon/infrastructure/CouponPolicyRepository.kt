package com.hhplus.ecommerce.coupon.infrastructure

import com.hhplus.ecommerce.common.enums.StateYn
import com.hhplus.ecommerce.coupon.domain.repository.ICouponPolicyRepository
import com.hhplus.ecommerce.coupon.infrastructure.exception.CouponNotFoundException
import com.hhplus.ecommerce.coupon.infrastructure.jpa.CouponPolicyJpaRepository
import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.CouponPolicyEntity
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CouponPolicyRepository(
    private val jpaRepository: CouponPolicyJpaRepository
) : ICouponPolicyRepository {

    override fun findById(id: Long): CouponPolicyEntity =
        jpaRepository.findById(id).orElseThrow { CouponNotFoundException("쿠폰 정책을 찾을 수 없습니다. id=$id") }

    override fun findByIdWithLock(id: Long): CouponPolicyEntity =
        jpaRepository.findByIdWithLock(id) ?: throw CouponNotFoundException("쿠폰 정책을 찾을 수 없습니다. id=$id")

    override fun findExpiredPolicies(now: LocalDateTime): List<CouponPolicyEntity> =
        jpaRepository.findByExpiredAtBeforeAndDelYn(now, StateYn.N)

    override fun save(entity: CouponPolicyEntity): CouponPolicyEntity =
        jpaRepository.save(entity)
}
