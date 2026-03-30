# API 명세

> 모든 API는 `/api/v1` 접두사를 사용합니다.
> 공통 응답 형식: `{ "code": 200, "message": "Success", "data": { ... } }`

---

## 상품

### 상품 조회 (캐시)
```json
GET /api/v1/product/cache?productId=1

// response
{
  "code": 200,
  "message": "Success",
  "data": {
    "productId": 1,
    "name": "한우 세트",
    "description": "맛있는 한우",
    "price": 150000,
    "imageUrl": "https://thumbnails",
    "quantity": 10
  }
}
```

### 상품 조회 (DB 직접)
```
GET /api/v1/product/db?productId=1
// response: 상품 조회 캐시와 동일
```

### 인기 상품 Top5 조회 (캐시)
```json
GET /api/v1/product/list/top_five/cache

// response
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "productId": 1,
      "name": "한우 세트",
      "description": "맛있는 한우",
      "price": 150000,
      "imageUrl": "https://thumbnails",
      "quantity": 10
    }
  ]
}
```

### 인기 상품 Top5 조회 (DB 직접)
```
GET /api/v1/product/list/top_five/db
// response: 인기 상품 Top5 캐시와 동일
```

---

## 잔액

### 잔액 조회
```json
GET /api/v1/balance?userId=1

// response
{
  "code": 200,
  "message": "Success",
  "data": {
    "userId": 1,
    "balance": 1000
  }
}
```

### 잔액 충전
```json
PATCH /api/v1/balance/charge

// request
{
  "userId": 1,
  "amount": 1000
}

// response
{
  "code": 200,
  "message": "Success",
  "data": {
    "userId": 1,
    "amount": 2000,
    "transactionType": "CHARGE"
  }
}
```

---

## 장바구니

### 장바구니 추가
```json
POST /api/v1/cart

// request
{
  "userId": 1,
  "productId": 1
}

// response
{
  "code": 200,
  "message": "Success",
  "data": {
    "cartId": 1,
    "userId": 1,
    "productId": 1
  }
}
```

### 장바구니 삭제
```json
DELETE /api/v1/cart

// request
{
  "cartId": 1
}

// response
{
  "code": 200,
  "message": "Success",
  "data": 1
}
```

### 장바구니 조회
```json
GET /api/v1/cart/list?userId=1

// response
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "cartId": 1,
      "userId": 1,
      "productId": 1
    }
  ]
}
```

---

## 주문

### 주문 생성
```json
POST /api/v1/order

// request
{
  "userId": 1,
  "orderDetails": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 150000
    }
  ]
}

// response
{
  "code": 200,
  "message": "Success",
  "data": {
    "orderId": 1,
    "userId": 1,
    "status": "REQUESTED",
    "orderDetails": [
      {
        "productId": 1,
        "quantity": 2,
        "price": 150000
      }
    ]
  }
}
```

---

## 결제

### 결제 요청
```json
POST /api/v1/payment

// request
{
  "orderId": 1,
  "userId": 1
}

// response
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "userId": 1,
    "orderId": 1,
    "status": "PAYMENT_COMPLETED"
  }
}
```

---

## 쿠폰

### 쿠폰 발급
```json
POST /api/v1/coupon/{couponPolicyId}/issue

// request
{
  "userId": 1
}

// response
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "couponPolicyId": 1,
    "status": "ISSUED",
    "issuedAt": "2026-03-31T10:00:00",
    "usedAt": null,
    "orderId": null
  }
}
```

### 내 쿠폰 목록 조회
```json
GET /api/v1/coupon/my?userId=1

// response
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "couponPolicyId": 1,
      "status": "ISSUED",
      "issuedAt": "2026-03-31T10:00:00",
      "usedAt": null,
      "orderId": null
    }
  ]
}
```

---

## 배송

### 배송 조회
```json
GET /api/v1/shipment/{orderId}

// response
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "orderId": 1,
    "carrierId": "CJ",
    "trackingNumber": "1234567890",
    "status": "IN_TRANSIT",
    "estimatedAt": "2026-04-02T18:00:00",
    "shippedAt": "2026-04-01T09:00:00",
    "deliveredAt": null
  }
}
```

---

## 사용자 주소

### 주소 추가
```json
POST /api/v1/user/address

// request
{
  "userId": 1,
  "alias": "집",
  "receiverName": "홍길동",
  "phone": "010-1234-5678",
  "address": "서울시 강남구 테헤란로 123",
  "zipCode": "06234",
  "isDefault": true
}

// response
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "alias": "집",
    "receiverName": "홍길동",
    "phone": "010-1234-5678",
    "address": "서울시 강남구 테헤란로 123",
    "zipCode": "06234",
    "isDefault": true
  }
}
```

### 주소 수정
```
PUT /api/v1/user/address/{addressId}
// request/response: 주소 추가와 동일
```

### 주소 삭제
```json
DELETE /api/v1/user/address/{addressId}

// response
{
  "code": 200,
  "message": "Success",
  "data": null
}
```

### 주소 목록 조회
```json
GET /api/v1/user/address?userId=1

// response
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "alias": "집",
      "receiverName": "홍길동",
      "phone": "010-1234-5678",
      "address": "서울시 강남구 테헤란로 123",
      "zipCode": "06234",
      "isDefault": true
    }
  ]
}
```
