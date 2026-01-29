package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.CustomHttpHeader;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mvc;

    private CreateBookingDto createBookingDto;
    private BookingDto bookingDto;

    @BeforeEach
    public void setup() {
        createBookingDto = new CreateBookingDto();
        createBookingDto.setStart(LocalDateTime.of(2026, 1, 1, 0, 0, 0));
        createBookingDto.setEnd(LocalDateTime.of(2026, 1, 2, 0, 0, 0));
        createBookingDto.setItemId(1L);

        bookingDto = new BookingDto(
                1L,
                createBookingDto.getStart(),
                createBookingDto.getEnd(),
                null,
                null,
                BookingStatus.WAITING
        );
    }

    @Test
    public void createBooking() throws Exception {
        when(bookingService.createBooking(any(), anyLong())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingDto))
                        .header(CustomHttpHeader.USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void approveTest() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(CustomHttpHeader.USER_ID, 1L)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getBookingTest() throws Exception {
        when(bookingService.findBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(CustomHttpHeader.USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getBookingsOfCurrentUserTest() throws Exception {
        when(bookingService.getBookingsByUser(anyLong(), any())).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header(CustomHttpHeader.USER_ID, 1L)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    public void getBookingsByOwner() throws Exception {
        when(bookingService.getBookingsByOwner(anyLong(), any())).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header(CustomHttpHeader.USER_ID, 1L)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }
}
