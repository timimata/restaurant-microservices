package pt.lusofona.reservationservice;

import pt.lusofona.reservationservice.events.ReservationEventProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class KafkaTestController {

    private final ReservationEventProducer producer;

    public KafkaTestController(ReservationEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/api/kafka/test-created")
    public String testCreated(@RequestParam UUID reservationId,
                              @RequestParam UUID restaurantId) {
        producer.publishCreated(reservationId, restaurantId);
        return "published";
    }
}
