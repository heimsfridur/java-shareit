package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoExport;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto add(@RequestBody ItemDto itemDto,
                       @RequestHeader(USER_ID_HEADER) int ownerId) {
        return itemService.add(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto newItemDto,
                          @PathVariable int itemId,
                          @RequestHeader(USER_ID_HEADER) int ownerId) {
        return itemService.update(itemId, ownerId, newItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable int itemId,
                           @RequestHeader(USER_ID_HEADER) int userId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(USER_ID_HEADER) int ownerId) {
        return itemService.getAll(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam("text") String text) {
        return itemService.find(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoExport addComment(@PathVariable int itemId,
                                       @RequestHeader(USER_ID_HEADER) int userId,
                                       @RequestBody CommentDto comment) {
        return itemService.addComment(itemId, userId, comment);
    }
}
