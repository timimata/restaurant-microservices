package pt.lusofona.restaurantservice.api.dto;

import java.time.Instant;
import java.util.UUID;

public record CreateSlotRequest(UUID restaurantId, Instant slotTime, int capacity) {}
