package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";


    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid ItemRequestDto requestDto,
                                      @RequestHeader(USER_ID_HEADER) int userId) {
        return itemRequestClient.add(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUserId(@RequestHeader(USER_ID_HEADER) int userId) {
        return itemRequestClient.getByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID_HEADER) int userId) {
        return itemRequestClient.getAll(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) int userId,
                                  @PathVariable int requestId) {
        return itemRequestClient.getById(userId, requestId);
    }
}
