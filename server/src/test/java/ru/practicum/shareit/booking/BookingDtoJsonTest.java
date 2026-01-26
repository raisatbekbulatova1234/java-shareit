package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingDtoJsonTest {
    private final JacksonTester<CreateBookingDto> jsonCreateDto;
    private final JacksonTester<BookingDto> jsonDto;

    private static final LocalDateTime TEST_START = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private static final LocalDateTime TEST_END = LocalDateTime.of(2026, 1, 2, 0, 0, 0);

    @Test
    public void createBookingDtoTest() throws Exception {
        CreateBookingDto createDto = new CreateBookingDto();
        createDto.setItemId(1L);
        createDto.setStart(TEST_START);
        createDto.setEnd(TEST_END);

        JsonContent<CreateBookingDto> res = jsonCreateDto.write(createDto);

        assertThat(res).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(res).extractingJsonPathValue("$.start").isEqualTo("2026-01-01T00:00:00");
        assertThat(res).extractingJsonPathValue("$.end").isEqualTo("2026-01-02T00:00:00");
    }

    @Test
    public void bookingDtoTest() throws Exception {
        ResponseItemDto item = new ResponseItemDto(
                1L,
                "itemName",
                "itemDescription",
                true,
                null, null,
                null
        );
        UserResponseDto user = new UserResponseDto(
                1L,
                "userEmail",
                "userName"
        );

        BookingDto dto = new BookingDto(
                1L,
                TEST_START,
                TEST_END,
                item,
                user,
                BookingStatus.WAITING
        );

        JsonContent<BookingDto> res = jsonDto.write(dto);

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathValue("$.start").isEqualTo("2026-01-01T00:00:00");
        assertThat(res).extractingJsonPathValue("$.end").isEqualTo("2026-01-02T00:00:00");

        assertThat(res).extractingJsonPathValue("$.item.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.item.name").isEqualTo("itemName");
        assertThat(res).extractingJsonPathStringValue("$.item.description").isEqualTo("itemDescription");
        assertThat(res).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);

        assertThat(res).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.booker.name").isEqualTo("userName");
        assertThat(res).extractingJsonPathStringValue("$.booker.email").isEqualTo("userEmail");

        assertThat(res).extractingJsonPathValue("$.status").isEqualTo(BookingStatus.WAITING.toString());
    }
}
