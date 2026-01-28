package ru.practicum.shareit.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoForRequestAnswer;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
public class ItemMapperTest {
    private User user;
    private Item item;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("email");

        item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("description");
        item.setOwner(user);
        item.setAvailable(true);
    }

    @Test
    public void toOwnerItemDtoTest() {
        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        lastBooking.setEnd(LocalDateTime.of(2020, 1, 2, 0, 0, 0));
        lastBooking.setItem(item);
        lastBooking.setBooker(user);
        lastBooking.setStatus(BookingStatus.APPROVED);

        Booking nextBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.of(2020, 1, 3, 0, 0, 0));
        lastBooking.setEnd(LocalDateTime.of(2020, 1, 4, 0, 0, 0));
        lastBooking.setItem(item);
        lastBooking.setBooker(user);
        lastBooking.setStatus(BookingStatus.APPROVED);

        OwnerItemDto res = ItemMapper.toOwnerItemDto(item, lastBooking, nextBooking);

        assertThat(res, is(notNullValue()));
        assertThat(res.getId(), is(item.getId()));
        assertThat(res.getName(), is(item.getName()));
        assertThat(res.getDescription(), is(item.getDescription()));
        assertThat(res.getAvailable(), is(item.getAvailable()));
        assertThat(res.getLastStart(), is(lastBooking.getStart()));
        assertThat(res.getLastEnd(), is(lastBooking.getEnd()));
        assertThat(res.getNextStart(), is(nextBooking.getStart()));
        assertThat(res.getNextEnd(), is(nextBooking.getEnd()));
    }

    @Test
    public void toItemDtoForRequestAnswerTest() {
        ItemDtoForRequestAnswer res = ItemMapper.toItemDtoForRequestAnswer(item);

        assertThat(res, is(notNullValue()));
        assertThat(res.getId(), is(item.getId()));
        assertThat(res.getName(), is(item.getName()));
        assertThat(res.getOwnerId(), is(item.getOwner().getId()));
    }
}
