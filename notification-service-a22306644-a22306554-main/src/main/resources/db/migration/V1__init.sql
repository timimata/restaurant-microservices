CREATE TABLE notification_log (
                                  id UUID PRIMARY KEY,
                                  event_id TEXT NOT NULL UNIQUE,
                                  event_type TEXT NOT NULL,
                                  created_at TIMESTAMP NOT NULL
);
