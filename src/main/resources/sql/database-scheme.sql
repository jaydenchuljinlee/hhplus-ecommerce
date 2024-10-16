----------------------------------------------------------------
-- 사용자 관련 테이블
----------------------------------------------------------------

-- 사용자
CREATE TABLE user_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 기본 키
    name VARCHAR(100) NOT NULL, -- 이름
    phone VARCHAR(20) NOT NULL, -- 연락처

    -- 베이스 필드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성일
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    del_yn CHAR(1) DEFAULT 'N' NOT NULL -- 사용 여부 (Y/N)
);

-- 잔액
CREATE TABLE balance (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     user_id BIGINT NOT NULL,
     balance BIGINT DEFAULT 0 NOT NULL,

    -- 베이스 필드
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성일
     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
     del_yn CHAR(1) DEFAULT 'N' NOT NULL -- 사용 여부 (Y/N)
);

-- 잔액 변경 이력
CREATE TABLE balance_history (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     balance_id BIGINT NOT NULL,
     amount BIGINT DEFAULT 0 NOT NULL,
     transaction_type VARCHAR(255),
     balance BIGINT DEFAULT 0 NOT NULL,

    -- 베이스 필드
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성일
     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
     del_yn CHAR(1) DEFAULT 'N' NOT NULL -- 사용 여부 (Y/N)
);


----------------------------------------------------------------
-- 상품 관련 테이블
----------------------------------------------------------------

CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    price BIGINT DEFAULT 0 NOT NULL,
    active_yn CHAR(1) DEFAULT 'Y' NOT NULL,

    -- 베이스 필드
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    del_yn CHAR(1) DEFAULT 'N' NOT NULL -- 사용 여부 (Y/N)
);

CREATE TABLE product_option (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_name VARCHAR(255),
    option_value VARCHAR(255),

    -- 베이스 필드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성일
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    del_yn CHAR(1) DEFAULT 'N' NOT NULL -- 사용 여부 (Y/N)
);

CREATE TABLE product_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    product_option_id BIGINT NOT NULL,
    quantity INT DEFAULT 0 NOT NULL,

    -- 베이스 필드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성일
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    del_yn CHAR(1) DEFAULT 'N' NOT NULL -- 사용 여부 (Y/N)
);

CREATE TABLE cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT DEFAULT 0 NOT NULL,

    -- 베이스 필드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성일
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    del_yn CHAR(1) DEFAULT 'N' NOT NULL -- 사용 여부 (Y/N)
);

----------------------------------------------------------------
-- 주문 관련 테이블
----------------------------------------------------------------

CREATE TABLE order_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    quantity INT,
    price BIGINT DEFAULT 0 NOT NULL,
    total_price BIGINT DEFAULT 0 NOT NULL,
    status VARCHAR(255),

    -- 베이스 필드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성일
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    del_yn CHAR(1) DEFAULT 'N' NOT NULL -- 사용 여부 (Y/N)
);

CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    price BIGINT DEFAULT 0 NOT NULL,
    status VARCHAR(255),

    -- 베이스 필드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성일
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    del_yn CHAR(1) DEFAULT 'N' NOT NULL -- 사용 여부 (Y/N)
);

CREATE TABLE payment_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    price BIGINT DEFAULT 0 NOT NULL,
    status VARCHAR(255),

    -- 베이스 필드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성일
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    del_yn CHAR(1) DEFAULT 'N' NOT NULL -- 사용 여부 (Y/N)
);


