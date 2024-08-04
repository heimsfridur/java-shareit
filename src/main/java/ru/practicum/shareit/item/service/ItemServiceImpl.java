package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
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
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto add(ItemDto itemDto, int ownerId) {
        userService.validateById(ownerId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userMapper.toUser(userService.getById(ownerId)));
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(int itemId, int ownerId, ItemDto newItemDto) {
        validateById(itemId);
        Item oldItem = itemRepository.findById(itemId).get();

        if (oldItem.getOwner().getId() != ownerId) {
            throw new AccessException("Only owner can update item");
        }

        if (newItemDto.getName() != null) {
            oldItem.setName(newItemDto.getName());
        }
        if (newItemDto.getDescription() != null) {
            oldItem.setDescription(newItemDto.getDescription());
        }
        if (newItemDto.getAvailable() != null) {
            oldItem.setAvailable(newItemDto.getAvailable());
        }

        Item item = itemRepository.save(oldItem);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(int itemId) {
        validateById(itemId);
        return itemMapper.toItemDto(itemRepository.findById(itemId).get());
    }

    @Override
    public List<ItemDto> getAll(int ownerId) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(item -> itemMapper.toItemDto(item))
                .toList();
    }

    @Override
    public List<ItemDto> find(String text) {
        if (text.equals("")) return new ArrayList<>();

        return itemRepository.findAll().stream()
                .filter(item -> item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription() != null && item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .map(item -> itemMapper.toItemDto(item))
                .toList();
    }

    private void validateById(int id) {
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException(String.format("Item with id %d is not found.", id));
        }
    }


}
