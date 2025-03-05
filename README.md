# 🛒 [hhplus] e-commerce 서비스

---
## 💡 프로젝트 개요
- 상품 주문에 필요한 메뉴 정보들을 구성하고 조회가 가능해야 합니다.
- 사용자는 상품을 여러개 선택해 주문할 수 있고, 미리 충전한 잔액을 이용합니다.
- 상품 주문 내역을 통해 판매량이 가장 높은 상품을 추천합니다.

## 🔧 API 요구사항
1️⃣ **`주요`** **잔액 충전 / 조회 API**
- 결제에 사용될 금액을 충전하는 API 를 작성
- 사용자 식별자 및 충전할 금액을 받아 잔액을 충전
- 사용자 식별자를 통해 해당 사용자의 잔액을 조회

2️⃣ **`기본` 상품 조회 API**
- 상품 정보 ( ID, 이름, 가격, 잔여수량 ) 을 조회하는 API 를 작성
- 조회시점의 상품별 잔여수량을 체크하여 정합성을 확보할 것

3️⃣ **`주요`** **주문 / 결제 API**
- 사용자 식별자와 (상품 ID, 수량) 목록을 입력받아 주문하고 결제를 수행하는 API 구현
- 결제는 기 충전된 잔액을 기반으로 수행하며 성공할 시 잔액을 차감해야 한다.
- 데이터 분석을 위해 결제 성공 시에 실시간으로 주문 정보를 데이터 플랫폼에 전송해야 한다.
  ( 데이터 플랫폼이 어플리케이션 `외부` 라는 가정만 지켜 작업할 것 )

4️⃣ **`기본` 상위 상품 조회 API**
- 최근 3일간 가장 많이 팔린 상위 5개 상품 정보를 제공하는 API 구현
- 통계 정보를 다루기 위한 기술적 고민을 충분히 해보기

5️⃣ **`심화` 장바구니 기능**
- 사용자는 구매 이전에 관심 있는 상품들을 장바구니에 적재
- 이 기능을 제공하기 위해 `장바구니에 상품 추가/삭제` API 와 `장바구니 조회` API 가 필요
- 위 두 기능을 제공하기 위해 어떤 요구사항의 비즈니스 로직을 설계해야할 지?

---

## 주요 작업 내용

### 동시성 제어
- [관련_PR](https://github.com/jaydenchuljinlee/hhplus-ecommerce/pull/6)

### 레디스를 통한 분산락 처리
- [관련 PR](https://github.com/jaydenchuljinlee/hhplus-ecommerce/pull/9)

### 캐싱 전략
- [관련 PR](https://github.com/jaydenchuljinlee/hhplus-ecommerce/pull/11)

### DB 인덱스에 대한 성능 분석
- [관련_PR](https://github.com/jaydenchuljinlee/hhplus-ecommerce/pull/12)

### 공통 작업 분리
- [관련_PR](https://github.com/jaydenchuljinlee/hhplus-ecommerce/pull/5)

### 서비스 규모 확장에 대한 분석
- [관련_PR](https://github.com/jaydenchuljinlee/hhplus-ecommerce/pull/13)

### 카프카를 통한 아웃박스 패턴
- [관련_PR](https://github.com/jaydenchuljinlee/hhplus-ecommerce/pull/15)

### 부하 테스트
- [관련_PR](https://github.com/jaydenchuljinlee/hhplus-ecommerce/pull/23)

### 프로메테우스 & 그라파나를 통한 모니터링 및 장애 대응
- [관련_PR](https://github.com/jaydenchuljinlee/hhplus-ecommerce/pull/24)

