package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 1;
    private final ItemMapper itemMapper;

    private final UserService userService;
    private final UserMapper userMapper;


    @Override
    public ItemDto add(ItemDto itemDto, int ownerId) {
        userService.validateById(ownerId);
        Item item = itemMapper.toItem(itemDto);
        item.setId(id++);
        item.setOwner(userMapper.toUser(userService.getById(ownerId)));
        items.put(item.getId(), item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(int itemId, int ownerId, ItemDto newItemDto) {
        validateById(itemId);
        Item item = items.get(itemId);
        if (item.getOwner().getId() != ownerId) {
            throw new AccessException("Only owner can update item");
        }

        if (newItemDto.getName() != null) {
            item.setName(newItemDto.getName());
        }
        if (newItemDto.getDescription() != null) {
            item.setDescription(newItemDto.getDescription());
        }
        if (newItemDto.getAvailable() != null) {
            item.setAvailable(newItemDto.getAvailable());
        }

        return itemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public ItemDto getById(int itemId) {
        validateById(itemId);
        return itemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getAll(int ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(item -> itemMapper.toItemDto(item))
                .toList();
    }

    @Override
    public List<ItemDto> find(String text) {
        if (text.equals("")) return new ArrayList<>();

        return items.values().stream()
                .filter(item -> item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription() != null && item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .map(item -> itemMapper.toItemDto(item))
                .toList();
    }

    private void validateById(int id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException(String.format("Item with id %d is not found.", id));
        }
    }


}
