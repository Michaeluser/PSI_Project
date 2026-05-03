insert into customer_account (id, full_name, email, credit_balance, verified_payment_card, active, created_at) values
('11111111-1111-1111-1111-111111111111', 'Andrej Tester', 'andrej@example.com', 120.00, true, true, now()),
('22222222-2222-2222-2222-222222222222', 'Lucia Rider', 'lucia@example.com', 18.00, true, true, now()),
('33333333-3333-3333-3333-333333333333', 'Guest Without Card', 'guest@example.com', 70.00, false, true, now());

insert into facility (id, code, name, type, city, address_line) values
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'SER-BA-01', 'BikeFlow Service Bratislava', 'SERVICE_POINT', 'Bratislava', 'Mlynská dolina 1'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'WH-BA-01', 'BikeFlow Warehouse Bratislava', 'WAREHOUSE', 'Bratislava', 'Stará Vajnorská 10'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'SHOP-TT-01', 'BikeFlow Shop Trnava', 'SHOP', 'Trnava', 'Hlavná 24');

insert into bike (id, code, model_name, category, status, price_per_minute, facility_id, reserved_by_customer_id) values
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'BK-001', 'Urban Flow 300', 'CITY', 'AVAILABLE', 0.25, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', null),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'BK-002', 'Trail Pro X', 'MOUNTAIN', 'AVAILABLE', 0.35, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', null),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3', 'BK-003', 'Volt Ride E1', 'ELECTRIC', 'AVAILABLE', 0.55, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', null);

insert into product (id, sku, name, unit) values
('cccccccc-cccc-cccc-cccc-ccccccccccc1', 'P-TIRE-29', '29 inch tire', 'pcs'),
('cccccccc-cccc-cccc-cccc-ccccccccccc2', 'P-BRAKE-SET', 'Disc brake set', 'pcs'),
('cccccccc-cccc-cccc-cccc-ccccccccccc3', 'P-CHAIN-11', '11-speed chain', 'pcs');

insert into inventory_stock (id, facility_id, product_id, quantity) values
('dddddddd-dddd-dddd-dddd-ddddddddddd1', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 30),
('dddddddd-dddd-dddd-dddd-ddddddddddd2', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'cccccccc-cccc-cccc-cccc-ccccccccccc2', 12),
('dddddddd-dddd-dddd-dddd-ddddddddddd3', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'cccccccc-cccc-cccc-cccc-ccccccccccc3', 25),
('dddddddd-dddd-dddd-dddd-ddddddddddd4', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 4),
('dddddddd-dddd-dddd-dddd-ddddddddddd5', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc2', 1),
('dddddddd-dddd-dddd-dddd-ddddddddddd6', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc3', 3);

insert into sales_history (id, facility_id, product_id, sales_date, quantity_sold) values
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', current_date - 2, 5),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee2', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', current_date - 10, 3),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee3', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc2', current_date - 5, 2),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee4', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'cccccccc-cccc-cccc-cccc-ccccccccccc3', current_date - 1, 4);
