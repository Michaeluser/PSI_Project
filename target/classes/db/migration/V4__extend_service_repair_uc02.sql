alter table service_booking add column received_at timestamptz;
alter table service_booking add column technical_state varchar(1500);
alter table service_booking add column additional_findings varchar(1500);
alter table service_booking add column client_approved_at timestamptz;
alter table service_booking add column completed_at timestamptz;
alter table service_booking add column client_notified_at timestamptz;
alter table service_booking add column loyalty_discount_percent integer not null default 0;
alter table service_booking add column loyalty_discount_amount numeric(10,2);
alter table service_booking add column parts_order_summary varchar(1500);

create table service_work_item (
    id uuid primary key,
    service_booking_id uuid not null references service_booking(id) on delete cascade,
    description varchar(1000) not null,
    labor_price numeric(10,2) not null
);

create table service_required_part (
    id uuid primary key,
    service_booking_id uuid not null references service_booking(id) on delete cascade,
    product_id uuid not null references product(id),
    requested_quantity integer not null,
    available_quantity integer not null,
    unit_price numeric(10,2) not null,
    availability_status varchar(50) not null
);
