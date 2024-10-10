## API 명세

- 상품 조회 API
```json
# PATCH: /product

# request
{
  "productId": 0
}

# response
{
  "code": 200,
  "message": "Success",
  "data": {
    "productId": 0,
    "name": "한우 세트",
    "description": "맛있는 한우",
    "price": 150000,
    "imageUrl": "https://thumbnails",
    "quantity": 10
  }
}

```

- 상품 Top5 조회 API

```json
# GET /product/list/top_five

# response
{        
  "code": 200,
  "message": "Success",
  "data": {
    "products": [
      {
        "productId": 1,
        "name": "한우 세트",
        "description": "맛있는 한우",
        "price": 150000,
        "imageUrl": "https://thumbnails",
        "quantity": 10
      },
      ...
    ]
  }
}
```

- 잔액 조회 API

```json
# GET /balance

# request
{
  "userId": 0
}

# response
{
  "code": 200,
  "message": "Success",
  "data": {
    "userId": 0,
    "balance": 1000
  }
}

```

- 잔액 충전 API
```json
# PATCH /balance/charge

# request
{
  "userId": 0,
  "amount": 1000
}

# response
{
  "code": 200,
  "message": "Success",
  "data": {
    "userId": 0,
    "amount": 2000,
    "transactionType": "CHARGE"
  }
}

```

- 장바구니 추가 API
```json
# POST /cart

# request
{
  "userId": 0,
  "productId": 0
}

#response
{
  "code": 200,
  "message": "Success",
  "data": {
    "cartId": 0,
    "userId": 0,
    "productId": 0
  }
}
```

- 장바구니 삭제 API
```json
# DELETE /cart

# request
{
  "cartId": 0
}

# response
{
  "code": 200,
  "message": "Success",
  "data": 0
}
```
- 장바구니 조회 API
```json
# GET /cart/list

# response
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "cartId": 0,
      "userId": 0,
      "productId": 0
    }
  ]
}
```

주문 API
```json
# POST /order

# request
{
  "userId": 0,
  "productId": 0,
  "quantity": 2,
  "price": 1000
}

# response
{
  "code": 200,
  "message": "Success",
  "data": {
    "orderId": 0,
    "productId": 0,
    "price": 1000,
    "quantity": 2,
    "status": "ORDER_COMPLETED"
  }
}
```

- 결제 API
```json
# POST /payment

# request
{
  "orderId": 0
}

# response
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 0,
    "userId": 0,
    "orderId": 0,
    "productId": 0,
    "quantity": 0,
    "price": 0,
    "status": "PAYMENT_COMPLETED"
  }
}
```