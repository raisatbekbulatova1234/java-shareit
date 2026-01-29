package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImplTest {
    private final EntityManager em;
    private final UserService userService;

    @Test
    public void createTest() {
        UserRequestDto userDto = makeUserDto("testName", "testEmail");

        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        assertThat(user.getName(), equalTo(userDto.getName()));
    }

    @Test
    public void handleCreateUserEmailDuplicated() {
        UserRequestDto userDto = makeUserDto("testName", "testEmail");
        userService.create(userDto);

        UserRequestDto userDtoDuplicateEmail = makeUserDto("testNameSec", "testEmail");

        assertThrows(DuplicatedDataException.class, () -> userService.create(userDtoDuplicateEmail));
    }

    @Test
    public void updateTest() {
        UserRequestDto userDto = makeUserDto("testName", "testEmail");
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
        Long id = user.getId();

        UserRequestDto updUserDto = makeUserDto("updName", "updEmail");

        userService.update(id, updUserDto);
        query = em.createQuery("select u from User u where u.id = :id", User.class);
        user = query.setParameter("id", id).getSingleResult();

        assertThat(user.getId(), equalTo(id));
        assertThat(user.getEmail(), equalTo(updUserDto.getEmail()));
        assertThat(user.getName(), equalTo(updUserDto.getName()));
    }

    @Test
    public void userWithNullNameShouldUpdateCorrectly() {
        UserRequestDto userDto = makeUserDto("testName", "testEmail");
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
        Long id = user.getId();

        UserRequestDto nullNameUserDto = makeUserDto(null, "updEmail");

        userService.update(id, nullNameUserDto);
        query = em.createQuery("select u from User u where u.id = :id", User.class);
        user = query.setParameter("id", id).getSingleResult();

        assertThat(user.getId(), equalTo(id));
        assertThat(user.getEmail(), equalTo(nullNameUserDto.getEmail()));
        assertThat(user.getName(), equalTo(userDto.getName()));
    }

    @Test
    public void userWithNullEmailShouldUpdateCorrectly() {
        UserRequestDto userDto = makeUserDto("testName", "testEmail");
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
        Long id = user.getId();

        UserRequestDto nullEmailUserDto = makeUserDto("updName", null);

        userService.update(id, nullEmailUserDto);
        query = em.createQuery("select u from User u where u.id = :id", User.class);
        user = query.setParameter("id", id).getSingleResult();

        assertThat(user.getId(), equalTo(id));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        assertThat(user.getName(), equalTo(nullEmailUserDto.getName()));
    }

    @Test
    public void handleDuplicateEmailWhenUpdate() {
        UserRequestDto userDto = makeUserDto("testName", "testEmail");
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
        Long id = user.getId();

        userDto = makeUserDto("testName", "duplicatedEmail");
        userService.create(userDto);

        UserRequestDto updUserDto = makeUserDto("updName", userDto.getEmail());

        Assertions.assertThrows(DuplicatedDataException.class, () -> userService.update(id, updUserDto));
    }

    @Test
    public void findAllTest() {
        UserRequestDto firstDto = makeUserDto("firstName", "firstEmail");
        UserRequestDto secDto = makeUserDto("secName", "secEmail");

        userService.create(firstDto);
        userService.create(secDto);

        List<User> users = userService.findAll();

        assertThat(users, notNullValue());
        assertThat(users.size(), equalTo(2));
    }

    @Test
    public void findByIdTest() {
        UserRequestDto userDto = makeUserDto("testName", "testEmail");

        userService.create(userDto);
        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User targetUser = query.setParameter("email", userDto.getEmail()).getSingleResult();

        User resulUser = userService.findById(targetUser.getId());

        assertThat(resulUser.getEmail(), equalTo(userDto.getEmail()));
        assertThat(resulUser.getName(), equalTo(userDto.getName()));
    }

    @Test
    public void handleNotFoundUser() {
        Assertions.assertThrows(NotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    public void deleteTest() {
        UserRequestDto userDto = makeUserDto("testName", "testEmail");
        userService.create(userDto);
        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User targetUser = query.setParameter("email", userDto.getEmail()).getSingleResult();

        boolean res = userService.deleteById(targetUser.getId());

        assertThat(res, equalTo(true));
    }

    private UserRequestDto makeUserDto(String name, String email) {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName(name);
        userRequestDto.setEmail(email);

        return userRequestDto;
    }
}
