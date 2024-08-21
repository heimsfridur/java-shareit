package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer>  {
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(int userId);

    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(int userId);

}
