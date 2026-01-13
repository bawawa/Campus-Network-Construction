-- 快速创建测试用户（所有账户密码：123456）

-- 管理员
INSERT INTO users (name, email, password, phone, role, is_active, created_at, updated_at)
VALUES
('系统管理员', 'admin@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000001', 'ADMIN', true, NOW(), NOW());

-- 营养师
INSERT INTO users (name, email, password, phone, role, is_active, created_at, updated_at)
VALUES
('李营养师', 'nutritionist1@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000002', 'NUTRITIONIST', true, NOW(), NOW()),
('王营养师', 'nutritionist2@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000003', 'NUTRITIONIST', true, NOW(), NOW());

-- 家长
INSERT INTO users (name, email, password, phone, role, relationship_type, is_active, created_at, updated_at)
VALUES
('张三', 'parent1@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000011', 'PARENT', 'FATHER', true, NOW(), NOW()),
('李四', 'parent2@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000012', 'PARENT', 'MOTHER', true, NOW(), NOW()),
('王五', 'parent3@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000013', 'PARENT', 'FATHER', true, NOW(), NOW()),
('赵六', 'parent4@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000014', 'PARENT', 'MOTHER', true, NOW(), NOW());

SELECT '✅ 用户数据创建完成！' AS status;
SELECT '📝 所有测试账户的密码都是：123456' AS password_info;
SELECT '' AS separator;
SELECT '测试账户列表：' AS title;
SELECT '1. 管理员：admin@asd.com' AS account1;
SELECT '2. 营养师：nutritionist1@asd.com' AS account2;
SELECT '3. 营养师：nutritionist2@asd.com' AS account3;
SELECT '4. 家长：parent1@asd.com' AS account4;
SELECT '5. 家长：parent2@asd.com' AS account5;
SELECT '6. 家长：parent3@asd.com' AS account6;
SELECT '7. 家长：parent4@asd.com' AS account7;

