package pt.lusofona.restaurantservice.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pt.lusofona.restaurantservice.data.RestaurantRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantRepository repo;

    public RestaurantController(RestaurantRepository repo) {
        this.repo = repo;
    }

    /* ========= CREATE ========= */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse create(@RequestBody CreateRestaurantRequest req) {

        if (req.name() == null || req.name().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }

        UUID id = UUID.randomUUID();

        repo.insert(
                id,
                req.name(),
                req.address(),
                req.city(),
                req.country(),
                req.phone(),
                req.email()
        );

        return new RestaurantResponse(
                id,
                req.name(),
                req.address(),
                req.city(),
                req.country(),
                req.phone(),
                req.email()
        );
    }

    /* ========= GET BY ID ========= */
    @GetMapping("/{id}")
    public RestaurantResponse get(@PathVariable UUID id) {
        var r = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        return new RestaurantResponse(
                r.id(),
                r.name(),
                r.address(),
                r.city(),
                r.country(),
                r.phone(),
                r.email()
        );
    }

    /* ========= GET ALL ========= */
    @GetMapping
    public List<RestaurantResponse> findAll() {
        return repo.findAll().stream()
                .map(r -> new RestaurantResponse(
                        r.id(),
                        r.name(),
                        r.address(),
                        r.city(),
                        r.country(),
                        r.phone(),
                        r.email()
                ))
                .toList();
    }

    // ===== DTOs =====

    public record CreateRestaurantRequest(
            String name,
            String address,
            String city,
            String country,
            String phone,
            String email
    ) {}

    public record RestaurantResponse(
            UUID id,
            String name,
            String address,
            String city,
            String country,
            String phone,
            String email
    ) {}
}
