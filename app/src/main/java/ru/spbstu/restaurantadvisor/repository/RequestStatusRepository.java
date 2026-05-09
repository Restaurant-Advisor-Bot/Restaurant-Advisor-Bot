package ru.spbstu.restaurantadvisor.repository;

import ru.spbstu.restaurantadvisor.model.RequestStatus;
import java.util.Optional;

public interface RequestStatusRepository {
    Optional<RequestStatus> findActiveGeneration(long telegramId, String restaurantId);
    RequestStatus save(RequestStatus status);
    void updateStatus(long id, String newStatus);
}