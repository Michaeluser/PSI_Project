create table product (
    id uuid primary key,
    sku varchar(100) not null unique,
    name varchar(255) not null,
    unit varchar(50) not null
);

create table inventory_stock (
    id uuid primary key,
    facility_id uuid not null references facility(id),
    product_id uuid not null references product(id),
    quantity integer not null
);

create table sales_history (
    id uuid primary key,
    facility_id uuid not null references facility(id),
    product_id uuid not null references product(id),
    sales_date date not null,
    quantity_sold integer not null
);

create table dispatch_request (
    id uuid primary key,
    request_number varchar(100) not null unique,
    source_facility_id uuid not null references facility(id),
    target_facility_id uuid not null references facility(id),
    priority varchar(50) not null,
    status varchar(50) not null,
    created_at timestamptz not null,
    notes varchar(1500)
);

create table dispatch_request_item (
    id uuid primary key,
    dispatch_request_id uuid not null references dispatch_request(id) on delete cascade,
    product_id uuid not null references product(id),
    requested_quantity integer not null
);
