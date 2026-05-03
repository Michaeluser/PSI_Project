create table rental (
    id uuid primary key,
    rental_number varchar(100) not null unique,
    customer_id uuid not null references customer_account(id),
    bike_id uuid not null references bike(id),
    status varchar(50) not null,
    planned_minutes integer not null,
    estimated_price numeric(10,2) not null,
    final_price numeric(10,2),
    created_at timestamptz not null,
    started_at timestamptz,
    ended_at timestamptz
);

create table rental_issue_report (
    id uuid primary key,
    rental_id uuid not null references rental(id),
    bike_id uuid not null references bike(id),
    issue_type varchar(50) not null,
    description varchar(1500) not null,
    created_at timestamptz not null
);
