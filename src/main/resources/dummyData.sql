-- ======================================================================
-- USERS (p_users)
-- ======================================================================
INSERT INTO p_users
(user_id, username, email, password, nickname, role, business_number, phone_number,
 created_at, modified_at, deleted_at, created_id, modified_id, deleted_id)
VALUES
    -- customer1
    (DEFAULT, 'guildong', 'hong@test.com',
     '$2a$12$P.t3xvuLK3XPQclGZaH.c.fBylKCFVB/x4Auqapn/Dpg2fcQmqDrG',
     'gdong', 'CUSTOMER', NULL, '01012345678',
     NOW(), NOW(), NULL, 0, NULL, NULL),
    -- customer2
    (DEFAULT, 'davidoh',  'oh@test.com',
     '$2a$12$P.t3xvuLK3XPQclGZaH.c.fBylKCFVB/x4Auqapn/Dpg2fcQmqDrG',
     'doh', 'CUSTOMER', NULL, '01056781234',
     NOW(), NOW(), NULL, 0, NULL, NULL),
    -- owner1
    (DEFAULT, 'mongryong','lee@test.com',
     '$2a$12$P.t3xvuLK3XPQclGZaH.c.fBylKCFVB/x4Auqapn/Dpg2fcQmqDrG',
     'mryong', 'OWNER', '1234567890', '01067892345',
     NOW(), NOW(), NULL, 0, NULL, NULL),
    -- owner2
    (DEFAULT, 'lucaskim', 'lucas@test.com',
     '$2a$12$P.t3xvuLK3XPQclGZaH.c.fBylKCFVB/x4Auqapn/Dpg2fcQmqDrG',
     'lucask', 'OWNER', '3456789012', '01086429743',
     NOW(), NOW(), NULL, 0, NULL, NULL),
    -- master1
    (DEFAULT, 'managerkim','mk@test.com',
     '$2a$12$P.t3xvuLK3XPQclGZaH.c.fBylKCFVB/x4Auqapn/Dpg2fcQmqDrG',
     'mkim', 'MASTER', NULL, '01098765432',
     NOW(), NOW(), NULL, 0, NULL, NULL);

-- ======================================================================
-- STORES (p_stores)
-- ======================================================================
INSERT INTO p_stores
(store_id, user_id, store_name, store_address, store_number, store_description, status, reject_reason,
 average_rating, review_count,
 created_at, modified_at, deleted_at, created_id, modified_id, deleted_id)
VALUES
    -- store1 (owner1)
    ('506772f3-6271-418e-a2c7-4fad65e22938',
     (SELECT user_id FROM p_users WHERE email = 'lee@test.com'   LIMIT 1),
    '몽룡이네 한식당', '전라북도 남원시 광한루로 456', '0631234567', '남원에 위치한 백반집', 'APPROVED', NULL,
    0.0, 0,
    NOW(), NOW(), NULL, 0, NULL, NULL),
  -- store2 (owner2)
  ('2ce4bac5-858a-4335-a319-2ceed1f2d154',
   (SELECT user_id FROM p_users WHERE email = 'lucas@test.com' LIMIT 1),
   '루카스네 덮밥', '충청북도 충주시 충원대로 238', '2345670819', '충주에 위치한 덮밥집', 'APPROVED', NULL,
   0.0, 0,
   NOW(), NOW(), NULL, 0, NULL, NULL);

-- ======================================================================
-- PRODUCTS (p_products)
-- ======================================================================
INSERT INTO p_products
(product_id, product_name, price, product_description, is_hidden, store_id,
 created_at, modified_at, deleted_at, created_id, modified_id, deleted_id)
VALUES
    -- store1
    ('3ac3e6a5-5d20-42cb-b698-ae932c195e23', '김치찌개', 8500, '전통 방식으로 끓인 얼큰한 김치찌개', TRUE,
     '506772f3-6271-418e-a2c7-4fad65e22938',
     NOW(), NOW(), NULL, 0, NULL, NULL),
    ('d429674f-6b54-4b4f-a08d-eb90078835b0', '불고기', 12000, '단맛을 더한 불고기', TRUE,
     '506772f3-6271-418e-a2c7-4fad65e22938',
     NOW(), NOW(), NULL, 0, NULL, NULL),
    -- store2
    ('957997e6-0f3b-4876-9a3b-3739b9edd6ea', '제육덮밥', 9000, '불맛을 더한 제육덮밥', TRUE,
     '2ce4bac5-858a-4335-a319-2ceed1f2d154',
     NOW(), NOW(), NULL, 0, NULL, NULL);

