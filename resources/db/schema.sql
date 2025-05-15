-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    real_name VARCHAR(50),
    avatar VARCHAR(200),
    gender TINYINT,
    member_level INT DEFAULT 0,
    points INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    last_login_time DATETIME,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);

-- 景点表
CREATE TABLE IF NOT EXISTS scenic_spot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    address VARCHAR(200),
    open_time VARCHAR(20),
    close_time VARCHAR(20),
    max_capacity INT,
    price DECIMAL(10,2),
    image_urls TEXT,
    status TINYINT DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);

-- 预约表
CREATE TABLE IF NOT EXISTS reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    scenic_spot_id BIGINT NOT NULL,
    reservation_date DATE NOT NULL,
    time_slot VARCHAR(20),
    visitor_count INT NOT NULL,
    total_price DECIMAL(10,2),
    actual_price DECIMAL(10,2),
    status TINYINT DEFAULT 0,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
); 