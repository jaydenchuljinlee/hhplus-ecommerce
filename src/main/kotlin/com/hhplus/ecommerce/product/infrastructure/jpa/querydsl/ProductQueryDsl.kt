package com.hhplus.ecommerce.product.infrastructure.jpa.querydsl

import com.hhplus.ecommerce.infrastructure.order.jpa.entity.QOrderEntity
import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.QPaymentEntity
import com.hhplus.ecommerce.product.infrastructure.dto.BestSellingProduct
import com.hhplus.ecommerce.infrastructure.product.jpa.entity.QProductDetailEntity
import com.hhplus.ecommerce.infrastructure.product.jpa.entity.QProductEntity
import com.hhplus.ecommerce.infrastructure.user.jpa.entity.QUserEntity
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ProductQueryDsl(
    private val queryFactory: JPAQueryFactory
): IProductQueryDsl {
    private val product: QProductEntity = QProductEntity.productEntity
    private val productDetail: QProductDetailEntity = QProductDetailEntity.productDetailEntity

    private val payment: QPaymentEntity = QPaymentEntity.paymentEntity
    private val order: QOrderEntity = QOrderEntity.orderEntity

    private val user: QUserEntity = QUserEntity.userEntity

    override fun findTop5BestSellingProductsLast3Days(): List<BestSellingProduct> {
        return queryFactory.from(product)
            .select(Projections.fields(
                BestSellingProduct::class.java,
                product.id.`as`("productId"),
                product.name.`as`("productName"),
                productDetail.quantity.max().`as`("stock"),
                order.quantity.sum().`as`("totalOrderCount"),
                payment.price.sum().`as`("totalPayPrice"),  // payment.price의 합계
                order.id.count().`as`("orderCount"),  // order의 합계
                payment.id.count().`as`("payCount")  // payment의 합계
                ))
            .innerJoin(productDetail).on(productDetail.productId.eq(product.id))
            .fetchJoin()
            .innerJoin(order).on(order.productId.eq(product.id))
            .fetchJoin()
            .innerJoin(payment).on(payment.orderId.eq(order.id))
            .fetchJoin()
            .innerJoin(user).on(user.id.eq(order.userId))
            .fetchJoin()
            .where(
                order.createdAt.after(LocalDateTime.now().minusDays(3))
            )  // 최근 3일간의 조건 추가
            .groupBy(product.id, product.name)  // product.id에 대한 그룹화
            .orderBy(order.quantity.sum().desc())  // order의 합계 기준으로 정렬
            .limit(5)  // 상위 5개
            .fetch()
    }
}