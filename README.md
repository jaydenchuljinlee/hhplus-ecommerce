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

## 📅 3주차 ~ 5주차 마일스톤 계획

| **주차** | **작업 내용**                         | **일정**           | **기간 (Days)** | **상태** |
|----------|---------------------------------------|------------------|---------------|----------|
| **3주차**| **요구사항 분석**                     | 10.06 ~ 10.07    | 2             | ✅ 완료  |
|          | **시퀀스 다이어그램 작성**            | 10.08            | 1             | 🔄 진행 중     |
|          | **ERD 분석 및 작성**                  | 10.09            | 1             | 🔄 진행 중     |
|          | **Mock API 개발 및 테스트**           | 10.10 ~ 10.11 오전 | 1.5           | 🔄 진행 중     |
| **4주차**| **잔액 도메인 개발**                  |                  |               | ⏳ 예정     |
|          | - 잔액 조회 API                       | 10.13            | 0.5           | ⏳ 예정     |
|          | - 잔액 조회 API 테스트 코드 작성      | 10.13            | 0.5           | ⏳ 예정     |
|          | - 잔액 충전 API                       | 10.14            | 0.5           | ⏳ 예정     |
|          | - 잔액 충전 API 테스트 코드 작성      | 10.14            | 0.5           | ⏳ 예정     |
|          | **상품 도메인 개발**                  | 10.15            | 1             | ⏳ 예정     |
|          | - 상품 조회 API                       | 10.15            | 0.5           | ⏳ 예정     |
|          | - 상품 조회 API 테스트 코드 작성      | 10.15            | 0.5           | ⏳ 예정     |
|          | **주문 도메인 개발**                  |                  |               | ⏳ 예정     |
|          | - 주문 API                            | 10.16            | 0.5           | ⏳ 예정     |
|          | - 주문 API 테스트 코드 작성           | 10.16            | 0.5           | ⏳ 예정     |
|          | **결제 도메인 개발**                  |                  |               | ⏳ 예정     |
|          | - 결제 API                            | 10.17            | 0.5           | ⏳ 예정     |
|          | - 결제 API 테스트 코드 작성           | 10.17            | 0.5           | ⏳ 예정     |
| **5주차**| **심화 기능 개발**                    |                  |               | ⏳ 예정     |
|          | - 상위 5개 상품 정보 조회 API         | 10.20            | 1             | ⏳ 예정     |
|          | - 장바구니 추가/삭제 API              | 10.21 ~ 10.22    | 2             | ⏳ 예정     |
|          | - 장바구니 조회 API                   | 10.23            | 1             | ⏳ 예정     |
|          | **전반적인 리팩터링**                 | 10.24            | 1             | ⏳ 예정     |

## 📊 Gantt 차트 프로젝트 일정

```mermaid
gantt
    title 프로젝트 일정
    dateFormat  YYYY-MM-DD
    section 3주차
        요구사항 분석            :done,    des1, 2024-10-06, 2d
        시퀀스 다이어그램 작성   :active,  des2, 2024-10-08, 1d
        ERD 분석 및 작성        :active,  des3, 2024-10-09, 1d
        Mock API 개발 및 테스트  :         des4, 2024-10-10, 1.5d
    section 4주차
        잔액 조회 API           :         des5, 2024-10-13, 0.5d
        잔액 조회 테스트 코드 작성 :       des6, 2024-10-13, 0.5d
        잔액 충전 API           :         des7, 2024-10-14, 0.5d
        잔액 충전 테스트 코드 작성 :       des8, 2024-10-14, 0.5d
        상품 도메인 개발        :         des9, 2024-10-15, 1d
        주문 도메인 개발        :         des10, 2024-10-16, 1d
        결제 도메인 개발        :         des11, 2024-10-17, 1d
    section 5주차
        상위 5개 상품 조회 API  :         des12, 2024-10-20, 1d
        장바구니 추가/삭제 API  :         des13, 2024-10-21, 2d
        장바구니 조회 API       :         des14, 2024-10-23, 1d
        전반적인 리팩터링       :         des15, 2024-10-24, 1d

```


---

## ⏳ 시퀀스 다이어그램

- ### 잔액 조회

```mermaid
sequenceDiagram
    actor User
    participant BalanceController
    participant BalanceFacade
    participant UserService
    participant BalanceService
    participant BalanceRepository

    alt 잔액 조회 요청
        User->>BalanceController: 잔액 조회 요청
    else 파라미터 유효성
        BalanceController->>User: 유효성 Error(400)
    end
    BalanceController->>BalanceFacade: 유효한 파라미터 전달
    alt 유저 정보 존재
        BalanceFacade->>UserService: 유저 존재 여부 검사
    else 유저 정보 미존재
        UserService->>BalanceFacade: 유저 미존재 Error(500)
    end
    BalanceFacade->>BalanceService: 잔액 조회
    alt 잔액 정보 조회 존재
        BalanceService->>BalanceRepository: 잔액 정보 조회
    else 잔액 정보 미존재
        BalanceRepository->>BalanceService: 잔액 정보 미존재 Error(500)
    end
    BalanceRepository-->>BalanceService: 잔액 정보 반환
    BalanceService-->>BalanceFacade: 조회된 잔액 정보 전달
    BalanceFacade-->>BalanceController: 조회된 잔액 정보 전달
    BalanceController-->>User: 잔액 조회 결과 반환
```

