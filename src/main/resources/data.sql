insert into customer_account (id, full_name, email, credit_balance, verified_payment_card, active, created_at) values
('11111111-1111-1111-1111-111111111111', 'Andrej Tester',      'andrej@example.com', 120.00, true,  true,  '2026-05-01 09:00:00+02:00'),
('22222222-2222-2222-2222-222222222222', 'Lucia Rider',        'lucia@example.com',    8.00, true,  true,  '2026-05-01 09:00:00+02:00'),
('33333333-3333-3333-3333-333333333333', 'Guest Without Card', 'guest@example.com',   70.00, false, true,  '2026-05-01 09:00:00+02:00');

insert into facility (id, code, name, type, city, address_line) values
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'SER-BA-01',  'BikeFlow Servis Bratislava',  'SERVICE_POINT', 'Bratislava', 'Mlynska dolina 1'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'WH-BA-01',   'BikeFlow Sklad Bratislava',   'WAREHOUSE',     'Bratislava', 'Stara Vajnorska 10'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'SHOP-TT-01', 'BikeFlow Predajna Trnava',    'SHOP',          'Trnava',     'Hlavna 24');

insert into bike (id, code, model_name, category, status, price_per_minute, facility_id, reserved_by_customer_id) values
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'BK-001', 'Urban Flow 300', 'CITY',     'AVAILABLE', 0.25, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', null),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'BK-002', 'Trail Pro X',    'MOUNTAIN', 'AVAILABLE', 0.35, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', null),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3', 'BK-003', 'Volt Ride E1',   'ELECTRIC', 'AVAILABLE', 0.55, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', null);

insert into service_booking (id, booking_number, customer_name, customer_email, bike_brand, bike_model,
    problem_description, preferred_from, preferred_to, scheduled_at, created_at, status,
    preliminary_price, estimated_completion_at, service_point_id, notes) values

('ff000001-0000-0000-0000-000000000001',
 'SB-2026-001', 'Andrej Tester', 'andrej@example.com',
 'Trek', 'Marlin 7',
 'Predne brzdy nereaguju spravne, disk je mozno pokriveny.',
 '2026-05-05 08:00:00+02:00', '2026-05-07 18:00:00+02:00',
 '2026-05-05 09:00:00+02:00', '2026-05-03 10:00:00+02:00',
 'SCHEDULED', null, null,
 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', null),

('ff000002-0000-0000-0000-000000000002',
 'SB-2026-002', 'Lucia Rider', 'lucia@example.com',
 'Specialized', 'Rockhopper',
 'Prasknuta rafik, treba vymenit cele koleso vratane pneumatiky.',
 '2026-05-01 08:00:00+02:00', '2026-05-04 18:00:00+02:00',
 '2026-05-01 09:00:00+02:00', '2026-04-30 14:00:00+02:00',
 'IN_REPAIR', 65.00, '2026-05-04 17:00:00+02:00',
 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'Caka sa na dodanie nahradneho kolesa.'),

('ff000003-0000-0000-0000-000000000003',
 'SB-2026-003', 'Andrej Tester', 'andrej@example.com',
 'Giant', 'Escape 3',
 'Pravidelna udrzba, mazanie retaze, nastavenie radenia.',
 '2026-04-28 08:00:00+02:00', '2026-04-30 18:00:00+02:00',
 '2026-04-28 09:00:00+02:00', '2026-04-27 11:00:00+02:00',
 'DONE', 25.00, '2026-04-30 15:00:00+02:00',
 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'Servis dokonceny, bicykel pripraveny na vyzdvihnutie.');

insert into product (id, sku, name, unit) values
('cccccccc-cccc-cccc-cccc-ccccccccccc1', 'P-TIRE-29',   '29 inch tire',   'pcs'),
('cccccccc-cccc-cccc-cccc-ccccccccccc2', 'P-BRAKE-SET', 'Disc brake set', 'pcs'),
('cccccccc-cccc-cccc-cccc-ccccccccccc3', 'P-CHAIN-11',  '11-speed chain', 'pcs');

insert into inventory_stock (id, facility_id, product_id, quantity, minimum_quantity) values
('dddddddd-dddd-dddd-dddd-ddddddddddd1', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 30, 10),
('dddddddd-dddd-dddd-dddd-ddddddddddd2', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'cccccccc-cccc-cccc-cccc-ccccccccccc2', 12,  5),
('dddddddd-dddd-dddd-dddd-ddddddddddd3', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'cccccccc-cccc-cccc-cccc-ccccccccccc3', 25,  8),
('dddddddd-dddd-dddd-dddd-ddddddddddd4', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc1',  4,  8),
('dddddddd-dddd-dddd-dddd-ddddddddddd5', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc2',  1,  3),
('dddddddd-dddd-dddd-dddd-ddddddddddd6', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc3',  3,  5);

insert into sales_history (id, facility_id, product_id, sales_date, quantity_sold) values
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', '2026-05-01', 5),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee2', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', '2026-04-23', 3),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee3', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc2', '2026-04-28', 2),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee4', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc3', '2026-05-02', 4);

-- Spare parts: SP-001 and SP-002 have stock, SP-003 is out of stock (demos PartOrder creation)
insert into spare_part (id, sku, name, stock_quantity) values
('55500001-0000-0000-0000-000000000001', 'SP-BRAKE-PAD', 'Brake pad set',        8),
('55500002-0000-0000-0000-000000000002', 'SP-CHAIN-12',  '12-speed chain',        5),
('55500003-0000-0000-0000-000000000003', 'SP-WHEEL-29',  '29 inch wheel (rear)',  0);