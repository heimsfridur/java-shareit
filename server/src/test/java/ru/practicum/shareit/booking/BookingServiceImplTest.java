package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.BookingApproveAccessException;
import ru.practicum.shareit.exceptions.BookingUnavailableItemException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingDtoRequest bookingDtoRequest;
    private BookingDto bookingDtoResponse;
    private Booking booking;
    private User booker;
    private User owner;
    private Item item;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


    @BeforeEach
    void setup() {
        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusHours(5))
                .end(LocalDateTime.now().plusHours(5))
                .build();

        booker = User.builder().id(1).email("booker@test.ru").name("booker").build();
        owner = User.builder().id(2).email("user@test.ru").name("user").build();


        item = Item.builder()
                .id(1)
                .name("item")
                .description("cool item")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.parse("2024-08-25T14:00:00"))
                .end(LocalDateTime.parse("2024-08-26T14:00:00"))
                .status(BookingStatus.WAITING)
                .build();

        bookingDtoResponse = BookingDto.builder()
                .id(1)
                .start(LocalDateTime.parse("2024-08-25T14:00:00"))
                .end(LocalDateTime.parse("2024-08-26T14:00:00"))
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void createBooking_ShouldReturnBookingDto() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.create(bookingDtoRequest, booker.getId());

        assertNotNull(result);
        assertEquals(bookingDtoResponse.getId(), result.getId());
        assertEquals(bookingDtoResponse.getStart().format(formatter), result.getStart().format(formatter));
        assertEquals(bookingDtoResponse.getEnd().format(formatter), result.getEnd().format(formatter));
        assertEquals(bookingDtoResponse.getStatus(), result.getStatus());
    }


    @Test
    void createBooking_whenItemUnavailable_thenShouldThrowException() {
        item.setAvailable(false);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(BookingUnavailableItemException.class, () -> {
            bookingService.create(bookingDtoRequest, booker.getId());
        });
    }

    @Test
    void createBooking_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.create(bookingDtoRequest, booker.getId());
        });
    }

    @Test
    void approveBooking_ShouldReturnApprovedBookingDto() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingRepository.existsById(1)).thenReturn(true);

        BookingDto result = bookingService.approve(1, true, owner.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void approveBooking_whenNotOwner_thenThrowAccessException() {
        when(bookingRepository.existsById(1)).thenReturn(true);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(BookingApproveAccessException.class, () ->
                bookingService.approve(1, true, booker.getId())
        );
    }

    @Test
    void approveBooking_whenBookingNotExist_ShouldThrowNotFoundException() {
        when(bookingRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                bookingService.approve(42, true, owner.getId())
        );
    }

    @Test
    void getBooking_whenUserIsBooker_ShouldReturnBookingDto() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.existsById(anyInt())).thenReturn(true);

        BookingDto result = bookingService.get(1, booker.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    void getAllBookings_whenStateIsAll_ShouldReturnAllBookings() {
        when(bookingRepository.findAllByBookerId(anyInt())).thenReturn(List.of(booking));
        doNothing().when(userService).validateById(anyInt());

        List<BookingDto> result = bookingService.getAll(booker.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }
}
