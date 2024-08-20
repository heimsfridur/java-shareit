package ru.practicum.shareit.request.RequestService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;


    @Override
    public ItemRequestDto add(ItemRequestDto requestDto, int userId) {
        validateUser(userId);
        User requester =  userRepository.findById(userId).get();
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto);

        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestDto> getByUserId(int userId) {
        validateUser(userId);

        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);

        return getItemDtosToRequestList(requests);
    }


    @Override
    public List<ItemRequestDto> getAll(int userId) {
        validateUser(userId);

        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);

        return getItemDtosToRequestList(requests);
    }

    @Override
    public ItemRequestDto getById(int userId, int requestId) {
        validateUser(userId);
        validateRequest(requestId);

        ItemRequest request = itemRequestRepository.findById(requestId).get();

        return getItemDtosToRequestList(List.of(request)).getFirst();

    }

    private List<ItemRequestDto> getItemDtosToRequestList(List<ItemRequest> requests) {
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .map(requestDto -> {
                    List<ItemDto> itemDtoList = itemRepository.findByRequestId(requestDto.getId())
                            .stream()
                            .map(ItemMapper::toItemDto)
                            .toList();

                    requestDto.setItems(itemDtoList);
                    return requestDto;
                })
                .toList();
    }

    private void validateUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Can not find user with id %d.", userId));
        }
    }

    private void validateRequest(int requestId) {
        if (!itemRequestRepository.existsById(requestId)) {
            throw new NotFoundException(String.format("Can not find request with id %d.", requestId));
        }
    }


}
