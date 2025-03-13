DELIMITER $$

CREATE PROCEDURE InsertOrderAndPaymentData(
    IN start_id INT,         -- 시작 ID
    IN end_id INT            -- 종료 ID
)
BEGIN
    DECLARE id_value INT;
    DECLARE new_order_id BIGINT;

    WHILE start_id <= end_id DO
        SET id_value = FLOOR(1 + RAND() * 100); -- product_id와 user_id에 동일하게 적용할 값

        -- order_info 테이블에 새로운 주문 데이터 삽입
INSERT INTO order_info (product_id, user_id, quantity, price, total_price, status, created_at, updated_at, del_yn)
VALUES (
           id_value,                             -- product_id (1 ~ 100 사이의 랜덤 값)
           id_value,                             -- user_id (product_id와 동일한 값)
           FLOOR(1 + RAND() * 10),               -- quantity (1 ~ 10 사이 랜덤)
           FLOOR(100 + RAND() * 1000),           -- price (100 ~ 1100 사이 랜덤)
           FLOOR(100 + RAND() * 10000),          -- total_price (100 ~ 10100 사이 랜덤)
           CASE
               WHEN RAND() < 0.5 THEN 'ORDER_REQUEST'
               ELSE 'COMPLETED'
               END,                                  -- status ('Order Request' 또는 'Completed')
           NOW() - INTERVAL FLOOR(RAND() * 30) DAY, -- created_at (현재 날짜로부터 30일 이내 랜덤)
           NOW() - INTERVAL FLOOR(RAND() * 30) DAY, -- updated_at (현재 날짜로부터 30일 이내 랜덤)
           CASE
               WHEN RAND() < 0.9 THEN 'N'
               ELSE 'Y'
               END                                   -- del_yn ('N' 또는 'Y', 'N' 확률 90%)
       );

-- 새로 삽입된 order_info의 ID 가져오기
SET new_order_id = LAST_INSERT_ID();

        -- payment 테이블에 해당 주문의 결제 데이터 삽입
INSERT INTO payment (order_id, price, status, created_at, updated_at, del_yn)
VALUES (
           new_order_id,                         -- order_info 테이블의 ID를 order_id로 사용
           FLOOR(100 + RAND() * 10000),          -- 결제 금액 (100 ~ 10100 사이 랜덤)
           CASE
               WHEN (SELECT status FROM order_info WHERE id = new_order_id) = 'COMPLETED' THEN 'PAID'
               ELSE 'PENDING'
               END,                                  -- status: 주문 상태에 따라 'PAID' 또는 'PENDING'으로 설정
           NOW() - INTERVAL FLOOR(RAND() * 30) DAY, -- created_at (현재 날짜로부터 30일 이내 랜덤)
           NOW() - INTERVAL FLOOR(RAND() * 30) DAY, -- updated_at (현재 날짜로부터 30일 이내 랜덤)
           'N'                                   -- del_yn: 기본값으로 'N' 설정
       );

SET start_id = start_id + 1;
END WHILE;
END $$

DELIMITER ;

-- 프로시저 실행 예제
CALL InsertOrderAndPaymentData(200001, 500000);
