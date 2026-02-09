CREATE TABLE menu_items
(
    id            UUID PRIMARY KEY,
    restaurant_id UUID           NOT NULL,
    name          TEXT           NOT NULL,
    description   TEXT,
    price         NUMERIC(10, 2) NOT NULL,
    currency      TEXT           NOT NULL
);
