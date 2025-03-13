-- balance_history 테이블: (balance_id, created_at)
CREATE INDEX idx_balance_history_balance_created ON balance_history (balance_id, created_at); -- 잔액 정보와 기간조 회를 위해 필요

-- product 테이블: (active_yn, created_at)
CREATE INDEX idx_product_active_price ON product (active_yn, created_at); -- 상품 사용 여부와 생성일자 기간 조회를 위해 필요

-- product_detail 테이블: (product_id, product_option_id)
CREATE INDEX idx_product_detail_product_option ON product_detail (product_id, product_option_id); -- 상품 메인 정보와 옵션 정보 기반의 조회를 위해 필요

-- cart 테이블: (user_id, product_id)
CREATE INDEX idx_cart_user_product ON cart (user_id, product_id); -- 사용자 정보와 상품 정보 기반의 조회를 위해 필요

-- order_info 테이블: (user_id, status, created_at)
CREATE INDEX idx_order_info_user_status_created ON order_info (user_id, status, created_at); -- 사용자 기반의 주문 상태 조회를 위해 필요
CREATE INDEX idx_order_info_product_status_created ON order_info (product_id, status, created_at); -- 상품 기반의 주문 상태 조회를 위해 필요

-- payment 테이블: (order_id, status)
CREATE INDEX idx_payment_order_status ON payment (order_id, status); -- 주문 정보와 결제 상태 기반의 조회를 위해 필요

-- payment_history 테이블: (payment_id, created_at)
CREATE INDEX idx_payment_history_payment_created ON payment_history (payment_id, created_at); -- 결제 정보와 기간 조회를 위해 필요
