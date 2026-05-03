create table customer_account (
    id uuid primary key,
    full_name varchar(255) not null,
    email varchar(255) not null unique,
    credit_balance numeric(12,2) not null,
    verified_payment_card boolean not null,
    active boolean not null,
    created_at timestamptz not null
);

create table facility (
    id uuid primary key,
    code varchar(50) not null unique,
    name varchar(255) not null,
    type varchar(50) not null,
    city varchar(255) not null,
    address_line varchar(255) not null
);

create table bike (
    id uuid primary key,
    code varchar(50) not null unique,
    model_name varchar(255) not null,
    category varchar(50) not null,
    status varchar(50) not null,
    price_per_minute numeric(10,2) not null,
    facility_id uuid not null references facility(id),
    reserved_by_customer_id uuid references customer_account(id)
);

create table service_booking (
    id uuid primary key,
    booking_number varchar(100) not null unique,
    customer_name varchar(255) not null,
    customer_email varchar(255) not null,
    bike_brand varchar(255) not null,
    bike_model varchar(255) not null,
    problem_description varchar(1500) not null,
    preferred_from timestamptz not null,
    preferred_to timestamptz not null,
    scheduled_at timestamptz not null,
    created_at timestamptz not null,
    status varchar(50) not null,
    preliminary_price numeric(10,2),
    estimated_completion_at timestamptz,
    service_point_id uuid not null references facility(id),
    notes varchar(1500)
);
