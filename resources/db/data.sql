-- 先清空景点表
DELETE FROM scenic_spot;

-- 重新插入景点数据
INSERT INTO scenic_spot (
    name, description, address, open_time, close_time, 
    max_capacity, price, status, create_time, update_time
) VALUES 
('西湖', '西湖风景秀丽，是杭州市著名景点', '浙江省杭州市', '08:00', '17:00', 10000, 80.00, 1, NOW(), NOW()),
('故宫', '中国明清两代的皇家宫殿', '北京市东城区', '08:30', '17:00', 8000, 120.00, 1, NOW(), NOW()),
('黄山', '黄山风景区，山清水秀', '安徽省黄山市', '06:00', '18:00', 5000, 150.00, 1, NOW(), NOW()),
('长城', '万里长城，中华文明的象征', '北京市八达岭', '07:30', '17:30', 15000, 100.00, 1, NOW(), NOW()),
('兵马俑', '秦始皇陵兵马俑博物馆', '西安市临潼区', '08:30', '18:00', 6000, 120.00, 1, NOW(), NOW()),
('桂林山水', '漓江山水甲天下', '广西桂林市', '00:00', '24:00', 20000, 200.00, 1, NOW(), NOW());

-- 清空并重新插入测试用户，密码是 123456
DELETE FROM sys_user WHERE username = 'test';
INSERT INTO sys_user (
    username, password, email, phone, status, member_level, points, create_time, update_time
) VALUES (
    'test', 
    '$2a$10$iHz0ktgeA1utzbXs4904heve0S8mn3K5Ogpkai7VXVFaxnpSRvrD.',  -- 密码: 123456
    'test@example.com', 
    '13800138000', 
    1, 
    0, 
    0, 
    NOW(), 
    NOW()
); 