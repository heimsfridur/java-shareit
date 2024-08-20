package ru.practicum.shareit.request.RequestService;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto add(ItemRequestDto requestDto, int userId);

    List<ItemRequestDto> getByUserId(int userId);

    List<ItemRequestDto> getAll(int userId);

    ItemRequestDto getById(int userId, int requestId);
}
