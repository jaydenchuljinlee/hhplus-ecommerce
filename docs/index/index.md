## Index 성능 보고서

### 인덱스란?
> 정의 
- 데이터 베이스에서 `읽기 성능`을 높이기 위해 사용하는 기술
- 특정 컬럼에 대한 값을 정렬하고 그 값에 대한 참조 포인터를 저장하여 데이터를 빠르게 조회하는 `색인`역할

> 인덱스의 대표적인 유형
- Primary 인덱스: 테이블의 기본 키에 대한 인덱스
- Unique 인덱스: UNIQUE 제약 조건이 설정된 컬럼에 생성
- Composite 인덱스: 여러 컬럼을 조합하여 생성한 인덱스로, 다중 컬럼을 기준으로 검색 성능을 향상

> 인덱스의 단점은?
- 데이터 삽입, 수정, 삭제 시 추가적인 오버헤드가 발생할 우려가 있다.
- 인덱스가 많아질 경우
  - DB는 인덱스도 캐싱하므로, 인덱스가 많아지면 메모리를 많이 차지하게 된다.
  - 인덱스는 추가적인 공간을 차지하므로, 많아질 경우 저장 공간을 많이 차지하게 된다.
    - 이는 결국 디스크 I/O가 많이 발생한다는 의미
  - 옵티마이저가 최적의 인덱스를 설정하는 데 오랜 시간이 걸리거나 잘 못 된 인덱스가 설정될 가능성이 있다.

---

### Index 필요한 부분 분석
- 각 테이블에서 인덱스가 필요한 기준은 다음과 같이 분석했습니다.
- balance_history 테이블
  - 기간 조건으로 검색할 가능성이 많기 때문에 `balance_id, created_at` 기준으로 인덱싱
- product_detail 테이블
  - 1:1로 매칭되는 상품에 대한 `product_id, product_option_id` 기준으로 인덱싱
- cart 테이블
  - 사용자와 상품 키로 자주 조회되기 때문에 `user_id, product_id` 기준으로 인덱싱
- order_info 테이블
  - 사용자와 주문 상태, 주문 생성 일시로 자주 조회되기 때문에 `user_id, status, created_at` 기준으로 인덱싱
- payment 테이블
  - 주문 정보와 상태 기반의 조회를 위해 `order_id, status` 기준으로 인덱싱
- payment_history 테이블
  - 결제 정보와 날짜 기준으로 자주 조회되기 때문에 `payment_id, created_at` 기준으로 인덱싱
---
### Index를 자세히 살펴봐야 하는 필요한 쿼리
- Best Top 5 상품 조회의 경우 다양한 조건이 걸리는 복합 쿼리
- 쿼리 예시
  - ```kotlin
    fun findTop5BestSellingProductsLast3Days(): List<BestSellingProduct> {
        return queryFactory.from(product)
            .select(Projections.fields(BestSellingProduct::class.java,
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
- 해당 쿼리의 경우, 인덱스가 설정되어있지 않다면 상품/주문/결제 등의 테이블 데이터를 풀스캔하여 굉장히 느려질 것이다.
- 따라서, 현재 시나리오에서 인덱스가 가장 필요한 부분이라 생각되었다.
---
### 인덱스 성능 측정
- 인덱싱 전의 Query Plan
  - ![before](/docs/index/best_query_before_index.png)
  - order에서 status와 create_at에 대한 인덱싱이 안 걸려 있어, 99,781개의 데이터를 모두 확인하는 풀스캔이 발생한다.
- 인덱싱 후의 Query Plan
  - ![after](/docs/index/best_query_after_index.png)
  - order에서 status와 create_at에 대한 인덱싱이 걸려 있어, 495개의 데이터만 조회하게 된다.
- 인덱싱 전의 DB profile을 통한 성능 측정
  - ![before](/docs/index/order_profiles_before_index.png)
- 인덱싱 후의 DB profile을 통한 성능 측정
  - ![after](/docs/index/order_profiles_after_index.png)



