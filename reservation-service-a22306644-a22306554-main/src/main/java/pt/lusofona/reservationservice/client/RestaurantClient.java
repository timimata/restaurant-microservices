package pt.lusofona.reservationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@FeignClient(name = "restaurantClient", url = "${services.restaurant.base-url}")
public interface RestaurantClient {

    @PostMapping("/api/slots/{restaurantId}/hold")
    void hold(@PathVariable UUID restaurantId, @RequestParam Instant slotTime);

    @PostMapping("/api/slots/{restaurantId}/release")
    void release(@PathVariable UUID restaurantId, @RequestParam Instant slotTime);

}
