package pt.lusofona.restaurantservice.api.dto;

import java.time.Instant;
import java.util.UUID;

public record AvailabilityResponse(UUID restaurantId, Instant slotTime, int capacity, int available) {}
