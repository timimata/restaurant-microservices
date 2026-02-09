package pt.lusofona.restaurantservice.slots;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "restaurant_slots")
@IdClass(RestaurantSlot.SlotId.class)
public class RestaurantSlot {

    @Id
    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Id
    @Column(name = "slot_time", nullable = false)
    private Instant slotTime;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int available;

    protected RestaurantSlot() {}

    public RestaurantSlot(UUID restaurantId, Instant slotTime, int capacity) {
        this.restaurantId = restaurantId;
        this.slotTime = slotTime;
        this.capacity = capacity;
        this.available = capacity;
    }

    public UUID getRestaurantId() { return restaurantId; }
    public Instant getSlotTime() { return slotTime; }
    public int getCapacity() { return capacity; }
    public int getAvailable() { return available; }

    public void holdOne() {
        if (available <= 0) throw new IllegalStateException("No availability");
        available -= 1;
    }

    public void releaseOne() {
        if (available < capacity) available += 1;
    }

    public static class SlotId implements Serializable {
        private UUID restaurantId;
        private Instant slotTime;

        public SlotId() {}
        public SlotId(UUID restaurantId, Instant slotTime) {
            this.restaurantId = restaurantId;
            this.slotTime = slotTime;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SlotId other)) return false;
            return Objects.equals(restaurantId, other.restaurantId) && Objects.equals(slotTime, other.slotTime);
        }
        @Override public int hashCode() { return Objects.hash(restaurantId, slotTime); }
    }
}
