package ru.practicum.shareit.Item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceImplTest {
    private final EntityManager em;
    private final ItemService service;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;

    private Long userId;

    @BeforeEach
    public void setUp() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("testUserName");
        userRequestDto.setEmail("testUserEmail");

        userId = userService.create(userRequestDto).getId();
    }

    @Test
    public void createTest() {
        RequestItemDto createDto = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );

        service.create(createDto, userId);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item res = query.setParameter("name", createDto.getName()).getSingleResult();

        assertThat(res.getId(), notNullValue());
        assertThat(res.getName(), equalTo(createDto.getName()));
        assertThat(res.getDescription(), equalTo(createDto.getDescription()));
        assertThat(res.getAvailable(), equalTo(createDto.getAvailable()));
        assertThat(res.getRequest(), nullValue());
    }

    @Test
    public void handleNotFoundUser() {
        RequestItemDto createDto = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );

        Assertions.assertThrows(NotFoundException.class, () -> service.create(createDto, userId + 1));
    }

    @Test
    public void handleItemRequestNotFound() {
        RequestItemDto createDto = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                1L
        );

        Assertions.assertThrows(NotFoundException.class, () -> service.create(createDto, userId));
    }

    @Test
    public void createItemWithRequestTest() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("testUserName");
        userRequestDto.setEmail("otherEmail");

        Long requesterId = userService.create(userRequestDto).getId();
        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto();
        createItemRequestDto.setDescription("testDescription");
        Long requestId = itemRequestService.create(createItemRequestDto, requesterId).getId();

        RequestItemDto createDto = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                requestId
        );

        service.create(createDto, userId);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item res = query.setParameter("name", createDto.getName()).getSingleResult();

        assertThat(res.getRequest(), notNullValue());
        assertThat(res.getRequest().getId(), equalTo(requestId));
    }

    @Test
    public void updateTest() {
        RequestItemDto createDto = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );

        service.create(createDto, userId);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", createDto.getName()).getSingleResult();
        Long itemId = item.getId();

        RequestItemDto updateDto = new RequestItemDto(
                "updName",
                "updDescription",
                false,
                null
        );

        service.update(itemId, updateDto, userId);
        query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        item = query.setParameter("id", itemId).getSingleResult();

        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo(updateDto.getName()));
        assertThat(item.getDescription(), equalTo(updateDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(updateDto.getAvailable()));
    }

    @Test
    public void handleUserIsNotOwner() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("otherUserName");
        userRequestDto.setEmail("otherUserEmail");
        Long otherUserId = userService.create(userRequestDto).getId();

        RequestItemDto createDto = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );

        service.create(createDto, userId);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", createDto.getName()).getSingleResult();
        Long itemId = item.getId();

        RequestItemDto updateDto = new RequestItemDto(
                "updName",
                "updDescription",
                false,
                null
        );

        Assertions.assertThrows(ConditionsNotMetException.class, () -> service.update(itemId, updateDto, otherUserId));
    }

    @Test
    public void nullNameUpdateTest() {
        RequestItemDto oldItem = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );

        service.create(oldItem, userId);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", oldItem.getName()).getSingleResult();
        Long itemId = item.getId();

        RequestItemDto newItem = new RequestItemDto(
                null,
                "updDescription",
                false,
                null
        );

        service.update(itemId, newItem, userId);
        query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        item = query.setParameter("id", itemId).getSingleResult();

        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo(oldItem.getName()));
        assertThat(item.getDescription(), equalTo(newItem.getDescription()));
        assertThat(item.getAvailable(), equalTo(newItem.getAvailable()));
    }

    @Test
    public void nullDescriptionUpdateTest() {
        RequestItemDto oldItem = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );

        service.create(oldItem, userId);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", oldItem.getName()).getSingleResult();
        Long itemId = item.getId();

        RequestItemDto newItem = new RequestItemDto(
                "updName",
                null,
                false,
                null
        );

        service.update(itemId, newItem, userId);
        query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        item = query.setParameter("id", itemId).getSingleResult();

        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo(newItem.getName()));
        assertThat(item.getDescription(), equalTo(oldItem.getDescription()));
        assertThat(item.getAvailable(), equalTo(newItem.getAvailable()));
    }

    @Test
    public void nullAvailableUpdateTest() {
        RequestItemDto oldItem = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );

        service.create(oldItem, userId);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", oldItem.getName()).getSingleResult();
        Long itemId = item.getId();

        RequestItemDto newItem = new RequestItemDto(
                "updName",
                "updDescription",
                null,
                null
        );

        service.update(itemId, newItem, userId);
        query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        item = query.setParameter("id", itemId).getSingleResult();

        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo(newItem.getName()));
        assertThat(item.getDescription(), equalTo(newItem.getDescription()));
        assertThat(item.getAvailable(), equalTo(oldItem.getAvailable()));
    }

    @Test
    public void findByIdTest() {
        RequestItemDto createDto = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );
        service.create(createDto, userId);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", createDto.getName()).getSingleResult();
        Long itemId = item.getId();

        ResponseItemDto responseDto = service.findById(itemId);

        assertThat(responseDto.getId(), equalTo(itemId));
        assertThat(responseDto.getName(), equalTo(createDto.getName()));
        assertThat(responseDto.getDescription(), equalTo(createDto.getDescription()));
        assertThat(responseDto.getAvailable(), equalTo(createDto.getAvailable()));
    }

    @Test
    public void handleNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(9999L));
    }

    @Test
    public void findAllByOwnerTest() {
        RequestItemDto createDto = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );
        service.create(createDto, userId);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", createDto.getName()).getSingleResult();
        Long itemId = item.getId();
        createLastBooking(itemId);
        createNextBooking(itemId);

        List<OwnerItemDto> res = service.findAllByOwner(userId);
        assertThat(res.size(), equalTo(1));

        OwnerItemDto resItem = res.getFirst();
        assertThat(resItem.getId(), equalTo(itemId));
        assertThat(resItem.getName(), equalTo(createDto.getName()));
        assertThat(resItem.getDescription(), equalTo(createDto.getDescription()));
        assertThat(resItem.getAvailable(), equalTo(createDto.getAvailable()));
        assertThat(resItem.getLastStart(), notNullValue());
        assertThat(resItem.getLastEnd(), notNullValue());
        assertThat(resItem.getNextStart(), notNullValue());
        assertThat(resItem.getNextEnd(), notNullValue());
    }

    @Test
    public void findBySearchTest() {
        RequestItemDto createDto = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );
        service.create(createDto, userId);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", createDto.getName()).getSingleResult();

        String searchText = "test";

        List<ResponseItemDto> res = service.findBySearch(searchText);
        assertThat(res.size(), equalTo(1));

        ResponseItemDto resItem = res.getFirst();

        assertThat(resItem.getId(), equalTo(item.getId()));
        assertThat(resItem.getName(), equalTo(createDto.getName()));
        assertThat(resItem.getDescription(), equalTo(createDto.getDescription()));
        assertThat(resItem.getAvailable(), equalTo(createDto.getAvailable()));
    }

    @Test
    public void handleBlankText() {
        RequestItemDto createDto = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );
        service.create(createDto, userId);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", createDto.getName()).getSingleResult();

        String searchText = "";

        List<ResponseItemDto> res = service.findBySearch(searchText);
        assertThat(res.isEmpty(), is(true));
    }

    @Test
    public void postCommentTest() {
        RequestItemDto createDto = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );
        service.create(createDto, userId);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", createDto.getName()).getSingleResult();
        Long itemId = item.getId();

        CreateCommentDto comment = new CreateCommentDto();
        comment.setText("testComment");

        User commentator = createAndApproveBooking(itemId);

        CommentDto result = service.postComment(comment, itemId, commentator.getId());

        assertThat(result, notNullValue());
        assertThat(result.getText(), equalTo(comment.getText()));
        assertThat(result.getAuthorName(), equalTo(commentator.getName()));
        assertThat(result.getItem().getId(), equalTo(itemId));
    }

    @Test
    public void handleDontUsed() {
        RequestItemDto createDto = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );
        service.create(createDto, userId);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", createDto.getName()).getSingleResult();
        Long itemId = item.getId();

        CreateCommentDto comment = new CreateCommentDto();
        comment.setText("testComment");

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("bookerName");
        userRequestDto.setEmail("bookerEmail");
        User commentator = userService.create(userRequestDto);

        Assertions.assertThrows(ConditionsNotMetException.class, ()
                -> service.postComment(comment, itemId, commentator.getId()));
    }

    private User createAndApproveBooking(Long itemId) {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("bookerName");
        userRequestDto.setEmail("bookerEmail");
        User booker = userService.create(userRequestDto);

        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        bookingDto.setItemId(itemId);

        BookingDto book = bookingService.createBooking(bookingDto, booker.getId());
        Long bookId = book.getId();

        bookingService.approve(userId, bookId, true);

        return booker;
    }

    private void createLastBooking(Long itemId) {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("lastBookerName");
        userRequestDto.setEmail("lastBookerEmail");
        User booker = userService.create(userRequestDto);

        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        bookingDto.setItemId(itemId);

        BookingDto book = bookingService.createBooking(bookingDto, booker.getId());
        Long bookId = book.getId();

        bookingService.approve(userId, bookId, true);
    }

    private void createNextBooking(Long itemId) {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("nextBookerName");
        userRequestDto.setEmail("nextBookerEmail");
        User booker = userService.create(userRequestDto);

        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(itemId);

        BookingDto book = bookingService.createBooking(bookingDto, booker.getId());
        Long bookId = book.getId();

        bookingService.approve(userId, bookId, true);
    }
}
