package pt.lusofona.restaurantservice.api;

import java.time.Instant;
import java.util.UUID;

public record SlotResponse(UUID restaurantId, Instant slotTime, int capacity, int available) {}
