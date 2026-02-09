package pt.lusofona.restaurantservice.core;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pt.lusofona.restaurantservice.data.SlotsRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class SlotsService {

    private final SlotsRepository repo;

    public SlotsService(SlotsRepository repo) {
        this.repo = repo;
    }

    public void create(UUID restaurantId, Instant slotTime, int capacity) {
        if (restaurantId == null || slotTime == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "restaurantId e slotTime são obrigatórios");
        }
        if (capacity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "capacity tem de ser > 0");
        }
        repo.insert(restaurantId, slotTime, capacity);
    }

    public SlotsRepository.SlotRow get(UUID restaurantId, Instant slotTime) {
        return repo.find(restaurantId, slotTime)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot não existe"));
    }

    public void hold(UUID restaurantId, Instant slotTime) {
        // se não existe -> 404
        var slot = repo.find(restaurantId, slotTime)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot não existe"));

        // tentar decrementar (se available==0 não mexe) -> 409
        int updated = repo.hold(restaurantId, slotTime);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sem disponibilidade");
        }
    }

    public void release(UUID restaurantId, Instant slotTime) {
        // se não existe -> 404
        repo.find(restaurantId, slotTime)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot não existe"));

        repo.release(restaurantId, slotTime);
    }
}
