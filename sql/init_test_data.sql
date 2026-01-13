-- ASD 儿童饮食与营养均衡系统 - 初始化测试数据
-- 创建不同角色的测试用户

-- 注意：密码已使用 BCrypt 加密（$2a$10$...）
-- 所有测试账户的默认密码都是：123456

-- ========================================
-- 1. 管理员账户
-- ========================================
INSERT INTO users (name, email, password, phone, role, is_active, created_at, updated_at)
VALUES
('系统管理员', 'admin@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000001', 'ADMIN', true, NOW(), NOW());

-- ========================================
-- 2. 营养师账户
-- ========================================
INSERT INTO users (name, email, password, phone, role, is_active, created_at, updated_at)
VALUES
('李营养师', 'nutritionist1@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000002', 'NUTRITIONIST', true, NOW(), NOW()),
('王营养师', 'nutritionist2@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000003', 'NUTRITIONIST', true, NOW(), NOW());

-- ========================================
-- 3. 家长账户
-- ========================================
INSERT INTO users (name, email, password, phone, role, relationship_type, is_active, created_at, updated_at)
VALUES
('张三', 'parent1@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000011', 'PARENT', 'FATHER', true, NOW(), NOW()),
('李四', 'parent2@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000012', 'PARENT', 'MOTHER', true, NOW(), NOW()),
('王五', 'parent3@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000013', 'PARENT', 'FATHER', true, NOW(), NOW()),
('赵六', 'parent4@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000014', 'PARENT', 'MOTHER', true, NOW(), NOW());

-- ========================================
-- 4. 插入儿童数据（关联家长）
-- ========================================
INSERT INTO children (name, gender, birth_date, parent_id, created_at, updated_at)
VALUES
('张小明', 'MALE', '2015-03-15', (SELECT id FROM users WHERE email = 'parent1@asd.com'), NOW(), NOW()),
('李小红', 'FEMALE', '2016-07-22', (SELECT id FROM users WHERE email = 'parent1@asd.com'), NOW(), NOW()),
('王小华', 'MALE', '2014-11-08', (SELECT id FROM users WHERE email = 'parent2@asd.com'), NOW(), NOW()),
('赵小美', 'FEMALE', '2017-01-30', (SELECT id FROM users WHERE email = 'parent3@asd.com'), NOW(), NOW());

-- ========================================
-- 5. 插入 ASD 特质档案
-- ========================================
INSERT INTO asd_profiles (child_id, asd_level, diagnosis_date, diagnosis_institution, symptoms, special_needs, created_at, updated_at)
VALUES
((SELECT id FROM children WHERE name = '张小明'), 'LEVEL_2', '2020-01-15', '北京市儿童医院', '社交障碍, 语言发育迟缓', '需要更多时间适应新环境，对噪音敏感', NOW(), NOW()),
((SELECT id FROM children WHERE name = '李小红'), 'LEVEL_1', '2020-05-20', '上海市儿童医院', '轻微自闭症', '喜欢固定规律，对视觉刺激敏感', NOW(), NOW()),
((SELECT id FROM children WHERE name = '王小华'), 'LEVEL_3', '2019-08-10', '广州市儿童医院', '重度自闭症, 沟通障碍', '需要专业康复训练，对食物质地有要求', NOW(), NOW()),
((SELECT id FROM children WHERE name = '赵小美'), 'LEVEL_2', '2021-03-05', '深圳市儿童医院', '中度自闭症', '需要营养师指导饮食', NOW(), NOW());

-- ========================================
-- 6. 插入食物营养数据
-- ========================================
INSERT INTO food_items (name, chinese_name, calories_per_100g, protein_per_100g, carbs_per_100g, fat_per_100g, fiber_per_100g, calcium_per_100g, iron_per_100g, vitamin_c_per_100g, category, is_preset, created_at, updated_at)
VALUES
('Apple', '苹果', 52, 0.3, 14.0, 0.2, 2.4, 6, 0.1, 4.6, 'FRUIT', true, NOW(), NOW()),
('Chicken Breast', '鸡胸肉', 165, 31, 0, 3.6, 0, 5, 0.9, 0, 'PROTEIN', true, NOW(), NOW()),
('Broccoli', '西兰花', 34, 2.8, 7.0, 0.4, 2.6, 47, 0.7, 89, 'VEGETABLE', true, NOW(), NOW()),
('Brown Rice', '糙米', 111, 2.6, 23.0, 0.9, 1.8, 3, 0.6, 0, 'GRAIN', true, NOW(), NOW()),
('Milk', '牛奶', 64, 3.2, 4.8, 3.3, 0, 120, 0.1, 0, 'DAIRY', true, NOW(), NOW()),
('Salmon', '三文鱼', 208, 20, 0, 13, 0, 9, 0.4, 0, 'PROTEIN', true, NOW(), NOW()),
('Carrot', '胡萝卜', 41, 0.9, 10.0, 0.2, 2.8, 33, 0.3, 6, 'VEGETABLE', true, NOW(), NOW()),
('Yogurt', '酸奶', 59, 10, 3.6, 0.4, 0, 110, 0.1, 0.2, 'DAIRY', true, NOW(), NOW()),
('Beef', '牛肉', 250, 26, 0, 15, 0, 20, 2.6, 0, 'PROTEIN', true, NOW(), NOW()),
('Spinach', '菠菜', 23, 2.9, 3.6, 0.4, 2.2, 99, 2.7, 28, 'VEGETABLE', true, NOW(), NOW());

