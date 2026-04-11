package com.hhplus.ecommerce.product.infrastructure.redis

object StockLuaScripts {

    /**
     * 가용 재고 검증 + 차감을 원자적으로 수행
     * KEYS[1]: stock:available:{productDetailId}
     * ARGV[1]: 요청 수량
     * 반환: 1 (성공), 0 (재고 부족)
     */
    val RESERVE = """
        local available = tonumber(redis.call('GET', KEYS[1]) or '0')
        local requested = tonumber(ARGV[1])
        if available >= requested then
            redis.call('DECRBY', KEYS[1], requested)
            return 1
        end
        return 0
    """.trimIndent()

    /**
     * 가용 재고 복원
     * KEYS[1]: stock:available:{productDetailId}
     * ARGV[1]: 복원 수량
     */
    val RELEASE = """
        redis.call('INCRBY', KEYS[1], tonumber(ARGV[1]))
        return 1
    """.trimIndent()
}
