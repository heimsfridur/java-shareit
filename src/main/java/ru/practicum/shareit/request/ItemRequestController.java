package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestService.RequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestService requestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";


    @PostMapping
    public ItemRequestDto add(@RequestBody @Valid ItemRequestDto requestDto,
                       @RequestHeader(USER_ID_HEADER) int userId) {
        return requestService.add(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getByUserId(@RequestHeader(USER_ID_HEADER) int userId) {
        return requestService.getByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(USER_ID_HEADER) int userId) {
        return requestService.getAll(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(USER_ID_HEADER) int userId,
                                  @PathVariable int requestId) {
        return requestService.getById(userId, requestId);
    }
}
