-- 사용자 데이터 삽입
INSERT INTO user_info (id, name, phone) VALUES
    (1, '이철진', '010-1234-5678'),
    (2, '이규명', '010-2345-6789'),
    (3, '윤성민', '010-3456-7890'),
    (4, '이정기', '010-4567-8901'),
    (5, '김현진', '010-5678-9012');

-- 각 사용자와 매핑된 balance 데이터 삽입
-- 예시로 각 사용자에게 10000.00의 초기 잔액을 부여
INSERT INTO balance (user_id, balance) VALUES
    (1, 10000000),
    (2, 10000000),
    (3, 10000000),
    (4, 10000000),
    (5, 10000000);


-- 1. Product 삽입 (상품 이름: 가방)
INSERT INTO product (name, price, active_yn, created_at, updated_at)
VALUES ('가방', 50000, 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO product (name, price, active_yn, created_at, updated_at)
VALUES ('신발', 50000, 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO product (name, price, active_yn, created_at, updated_at)
VALUES ('옷', 50000, 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO product (name, price, active_yn, created_at, updated_at)
VALUES ('책', 50000, 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO product (name, price, active_yn, created_at, updated_at)
VALUES ('식탁', 50000, 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO product (name, price, active_yn, created_at, updated_at)
VALUES ('TV', 50000, 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO product (name, price, active_yn, created_at, updated_at)
VALUES ('에어컨', 50000, 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO product (name, price, active_yn, created_at, updated_at)
VALUES ('장롱', 50000, 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. Product Option 삽입 (옵션: 색상 - 빨강)
INSERT INTO product_option (option_name, option_value, created_at, updated_at)
VALUES ('Color', 'Red', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. Product Detail 삽입 (Product와 Product Option의 ID를 참조)
INSERT INTO product_detail (product_id, product_option_id, quantity, created_at, updated_at)
VALUES (
           (SELECT id FROM product WHERE name = '가방'),
           (SELECT id FROM product_option WHERE option_name = 'Color' AND option_value = 'Red'),
           50,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO product_detail (product_id, product_option_id, quantity, created_at, updated_at)
VALUES (
           (SELECT id FROM product WHERE name = '신발'),
           (SELECT id FROM product_option WHERE option_name = 'Color' AND option_value = 'Red'),
           10,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO product_detail (product_id, product_option_id, quantity, created_at, updated_at)
VALUES (
           (SELECT id FROM product WHERE name = '옷'),
           (SELECT id FROM product_option WHERE option_name = 'Color' AND option_value = 'Red'),
           1,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO product_detail (product_id, product_option_id, quantity, created_at, updated_at)
VALUES (
           (SELECT id FROM product WHERE name = '책'),
           (SELECT id FROM product_option WHERE option_name = 'Color' AND option_value = 'Red'),
           1,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO product_detail (product_id, product_option_id, quantity, created_at, updated_at)
VALUES (
           (SELECT id FROM product WHERE name = '식탁'),
           (SELECT id FROM product_option WHERE option_name = 'Color' AND option_value = 'Red'),
           1,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO product_detail (product_id, product_option_id, quantity, created_at, updated_at)
VALUES (
           (SELECT id FROM product WHERE name = 'TV'),
           (SELECT id FROM product_option WHERE option_name = 'Color' AND option_value = 'Red'),
           1,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO product_detail (product_id, product_option_id, quantity, created_at, updated_at)
VALUES (
           (SELECT id FROM product WHERE name = '에어컨'),
           (SELECT id FROM product_option WHERE option_name = 'Color' AND option_value = 'Red'),
           1,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO product_detail (product_id, product_option_id, quantity, created_at, updated_at)
VALUES (
           (SELECT id FROM product WHERE name = '장롱'),
           (SELECT id FROM product_option WHERE option_name = 'Color' AND option_value = 'Red'),
           1,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

