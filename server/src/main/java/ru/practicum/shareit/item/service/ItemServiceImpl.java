package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.UnavailableToAddCommentException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDtoExport;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto add(ItemDto itemDto, int ownerId) {
        userService.validateById(ownerId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userService.getById(ownerId)));

        Integer requestId = itemDto.getRequestId();
        if (requestId != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException(String.format("ItemRequest with id %d is not found", requestId)));
            item.setRequest(itemRequest);
        }

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
    public ItemDto getById(int itemId, int userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Can not find item with id %d.", itemId)));

        List<CommentDtoExport> comments = commentRepository.findAllByItemId(item.getId())
                .stream()
                .map(CommentMapper::toCommentDtoExport)
                .toList();


        ItemDto itemDto = itemMapper.toItemDto(itemRepository.findById(itemId).get());
        itemDto.setComments(comments);

        if (item.getOwner().getId() == userId) {
            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusNotOrderByStartDesc(itemId, LocalDateTime.now(), BookingStatus.REJECTED);
            if (!lastBooking.isEmpty()) {
                itemDto.setLastBooking(BookingMapper.toBookingDto(lastBooking.get()));
            }
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusNotOrderByStart(itemId, LocalDateTime.now(), BookingStatus.REJECTED);
            if (!nextBooking.isEmpty()) {
                itemDto.setNextBooking(BookingMapper.toBookingDto(nextBooking.get()));
            }
        }

        return itemDto;
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

    @Override
    public CommentDtoExport addComment(int itemId, int userId, Comment comment) {
        User author =  userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Can not find user with id %d.", userId)));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Can not find item with id %d.", itemId)));

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemId(userId, itemId);

        boolean isAbleToAddComment = bookings.stream()
                .anyMatch(booking -> booking.getEnd().isBefore(LocalDateTime.now())
                        && booking.getStatus().equals(BookingStatus.APPROVED));

        if (!isAbleToAddComment) {
            throw new UnavailableToAddCommentException("Can not add comment, because booking is not ended or wasnt approved.");
        }

        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthor(author);

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDtoExport(savedComment);

    }

    public void validateById(int id) {
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException(String.format("Item with id %d is not found.", id));
        }
    }


}
