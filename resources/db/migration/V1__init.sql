-- 更新管理员密码为 "123456" 的 BCrypt 加密版本
UPDATE admin 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyUor6/Nm'
WHERE username = 'admin'; 