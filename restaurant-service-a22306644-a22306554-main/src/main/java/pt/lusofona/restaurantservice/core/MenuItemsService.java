package pt.lusofona.restaurantservice.core;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pt.lusofona.restaurantservice.data.MenuItemsRepository;
import pt.lusofona.restaurantservice.data.RestaurantRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class MenuItemsService {

    private final MenuItemsRepository menuRepo;
    private final RestaurantRepository restaurantRepo;

    public MenuItemsService(MenuItemsRepository menuRepo, RestaurantRepository restaurantRepo) {
        this.menuRepo = menuRepo;
        this.restaurantRepo = restaurantRepo;
    }

    /* ========= CREATE ========= */
    public MenuItemsRepository.MenuItemRow create(
            UUID restaurantId,
            String name,
            String description,
            BigDecimal price,
            String currency
    ) {
        if (restaurantId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "restaurantId é obrigatório");
        }
        if (!restaurantRepo.exists(restaurantId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não existe");
        }
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name é obrigatório");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price tem de ser > 0");
        }
        if (currency == null || currency.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "currency é obrigatório");
        }

        UUID id = UUID.randomUUID();

        menuRepo.insert(
                id,
                restaurantId,
                name.trim(),
                (description == null || description.isBlank()) ? null : description.trim(),
                price,
                currency.trim()
        );

        return menuRepo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Falha ao criar menu item"
                        )
                );
    }

    /* ========= GET ========= */
    public MenuItemsRepository.MenuItemRow get(UUID id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id é obrigatório");
        }

        return menuRepo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Menu item não existe"
                        )
                );
    }

    /* ========= LIST BY RESTAURANT ========= */
    public List<MenuItemsRepository.MenuItemRow> listByRestaurant(UUID restaurantId) {
        if (restaurantId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "restaurantId é obrigatório");
        }
        if (!restaurantRepo.exists(restaurantId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não existe");
        }

        return menuRepo.findByRestaurantId(restaurantId);
    }

    /* ========= UPDATE ========= */
    public MenuItemsRepository.MenuItemRow update(
            UUID id,
            String name,
            String description,
            BigDecimal price,
            String currency
    ) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id é obrigatório");
        }
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name é obrigatório");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price tem de ser > 0");
        }
        if (currency == null || currency.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "currency é obrigatório");
        }

        // garante que existe
        menuRepo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Menu item não existe"
                        )
                );

        int updated = menuRepo.update(
                id,
                name.trim(),
                (description == null || description.isBlank()) ? null : description.trim(),
                price,
                currency.trim()
        );

        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item não existe");
        }

        return menuRepo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Falha ao atualizar menu item"
                        )
                );
    }

    /* ========= DELETE ========= */
    public void remove(UUID id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id é obrigatório");
        }

        // 404 se não existir
        menuRepo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Menu item não existe"
                        )
                );

        int deleted = menuRepo.deleteById(id);
        if (deleted == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item não existe");
        }
    }
}