---

- ### 잔액 충전
```mermaid
sequenceDiagram
    actor User as 사용자
    participant BalanceController
    participant BalanceFacade
    participant UserService
    participant BalanceService
    participant BalanceRepository

    User->>BalanceController: 잔액 충전 요청 (user ID, amount)
    opt 유효성 검사
        User->>BalanceController: 400 Error
    end
    BalanceController->>BalanceFacade: 유효한 파라미터 전달
    alt 유저 정보 존재
        BalanceFacade->>UserService: 유저 존재 여부 검사
    else 유저 정보 미존재
        UserService->>BalanceFacade: 유저 미존재 500 Error
    end
    opt 충전 로직
        BalanceFacade->>BalanceService: 충전 요청
        BalanceService->>BalanceService: 사용자 ID 기반 Lock 획득
        alt 충전 유효성 검사 실패
            BalanceService-->BalanceFacade: 충전 유효성 500 Error
        end
    end
    BalanceService->>BalanceRepository: 사용자 잔액 정보 갱신
    BalanceRepository-->>BalanceService: 갱신 결과 반환
    BalanceService-->>BalanceFacade: 충전 완료 결과 반환
    BalanceFacade-->>BalanceController: 충전 완료 결과 반환
    BalanceController-->>User: 잔액 충전 결과 반환
```
---

- ### 상품 조회

```mermaid
sequenceDiagram
    actor User as 사용자
    participant ProductController
    participant ProductFacade
    participant ProductService
    participant ProductRepository

    User->>ProductController: 상품 정보 조회 요청 (상품 ID)
    opt 유효성 검사
        ProductController->>User: 400 Error
    end
    ProductController->>ProductFacade: 유효한 파라미터 전달
    ProductFacade->>ProductRepository: 상품 정보 조회
    alt 상품 정보 조회
        ProductRepository-->>ProductService: 상품 조회 정보 반환
    else 상품 정보 미존재
        ProductRepository-->>ProductService: 상품 정보 조회 500 Error
    end
    ProductRepository-->>ProductFacade: 상품 정보 전달
    ProductFacade->>ProductFacade: 상품 정보 가공 (필요 시)
    ProductFacade-->>ProductController: 최종 상품 정보 반환
    ProductController-->>User: 상품 정보 조회 결과 반환

```
---

- ### 주문 및 결제
```mermaid
sequenceDiagram
    actor User as 사용자
    participant OrderController
    participant OrderFacade
    participant UserService
    participant ProductService
    participant ProductRepository
    participant BalanceService
    participant OrderService

    participant OrderRepository
    participant OrderSnapshotRepository
    participant OrderHistoryRepository
    participant PaymentAPI as 외부 결제 API

    User->>OrderController: 주문 결제 요청 (user ID, product ID)
    opt 유효성 검사
        OrderController->>User: 400 Error
    end
    OrderController->>OrderFacade: 유효한 파라미터 전달
    OrderFacade-->UserService: 사용자 정보 조회
    alt 사용자 정보 존재
        UserService->>OrderFacade: 사용자 정보 반환
    else 사용자 정보 없음
        UserService->>OrderFacade: 사용자 정보 조회 500 Error
    end
    OrderFacade->>ProductService: 상품 재고 정보 조회
    alt 상품 재고 정보 존재
        ProductService->>ProductService: 상품 ID 기반 Lock 획득
        ProductService->>OrderFacade: 상품 재고 정보 반환
    else 상품 재고 정보 미존재
        ProductService->>OrderFacade: 상품 재고 정보 조회 500 Error
    end
    alt 상품 재고 차감 가능
        ProductService->>ProductRepository: 재고 차감 요청
    else 상품 재고 차감 불가
        ProductService --> OrderFacade: 상품 재고 부족 500 Error
    end
    OrderFacade->>BalanceService: 잔액 정보 조회
    alt 잔액 정보 존재
        OrderFacade->>OrderFacade: 사용자 ID 기반의 잔액 정보 Lock 획득
        BalanceService->>OrderFacade: 잔액 정보 반환
    else 잔액 정보 미존재
        BalanceService->>OrderFacade: 잔액 정보 조회 500 Error
    end
    OrderService->>OrderRepository: 주문 정보 저장
    OrderService->>OrderSnapshotRepository: 주문 요청 스냅샷 저장
    OrderService->>OrderHistoryRepository: 주문 히스토리 저장

    OrderService->>PaymentAPI: 외부 결제 API 요청
    PaymentAPI-->>OrderService: 결제 승인 or 실패 응답
    alt 결제 성공
        OrderService->>OrderRepository: 결제 정보 저장 (상태값 "결제 완료")
        OrderService->>OrderRepository: 결제 이력 저장
    else 결제 실패
        OrderService->>OrderRepository: 주문 상태를 "주문 완료"
    end
    OrderService-->>User: 최종 주문 결제 결과 반환

```