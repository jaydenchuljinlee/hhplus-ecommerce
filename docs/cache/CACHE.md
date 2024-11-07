## 캐싱이 필요한 로직 분석

### 상품 조회 API (AOP 캐시)
- 한 번이라도 조회가 된 상품이라면, 캐시에 담아둔다.
- TTL: 1일
- 상품 조회 API가 호출되면, TTL은 초기화된다.
- 주문 시에는 재고 정합성을 맞춰주기 위해 캐싱을 날려주는 방식을 채택했다.
  - 리프레시 하지 않고 날리는 방식을 채택한 이유는 `상품 조회 > 캐시 조회 > 재설정` 과정이 주문 로직에 들어가게 되어, 주문 로직에 불필요한 로직이 많이 들어가게 된다고 생각해서 빼 주게 되었다. 

### 인기 상품 조회 API (Default Cache)
- 인기 상품의 경우 사용자의 주문 or 결제에 영향을 미치지 않는 부분이라, 커스텀 캐시를 사용하지 않았다.
- 해당 캐시 조회의 경우 TTL 만료가 되면, 다시 DB 조회를 하는 방식으로 진행했다.
- 인기 상품 조회의 경우, 주문과는 다르게 결제 시에 캐시를 리프레시 해 주는 방식을 채택했다.
  - 결제의 경우, 주문보다는 덜 복잡한 로직이 들어간다고 판단했기 때문에 리프레시 해 줬지만 <= 역시나 불필요한 로직이 추가됐다고 생각되어 리팩토링할 예정이다.  

---

## 캐싱 성능 보고서

### 성능 측정 지표
- DB 조회의 경우, 최대 속도가 500ms를 넘는지 체크
- 캐시의 경우, 최대 속도가 120ms를 넘는지 체크
- 성능 측정은 

### DB 조회
- DB 조회의 경우 디스크 IO를 유발하는 조회 방법으로, 일반적인 영구 데이터 조회 방법이다.
- 사용 API -> `GET /product/db?productId=1`
- 사용 Service 코드 -> [ProductService.getProductDB()](https://github.com/jaydenchuljinlee/hhplus-ecommerce/blob/feature/step_14/src/main/kotlin/com/hhplus/ecommerce/domain/product/ProductService.kt)
- 성능 측정
  - 측정 DB는 Docker와 MySQL을 사용했다. 
  - 평균 속도는 `11ms`, 최대 Delay 속도는 `137ms`가 나왔다. 
![DB](/docs/cache/product_db_perf.png)

### Default 캐시 조회
- Spring Cache에서 제공해주는 기본 캐싱 방식이다.
- 설정 방식 -> [CacheConfig.kt](https://github.com/jaydenchuljinlee/hhplus-ecommerce/blob/feature/step_14/src/main/kotlin/com/hhplus/ecommerce/common/config/CacheConfig.kt)
- 사용 API -> `GET /product/cache?productId=1`
- 사용 Service 코드 -> [ProductService.getProductCache()](https://github.com/jaydenchuljinlee/hhplus-ecommerce/blob/feature/step_14/src/main/kotlin/com/hhplus/ecommerce/domain/product/ProductService.kt)
  - 현재 주석으로 처리
- 성능 측정
  - Docker로 띄운 Redis 서버를 사용헀다.
  - 평균 속도는 `3.1ms`, 최대 Delay 속도는 `87ms`가 나왔다. => 일반 DB보다 확실히 빠름
- ![Default_Cache](/docs/cache/product_default_cache_perf.png)

### AOP 캐시 조회
- Spring AOP를 사용해서 TTL 값을 수정하는 커스텀 캐싱 방식이다.
- 설정 방식 -> [RedisCacheableAspect.kt](https://github.com/jaydenchuljinlee/hhplus-ecommerce/blob/feature/step_14/src/main/kotlin/com/hhplus/ecommerce/common/anotation/aspect/RedisCacheableAspect.kt)
- 사용 API -> `GET /product/cache?productId=1`
- 사용 Service 코드 -> [ProductService.getProductCache()](https://github.com/jaydenchuljinlee/hhplus-ecommerce/blob/feature/step_14/src/main/kotlin/com/hhplus/ecommerce/domain/product/ProductService.kt)
    - 현재 사용중
- 성능 측정
    - Docker로 띄운 Redis 서버를 사용헀다.
    - 평균 속도는 `4.13ms`, 최대 Delay 속도는 `90ms`가 나왔다. => 일반 DB보다 확실히 빠르지만, Default 캐싱보다는 느림.
- ![AOP_Cache](/docs/cache/product_aop_cache_perf.png)


---

## 결론
- `Default 캐싱 > AOP 캐싱 > DB 조회` 순으로 속도 차이가 확실히 났다.
- Custom으로 구현한 AOP 캐싱이 상대적으로 느린 이유는, 조회되는 순간에 캐시를 다시 한 번 덮어씌우는 작업이 있기 때문이다.
  - 여러 사용자가 지속적으로 조회하는 `상품`의 경우 지속적이 캐싱이 필요하기 때문에 TTL 만료보다 지속적으로 초기화 해 주는 작업이 더 좋다고 판단했다.
- 단순한 캐싱의 경우, 만료 후에 다시 DB 조회를 해도 무방하다고 판단되어 Spring에서 제공하는 기본 캐싱을 사용하는 판단을 했다.