-- ======================================================================
-- ORDERS (p_orders)
--  - store_id(UUID)는 FK 아님(간접참조)
-- ======================================================================
INSERT INTO p_orders
(order_id, user_id, store_id, total_price, product_count, address, status, memo,
 created_at, modified_at, deleted_at, created_id, modified_id, deleted_id)
VALUES
    -- store1 - order1 (customer1, 김치찌개 1개)
    ('49832632-2afa-4a65-8e1e-0a19e2175d1b',
     (SELECT user_id FROM p_users WHERE email = 'hong@test.com' LIMIT 1),
    '506772f3-6271-418e-a2c7-4fad65e22938',
    8500, 1, 'string123', 'PAYMENT_PENDING', 'string',
    NOW(), NOW(), NULL, 0, NULL, NULL),

  -- store1 - order2 (customer1, 불고기 1개)
  ('a933a277-1179-457e-a0f3-3bce152edbbe',
   (SELECT user_id FROM p_users WHERE email = 'hong@test.com' LIMIT 1),
   '506772f3-6271-418e-a2c7-4fad65e22938',
   12000, 1, 'string123', 'PAYMENT_PENDING', 'string',
   NOW(), NOW(), NULL, 0, NULL, NULL),

  -- store2 - order1 (customer2, 제육덮밥 1개, 결제됨)
  ('94a1916f-3dbc-4475-b286-224146e49a77',
   (SELECT user_id FROM p_users WHERE email = 'oh@test.com' LIMIT 1),
   '2ce4bac5-858a-4335-a319-2ceed1f2d154',
   9000, 1, 'string123', 'PAID', 'string',
   NOW(), NOW(), NULL, 0, NULL, NULL),

  -- store1 - orderX (customer2, 불고기 1개, 결제됨)
  ('c0ac2c6e-e674-49e2-8a92-22f9e9518f4c',
   (SELECT user_id FROM p_users WHERE email = 'oh@test.com' LIMIT 1),
   '506772f3-6271-418e-a2c7-4fad65e22938',
   12000, 1, 'string123', 'PAID', 'string',
   NOW(), NOW(), NULL, 0, NULL, NULL);

-- ======================================================================
-- PAYMENTS (p_payments)
-- ======================================================================
INSERT INTO p_payments
(payment_id, order_id, payment_amount, status, pg_payment_key, pg_order_id,
 created_at, modified_at, deleted_at, created_id, modified_id, deleted_id)
VALUES
    (gen_random_uuid(), '94a1916f-3dbc-4475-b286-224146e49a77', 9000,  'CAPTURED', 'pay_key_94a1', 'ORDER-94A1',
     NOW(), NOW(), NULL, 0, NULL, NULL),
    (gen_random_uuid(), 'c0ac2c6e-e674-49e2-8a92-22f9e9518f4c', 12000, 'CAPTURED', 'pay_key_c0ac', 'ORDER-C0AC',
     NOW(), NOW(), NULL, 0, NULL, NULL);

-- ======================================================================
-- ADDRESS (p_address)
-- ======================================================================
INSERT INTO p_address
(address_id,address_name, name, phone_number, post_code, road_name_address, detailed_address, default_address, user_id,
 created_at, modified_at, created_id)
VALUES
    -- customer1 (홍길동) 주소
    (gen_random_uuid(),'집', '홍길동', '01012345678', '12345', '서울시 강남구 테헤란로 123', '101호', true,
     (SELECT user_id FROM p_users WHERE email = 'hong@test.com' LIMIT 1), NOW(), NOW(), 0),
  -- customer2 (오다비드) 주소
  (gen_random_uuid(),'회사', '오다비드', '01056781234', '54321', '부산시 해운대구 센텀로 456', '202호', true,
   (SELECT user_id FROM p_users WHERE email = 'oh@test.com' LIMIT 1), NOW(), NOW(), 0);
