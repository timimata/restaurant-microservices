CREATE TABLE restaurant_slots (
                                  restaurant_id UUID NOT NULL,
                                  slot_time TIMESTAMP NOT NULL,
                                  capacity INT NOT NULL,
                                  available INT NOT NULL,
                                  PRIMARY KEY (restaurant_id, slot_time)
);
