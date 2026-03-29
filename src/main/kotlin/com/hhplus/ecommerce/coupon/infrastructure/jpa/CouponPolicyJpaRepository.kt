package com.hhplus.ecommerce.coupon.infrastructure.jpa

import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.CouponPolicyEntity
import com.hhplus.ecommerce.common.enums.StateYn
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface CouponPolicyJpaRepository : JpaRepository<CouponPolicyEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CouponPolicyEntity c WHERE c.id = :id")
    fun findByIdWithLock(id: Long): CouponPolicyEntity?

    fun findByExpiredAtBeforeAndDelYn(expiredAt: LocalDateTime, delYn: StateYn): List<CouponPolicyEntity>
}
