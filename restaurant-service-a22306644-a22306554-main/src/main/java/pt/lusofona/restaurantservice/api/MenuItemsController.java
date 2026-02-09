package pt.lusofona.restaurantservice.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pt.lusofona.restaurantservice.core.MenuItemsService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemsController {

    private final MenuItemsService service;

    public MenuItemsController(MenuItemsService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse create(@RequestBody CreateMenuItemRequest req) {
        var created = service.create(
                req.restaurantId(),
                req.name(),
                req.description(),
                req.price(),
                req.currency()
        );

        return toResponse(created);
    }

    @GetMapping("/{id}")
    public MenuItemResponse get(@PathVariable UUID id) {
        return toResponse(service.get(id));
    }

    // GET /api/menu-items?restaurantId=...
    @GetMapping
    public List<MenuItemResponse> listByRestaurant(@RequestParam UUID restaurantId) {
        return service.listByRestaurant(restaurantId).stream()
                .map(this::toResponse)
                .toList();
    }

    // UPDATE
    @PutMapping("/{id}")
    public MenuItemResponse update(@PathVariable UUID id, @RequestBody UpdateMenuItemRequest req) {
        var updated = service.update(
                id,
                req.name(),
                req.description(),
                req.price(),
                req.currency()
        );
        return toResponse(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.remove(id);
    }

    private MenuItemResponse toResponse(pt.lusofona.restaurantservice.data.MenuItemsRepository.MenuItemRow i) {
        return new MenuItemResponse(
                i.id(),
                i.restaurantId(),
                i.name(),
                i.description(),
                i.price(),
                i.currency()
        );
    }

    public record CreateMenuItemRequest(
            UUID restaurantId,
            String name,
            String description,
            BigDecimal price,
            String currency
    ) {}

    public record UpdateMenuItemRequest(
            String name,
            String description,
            BigDecimal price,
            String currency
    ) {}

    public record MenuItemResponse(
            UUID id,
            UUID restaurantId,
            String name,
            String description,
            BigDecimal price,
            String currency
    ) {}
}
