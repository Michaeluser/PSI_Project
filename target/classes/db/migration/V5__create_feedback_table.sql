CREATE TABLE rental_feedback (
    id          UUID PRIMARY KEY,
    rental_id   UUID NOT NULL UNIQUE REFERENCES rental(id),
    rating      INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     VARCHAR(1000),
    submitted_at TIMESTAMPTZ NOT NULL
);