-- ========================================
-- 7. 插入食谱数据
-- ========================================
INSERT INTO recipes (name, chinese_name, description, suitable_meal_type, difficulty_level, cooking_time, age_group, ingredients, cooking_steps, texture_notes, allergen_warnings, feeding_tips, is_preset, created_at, updated_at)
VALUES
('Apple Oatmeal', '苹果燕麦粥', '营养丰富的早餐，适合ASD儿童，口感温和', 'BREAKFAST', 1, 15, '2-6', '[{"name":"燕麦","amount":"50g"},{"name":"苹果","amount":"50g"},{"name":"牛奶","amount":"200ml"}]', '1.将燕麦和牛奶煮成粥\n2.苹果切小块加入\n3.小火煮5分钟', '软糯易吞咽', '无', '可以先让儿童闻一闻味道，再慢慢喂食', true, NOW(), NOW()),
('Chicken Breast Steam', '清蒸鸡胸肉', '高蛋白低脂肪，适合肌肉发育', 'LUNCH', 2, 30, '3-12', '[{"name":"鸡胸肉","amount":"150g"},{"name":"胡萝卜","amount":"50g"},{"name":"西兰花","amount":"50g"}]', '1.鸡胸肉切块腌制\n2.胡萝卜西兰花焯水\n3.上锅蒸15分钟', '软嫩，无筋', '鸡肉、胡萝卜', '可以切成小方块方便儿童抓取', true, NOW(), NOW()),
('Fish Cake', '鱼肉糕', '富含Omega-3，有益大脑发育', 'DINNER', 3, 40, '2-8', '[{"name":"三文鱼","amount":"200g"},{"name":"土豆","amount":"100g"},{"name":"鸡蛋","amount":"1个"}]', '1.鱼肉蒸熟压碎\n2.土豆蒸熟压泥\n3.混合成型蒸制', '细腻无骨刺', '鱼类、鸡蛋', '适合刚开始吃肉类的儿童', true, NOW(), NOW()),
('Fruit Salad', '水果沙拉', '丰富的维生素，适合加餐', 'SNACK', 1, 10, '2-12', '[{"name":"苹果","amount":"1/2个"},{"name":"香蕉","amount":"1根"},{"name":"酸奶","amount":"100ml"}]', '1.水果切小块\n2.拌入酸奶', '新鲜爽口', '无', '使用当季新鲜水果', true, NOW(), NOW());

-- ========================================
-- 8. 插入饮食记录
-- ========================================
INSERT INTO dietary_records (child_id, meal_type, record_date, food_items, notes, created_at, updated_at)
VALUES
((SELECT id FROM children WHERE name = '张小明'), 'BREAKFAST', '2026-01-10', '[{"foodName":"苹果燕麦粥","amount":"200g"}]', '早餐吃得很好', NOW(), NOW()),
((SELECT id FROM children WHERE name = '张小明'), 'LUNCH', '2026-01-10', '[{"foodName":"清蒸鸡胸肉","amount":"150g"}]', '喜欢吃肉', NOW(), NOW()),
((SELECT id FROM children WHERE name = '李小红'), 'BREAKFAST', '2026-01-10', '[{"foodName":"牛奶","amount":"200ml"}]', '喝了全部牛奶', NOW(), NOW()),
((SELECT id FROM children WHERE name = '王小华'), 'DINNER', '2026-01-10', '[{"foodName":"鱼肉糕","amount":"100g"}]', '需要喂食', NOW(), NOW());

