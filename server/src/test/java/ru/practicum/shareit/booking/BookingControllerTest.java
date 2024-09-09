package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.BookingApproveAccessException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDtoRequest bookingDtoRequest;

    private BookingDto bookingDtoResponse;
    private User owner;
    private User booker;
    private Item item;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


    @BeforeEach
    void setup() {
        owner = User.builder().id(1).name("Owner").email("owner@test.com").build();
        booker = User.builder().id(2).name("Booker").email("booker@test.com").build();
        item = Item.builder().id(1).name("Item").description("Item Description").available(true).owner(owner).build();

        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusHours(5))
                .end(LocalDateTime.now().plusHours(5))
                .build();

        bookingDtoResponse = BookingDto.builder()
                .id(1)
                .start(LocalDateTime.parse("2024-08-25T14:00:00"))
                .end(LocalDateTime.parse("2024-08-26T14:00:00"))
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build();
    }

    @Test
    void createBooking_ShouldReturnStatusOk() throws Exception {
        Mockito.when(bookingService.create(any(BookingDtoRequest.class), anyInt()))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$.start").value(bookingDtoResponse.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingDtoResponse.getEnd().format(formatter)))
                .andExpect(jsonPath("$.status").value(bookingDtoResponse.getStatus().toString()));
    }

    @Test
    void approveBooking_ShouldReturnApprovedStatus() throws Exception {
        bookingDtoResponse.setStatus(BookingStatus.APPROVED);
        int bookingId = 1;
        boolean approved = true;
        int userId = 1;

        Mockito.when(bookingService.approve(eq(bookingId), eq(approved), eq(userId)))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$.start").value(bookingDtoResponse.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingDtoResponse.getEnd().format(formatter)))
                .andExpect(jsonPath("$.status").value(bookingDtoResponse.getStatus().toString()));
    }

    @Test
    void approveBooking_ShouldReturnAccessDenied() throws Exception {
        int bookingId = 1;
        boolean approved = true;
        int userId = 2;

        Mockito.when(bookingService.approve(eq(bookingId), eq(approved), eq(userId)))
                .thenThrow(new BookingApproveAccessException("Only item owner can approve booking."));

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBooking_ShouldReturnBookingDetails() throws Exception {
        int bookingId = 1;
        int userId = 1;

        Mockito.when(bookingService.get(eq(bookingId), eq(userId)))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$.start").value(bookingDtoResponse.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingDtoResponse.getEnd().format(formatter)))
                .andExpect(jsonPath("$.status").value(bookingDtoResponse.getStatus().toString()))
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()));
    }

    @Test
    void getBooking_ShouldReturnAccessDenied() throws Exception {
        int bookingId = 1;
        int userId = 3; // ID, не совпадающий ни с владельцем, ни с booker'ом

        Mockito.when(bookingService.get(eq(bookingId), eq(userId)))
                .thenThrow(new AccessException("Only booker or item owner can access booking details."));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllBookings_ShouldReturnBookingList() throws Exception {
        int userId = 1;
        BookingState state = BookingState.ALL;
        List<BookingDto>  bookings = List.of(bookingDtoResponse);

        Mockito.when(bookingService.getAll(eq(userId), eq(state)))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookings.size()))
                .andExpect(jsonPath("$[0].id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDtoResponse.getStatus().toString()));
    }

    @Test
    void getAllByOwner_ShouldReturnStatusOk_WithAllState() throws Exception {
        List<BookingDto> bookings = List.of(bookingDtoResponse);

        Mockito.when(bookingService.getAllByOwner(anyInt(), eq(BookingState.ALL)))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookings.size()))
                .andExpect(jsonPath("$[0].id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDtoResponse.getStart().format(formatter)))
                .andExpect(jsonPath("$[0].end").value(bookingDtoResponse.getEnd().format(formatter)))
                .andExpect(jsonPath("$[0].status").value(bookingDtoResponse.getStatus().toString()));
    }

}
