package pt.lusofona.restaurantservice.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pt.lusofona.restaurantservice.core.SlotsService;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@RestController
@RequestMapping("/api/slots")
public class SlotsController {

    private final SlotsService service;

    public SlotsController(SlotsService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody CreateSlotRequest req) {
        service.create(req.restaurantId(), req.slotTime(), req.capacity());
    }

    @GetMapping("/{restaurantId}")
    public SlotResponse get(
            @PathVariable UUID restaurantId,
            @RequestParam String slotTime
    ) {
        Instant instant = parseSlotTime(slotTime);
        var slot = service.get(restaurantId, instant);
        return new SlotResponse(
                slot.restaurantId(),
                slot.slotTime(),
                slot.capacity(),
                slot.available()
        );
    }

    @PostMapping("/{restaurantId}/hold")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hold(
            @PathVariable UUID restaurantId,
            @RequestParam String slotTime
    ) {
        Instant instant = parseSlotTime(slotTime);
        service.hold(restaurantId, instant);
    }

    @PostMapping("/{restaurantId}/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void release(
            @PathVariable UUID restaurantId,
            @RequestParam String slotTime
    ) {
        Instant instant = parseSlotTime(slotTime);
        service.release(restaurantId, instant);
    }

    // =========================
    // Utils
    // =========================
    private Instant parseSlotTime(String raw) {
        if (raw == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "slotTime is required"
            );
        }

        try {
            return Instant.parse(raw.trim());
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid slotTime format. Use ISO-8601, e.g. 2026-01-04T21:34:35Z"
            );
        }
    }
}