-- ========================================
-- 9. 插入营养师留言
-- ========================================
INSERT INTO nutritionist_notes (child_id, nutritionist_id, note_type, title, content, priority, created_at, updated_at)
VALUES
((SELECT id FROM children WHERE name = '张小明'), (SELECT id FROM users WHERE email = 'nutritionist1@asd.com'), 'nutrition_advice', '早餐营养建议', '建议增加早餐蛋白质摄入，可以尝试鸡蛋羹', 4, NOW(), NOW()),
((SELECT id FROM children WHERE name = '李小红'), (SELECT id FROM users WHERE email = 'nutritionist1@asd.com'), 'diet_warning', '避免过敏原', '注意避免食用鸡蛋和花生制品', 5, NOW(), NOW()),
((SELECT id FROM children WHERE name = '王小华'), (SELECT id FROM users WHERE email = 'nutritionist2@asd.com'), 'progress_feedback', '进步明显', '最近体重增长良好，继续保持', 4, NOW(), NOW()),
((SELECT id FROM children WHERE name = '赵小美'), (SELECT id FROM users WHERE email = 'nutritionist2@asd.com'), 'health_reminder', '定期检查', '建议每月进行一次营养状况检查', 3, NOW(), NOW());

-- ========================================
-- 10. 插入饮食限制
-- ========================================
INSERT INTO dietary_restrictions (child_id, restriction_type, description, severity, created_at, updated_at)
VALUES
((SELECT id FROM children WHERE name = '张小明'), 'TEXTURE_SENSITIVITY', '对粗糙食物敏感，需要软烂食物', 'MODERATE', NOW(), NOW()),
((SELECT id FROM children WHERE name = '李小红'), 'ALLERGY', '对鸡蛋和花生过敏', 'SEVERE', NOW(), NOW()),
((SELECT id FROM children WHERE name = '王小华'), 'SENSORY_ISSUE', '对强光和噪音敏感', 'MILD', NOW(), NOW()),
((SELECT id FROM children WHERE name = '赵小美'), 'TEXTURE_SENSITIVITY', '只吃糊状食物', 'MODERATE', NOW(), NOW());

-- ========================================
-- 11. 插入权限和角色权限关联
-- ========================================
-- 插入权限定义
INSERT INTO permissions (code, name, display_name, description, created_at, updated_at)
VALUES
('VIEW_CHILDREN', '查看儿童信息', '可以查看儿童的基本信息和档案', NOW(), NOW()),
('ADD_DIETARY_RECORDS', '添加饮食记录', '可以添加和修改饮食记录', NOW(), NOW()),
('VIEW_REPORTS', '查看营养报告', '可以查看营养分析报告', NOW(), NOW()),
('VIEW_RECIPES', '查看食谱推荐', '可以查看食谱和推荐', NOW(), NOW()),
('CREATE_ADVICE', '创建营养建议', '营养师可以创建营养建议', NOW(), NOW()),
('VIEW_NUTRITIONIST_PANEL', '查看营养师面板', '营养师可以访问营养师面板', NOW(), NOW()),
('MANAGE_USERS', '用户管理', '管理员可以管理所有用户', NOW(), NOW()),
('MANAGE_ROLES', '角色管理', '管理员可以管理角色和权限', NOW(), NOW()),
('SYSTEM_SETTINGS', '系统设置', '管理员可以修改系统设置', NOW(), NOW());

-- 插入角色定义
INSERT INTO roles (name, display_name, description, is_active, created_at, updated_at)
VALUES
('PARENT', '家长', '管理自己孩子的饮食和营养信息', true, NOW(), NOW()),
('NUTRITIONIST', '营养师', '为多个孩子提供营养建议和分析', true, NOW(), NOW()),
('ADMIN', '管理员', '系统管理员，拥有所有权限', true, NOW(), NOW());

-- 关联角色和权限
INSERT INTO role_permissions (role_id, permission_id, created_at, updated_at)
SELECT
    r.id as role_id,
    p.id as permission_id,
    NOW() as created_at,
    NOW() as updated_at
FROM roles r
CROSS JOIN permissions p
WHERE
    (r.name = 'PARENT' AND p.code IN ('VIEW_CHILDREN', 'ADD_DIETARY_RECORDS', 'VIEW_REPORTS', 'VIEW_RECIPES')) OR
    (r.name = 'NUTRITIONIST' AND p.code IN ('VIEW_CHILDREN', 'VIEW_REPORTS', 'VIEW_RECIPES', 'CREATE_ADVICE', 'VIEW_NUTRITIONIST_PANEL')) OR
    (r.name = 'ADMIN' AND p.code IN ('VIEW_CHILDREN', 'ADD_DIETARY_RECORDS', 'VIEW_REPORTS', 'VIEW_RECIPES', 'CREATE_ADVICE', 'VIEW_NUTRITIONIST_PANEL', 'MANAGE_USERS', 'MANAGE_ROLES', 'SYSTEM_SETTINGS'));

-- ========================================
-- 完成
-- ========================================
SELECT '测试数据插入完成！' AS status;
SELECT '所有测试账户的密码都是：123456' AS password_info;

