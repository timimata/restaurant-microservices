CREATE TABLE reservations (
                              id UUID PRIMARY KEY,
                              restaurant_id UUID NOT NULL,
                              status TEXT NOT NULL,
                              created_at TIMESTAMP NOT NULL
);
