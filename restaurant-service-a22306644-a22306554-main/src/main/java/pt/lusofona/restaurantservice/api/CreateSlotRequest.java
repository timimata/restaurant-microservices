package pt.lusofona.restaurantservice.api;

import java.time.Instant;
import java.util.UUID;

public record CreateSlotRequest(UUID restaurantId, Instant slotTime, int capacity) {}
