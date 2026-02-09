package pt.lusofona.restaurantservice.slots;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RestaurantSlotService {

    private final RestaurantSlotRepository repository;

    public RestaurantSlotService(RestaurantSlotRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RestaurantSlot createSlot(UUID restaurantId, Instant slotTime, int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");

        var existing = repository.findByRestaurantIdAndSlotTime(restaurantId, slotTime);
        if (existing.isPresent()) return existing.get(); // simples (ou podes lançar 409)

        return repository.save(new RestaurantSlot(restaurantId, slotTime, capacity));
    }

    @Transactional(readOnly = true)
    public RestaurantSlot getAvailability(UUID restaurantId, Instant slotTime) {
        return repository.findByRestaurantIdAndSlotTime(restaurantId, slotTime)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));
    }

    @Transactional
    public void hold(UUID restaurantId, Instant slotTime) {
        var slot = repository.findForUpdate(restaurantId, slotTime)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        slot.holdOne();
        repository.save(slot);
    }

    @Transactional
    public void release(UUID restaurantId, Instant slotTime) {
        var slot = repository.findForUpdate(restaurantId, slotTime)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        slot.releaseOne();
        repository.save(slot);
    }
}
