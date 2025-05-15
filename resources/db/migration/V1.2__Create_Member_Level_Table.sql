CREATE TABLE member_level (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '等级名称',
    min_points INT NOT NULL COMMENT '最小积分',
    max_points INT NOT NULL COMMENT '最大积分',
    discount DECIMAL(3,2) NOT NULL COMMENT '折扣率',
    privileges TEXT COMMENT '特权描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级表';

-- 修改初始数据，删除重复的数据
INSERT INTO member_level (name, min_points, max_points, discount, privileges) VALUES
('普通会员', 0, 999, 1.00, '基础预约服务'),
('银卡会员', 1000, 4999, 0.95, '95折优惠\n优先预约服务'),
('金卡会员', 5000, 9999, 0.90, '9折优惠\n优先预约服务\n特殊活动优先参与'),
('钻石会员', 10000, 999999, 0.85, '85折优惠\n最优先预约服务\n特殊活动优先参与\nVIP专属客服'); 
 