package pt.lusofona.reservationservice.dto;

import java.time.Instant;
import java.util.UUID;

public record CreateReservationRequest(UUID restaurantId, Instant slotTime) {}
