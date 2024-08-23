//package ru.practicum.shareit.booking;
//
////import com.fasterxml.jackson.databind.ObjectMapper;
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////import org.mockito.Mockito;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
////import org.springframework.boot.test.mock.mockito.MockBean;
////import org.springframework.http.HttpStatus;
////import org.springframework.http.MediaType;
////import org.springframework.http.ResponseEntity;
////import org.springframework.test.web.servlet.MockMvc;
////import ru.practicum.shareit.booking.dto.BookingDtoRequest;
////
////import java.time.LocalDateTime;
////
////import static org.mockito.ArgumentMatchers.any;
////import static org.mockito.ArgumentMatchers.eq;
////import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
////import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
////@WebMvcTest(BookingController.class)
//public class BookingControllerTest {
////    @Autowired
////    private MockMvc mockMvc;
////
////    @MockBean
////    private BookingClient bookingClient;
////
////    @Autowired
////    private ObjectMapper objectMapper;
////
////    private BookingDtoRequest bookingDtoRequest;
////
////
////    @BeforeEach
////    void setUp() {
////        bookingDtoRequest = BookingDtoRequest.builder()
////                .itemId(1)
////                .start(LocalDateTime.parse("2024-08-25T14:00:00"))
////                .end(LocalDateTime.parse("2024-08-26T14:00:00"))
////                .build();
////    }
////
////    @Test
////    void createBooking_ShouldReturnStatusOk() throws Exception {
////        Mockito.when(bookingClient.create(any(BookingDtoRequest.class), eq(1L)))
////                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
////
////        mockMvc.perform(post("/bookings")
////                        .header("X-Sharer-User-Id", 1)
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
////                .andExpect(status().isOk());
////    }
//
//}
