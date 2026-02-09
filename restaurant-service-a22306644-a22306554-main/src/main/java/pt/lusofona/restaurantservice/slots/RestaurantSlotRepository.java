package pt.lusofona.restaurantservice.slots;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantSlotRepository extends JpaRepository<pt.lusofona.restaurantservice.slots.RestaurantSlot, pt.lusofona.restaurantservice.slots.RestaurantSlot.SlotId> {

    Optional<pt.lusofona.restaurantservice.slots.RestaurantSlot> findByRestaurantIdAndSlotTime(UUID restaurantId, Instant slotTime);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from RestaurantSlot s where s.restaurantId = :restaurantId and s.slotTime = :slotTime")
    Optional<pt.lusofona.restaurantservice.slots.RestaurantSlot> findForUpdate(@Param("restaurantId") UUID restaurantId, @Param("slotTime") Instant slotTime);
}
