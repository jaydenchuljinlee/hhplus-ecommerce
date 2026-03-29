package com.hhplus.ecommerce.coupon.infrastructure.jpa

import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.CouponPolicyEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import jakarta.persistence.LockModeType

interface CouponPolicyJpaRepository : JpaRepository<CouponPolicyEntity, Long> {

    /** 선착순 발급 시 비관적 락으로 조회 (Redis Lock과 이중 보호) */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CouponPolicyEntity c WHERE c.id = :id")
    fun findByIdWithLock(@Param("id") id: Long): CouponPolicyEntity?
}
