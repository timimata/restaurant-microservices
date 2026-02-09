package pt.lusofona.notificationservice.api;

import pt.lusofona.notificationservice.data.NotificationLogRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationLogRepository repository;

    public NotificationController(NotificationLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<NotificationLogResponse> list(@RequestParam(defaultValue = "50") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 200); // 1..200
        return repository.findLatest(safeLimit).stream()
                .map(r -> new NotificationLogResponse(r.id(), r.eventId(), r.eventType(), r.createdAt()))
                .toList();
    }
}
