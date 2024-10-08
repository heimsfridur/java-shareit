package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoExport;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, int ownerId);

    ItemDto update(int itemId, int ownerId, ItemDto newItemDto);

    ItemDto getById(int itemId, int userId);

    List<ItemDto> getAll(int ownerId);

    List<ItemDto> find(String text);

    void validateById(int id);

    CommentDtoExport addComment(int itemId, int userId, CommentDto comment);
}
