## Index 성능 보고서

## 인덱스란?
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

> 인덱스와 락의 관계
- 만약, 인덱스가 없다면 동시성 환경에서 락의 범위가 페이지 혹은 테이블 단위로 넓어지게 된다 => `페이지락`, `테이블락`
  - 그 이유는 user_id = 1234일 때, 해당 데이터를 찾기위해 순차적으로 찾아야하기 때문이다.
  - 테이블 스캔을 하게 될 경우, 검색 과정의 변경 방지를 위해 넓은 범위에 락을 걸게 된다.
- 인덱스가 있는 경우에는, 인덱스를 통해 특정 값의 위치를 바로 찾을 수 있기 때문에 `행 락` 또는 `레코드 락`을 걸기 편하다.
  - 이렇게 바로 찾을 수 있는 이유는, 대부분의 RDB의 경우 `B트리` 구조를 사용하는데, 이는 데이터를 순차적으로 조회하는 것이 아닌 `이진 탐색`을 통해
  조건에 맞는 자식 노드를 찾아가는 과정이라 데이터가 많아도 특정 값에 빠르게 접근할 수 있다. 
  - 또한, 인덱스는 테이블에서 특정 값이 위치한 주소 or 포인터를 사용하는 `위치 참조` 방식을 사용하기 때문에 찾고자 하는 데이터를 빠르게 찾을 수 있다.

---

## Index 필요한 부분 분석
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
## Index를 자세히 살펴봐야 하는 필요한 쿼리
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
## 인덱스 성능 측정 (위 best top 5 복합 쿼리 기반)
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

---

## 대용량 데이터에서 성능 측정
- 대략 500만 개 정도의 데이터를 가지고 측정 진행
  - 현업에서는 이보다 더 많은 데이터를 가지고 있어서 현재 측정한 시간보다 더 측정시간이 차이가 있겠지만,
  - 스스로 유의미한 성능의 차이가 보이는 구간이라고 생각하여 이를 기준으로 측정 진행
- 기준 테이블은 `order_info` 테이블
  - 인덱싱 컬럼은 기존 복합 인덱스로 사용한 `user_id`
  - 인덱싱이 걸리지 않은 컬럼은 `product_id` => 물론 이 컬럼도 나중에 주문에 대한 상품 집계를 위해서 사용할 수도 있지만, 현재는 인덱싱 성능 측정 기준으로 잡기위해 사용
- 데이터 생성 방법
  - 프로시저를 통해 500만 개의 랜덤한 값을 넣어주도록 설정
  - `user_id`와 `product_id`는 성능 측정을 위해 1~100 사이의 랜덤한 같은 값을 매 분기마다 넣어줌
- 데이터 예시
  - ![large_data](/docs/index/large_data_sample_file.png)
- 성능 측정 결과
  - 평균적으로 인덱스가 걸린 컬럼의 경우 빠른 조회 성능이 도출됐다.
    - 조회되는 데이터가 몇 만 건일 경우 => 0.5초 이내
    - ![large_data](/docs/index/order_query_after_index.png)
    - 단 건일 경우 => 대략 0.004 초 이내
    - ![large_data](/docs/index/order_query_after_index_one.png)
  - 반대로, 인덱스가 걸리지 않은 product_id의 경우 풀스캔이 발생하여 매 번 2초 정도의 조회 시간이 발생
    - 조회되는 데이터가 몇 만 건일 경우 => 풀스캔으로 인한 2초
      - ![large_data](/docs/index/order_query_before_index.png)
    - 단 건일 경우 => 1~2초. 처음 500만 건의 데이터가 쌓였을 당시에는 2초가 걸렸지만... 지속적으로 테스트를 진행하니 대략 1초 남짓 걸리게 되었네요..
      - ![large_data](/docs/index/order_query_before_index_one.png)
  

