package ru.develop.bank;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.develop.bank.dto.UpdatedUserDto;
import ru.develop.bank.dto.UserAfterTransfer;
import ru.develop.bank.dto.UserDto;
import ru.develop.bank.exception.AlreadyExistsException;
import ru.develop.bank.exception.ConflictException;
import ru.develop.bank.exception.NotFoundException;
import ru.develop.bank.exception.ValidationException;
import ru.develop.bank.model.Email;
import ru.develop.bank.model.PhoneNumber;
import ru.develop.bank.model.User;
import ru.develop.bank.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    private final EntityManager entityManager;

    private final UserService userService;

    @Test
    void shouldCreateUser() {
        UserDto userDto = createUser(1);
        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", 1).getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getId(), is(1L));
        assertThat(user.getLogin(), equalTo(userDto.getLogin()));
        assertThat(user.getBirthday(), equalTo(userDto.getBirthday()));
        assertThat(user.getAccountBalance(), is(userDto.getAccountBalance()));

        TypedQuery<PhoneNumber> queryForPhoneNumbers = entityManager.createQuery(
                "Select p from PhoneNumber p where p.phoneNumber in :list", PhoneNumber.class);
        List<PhoneNumber> savedPhoneNumbers = queryForPhoneNumbers.setParameter("list",
                userDto.getPhoneNumbers()).getResultList();
        assertThat(savedPhoneNumbers, notNullValue());
        assertThat(savedPhoneNumbers.size(), is(2));
        assertThat(savedPhoneNumbers.get(0).getPhoneNumber(), equalTo(userDto.getPhoneNumbers().get(0)));
        assertThat(savedPhoneNumbers.get(1).getPhoneNumber(), equalTo(userDto.getPhoneNumbers().get(1)));

        TypedQuery<Email> queryForEmails = entityManager.createQuery(
                "Select e from Email e where e.email in :list", Email.class);
        List<Email> savedEmails = queryForEmails.setParameter("list", userDto.getEmails()).getResultList();
        assertThat(savedEmails, notNullValue());
        assertThat(savedEmails.size(), is(2));
        assertThat(savedEmails.get(0).getEmail(), equalTo(userDto.getEmails().get(0)));
        assertThat(savedEmails.get(1).getEmail(), equalTo(userDto.getEmails().get(1)));

        assertThrows(AlreadyExistsException.class, () -> userService.create(userDto),
                "Номер телефона " + userDto.getPhoneNumbers().get(0) + " уже используется.");
        userDto.setPhoneNumbers(List.of("+73332221100"));
        assertThrows(AlreadyExistsException.class, () -> userService.create(userDto),
                "Email " + userDto.getEmails().get(0) + " уже используется.");
        userDto.setEmails(List.of("NewEmail@mail.ru"));
        assertThrows(AlreadyExistsException.class, () -> userService.create(userDto),
                "Пользователь с логином " + userDto.getLogin() + " уже существует.");
    }

    @Test
    void shouldAddPhoneNumber() {
        createUser(1);
        UpdatedUserDto updateResult = userService.addPhoneNumber(1L, "+71112223344");

        assertThat(updateResult, notNullValue());
        assertThat(updateResult.getPhoneNumbers().size(), is(3));
        assertThat(updateResult.getPhoneNumbers().get(2), equalTo("+71112223344"));

        assertThrows(AlreadyExistsException.class, () -> userService.addPhoneNumber(1L, "+71112223344"),
                "Номер телефона +71112223344 уже используется.");
        assertThrows(ValidationException.class, () -> userService.addPhoneNumber(1L, ""),
                "Номер телефона не может быть пустой строкой.");
        assertThrows(NotFoundException.class, () -> userService.addPhoneNumber(2L, "+71112223355"),
                "Пользователя с id 2L не существует.");
    }

    @Test
    void shouldUpdatePhoneNumber() {
        UserDto userDto = createUser(1);

        UpdatedUserDto updatedUserDto = userService.updatePhoneNumber(1L, userDto.getPhoneNumbers().get(0),
                "+73332221100");

        assertThat(updatedUserDto, notNullValue());
        assertThat(updatedUserDto.getPhoneNumbers().size(), is(2));
        assertThat(updatedUserDto.getPhoneNumbers().contains("+73332221100"), equalTo(true));
        assertThat(updatedUserDto.getPhoneNumbers().contains(userDto.getPhoneNumbers().get(0)),
                equalTo(false));

        assertThrows(ValidationException.class, () -> userService.updatePhoneNumber(1L,
                        userDto.getPhoneNumbers().get(0), ""),
                "Номер телефона не может быть пустой строкой.");
        assertThrows(AlreadyExistsException.class, () -> userService.updatePhoneNumber(1L,
                        userDto.getPhoneNumbers().get(0), userDto.getPhoneNumbers().get(1)),
                "Номер телефона " + userDto.getPhoneNumbers().get(1) + " уже используется.");
        assertThrows(ValidationException.class, () -> userService.updatePhoneNumber(1L, "",
                "+73332221199"), "Номер телефона не может быть пустой строкой.");
        assertThrows(NotFoundException.class, () -> userService.updatePhoneNumber(1L,
                        "+78529634174", "+73332221199"),
                "Номер телефона +78529634174 не найден.");
        assertThrows(NotFoundException.class, () -> userService.updatePhoneNumber(2L,
                        userDto.getPhoneNumbers().get(0), "+73332221199"),
                "Пользователя с id 2 не существует.");

        createUser(2);

        assertThrows(ConflictException.class, () -> userService.updatePhoneNumber(2L,
                        userDto.getPhoneNumbers().get(1), "+73332221199"),
                "Пользователь с id 2 не может обновить номер телефона " + userDto.getPhoneNumbers().get(0) +
                        " т.к. не является его владельцем.");
    }

    @Test
    void shouldDeletePhoneNumber() {
        UserDto userDto = createUser(1);
        String phoneNumber = userDto.getPhoneNumbers().get(0);
        userService.deletePhoneNumber(1L, phoneNumber);
        TypedQuery<PhoneNumber> query = entityManager
                .createQuery("Select p from PhoneNumber p join p.user u where u.id = :id", PhoneNumber.class);
        List<PhoneNumber> phoneNumbers = query.setParameter("id", 1).getResultList();
        assertThat(phoneNumbers.size(), is(1));
        assertThat(phoneNumbers.get(0).getPhoneNumber(), equalTo(userDto.getPhoneNumbers().get(1)));

        assertThrows(NotFoundException.class, () ->
                        userService.deletePhoneNumber(2L, userDto.getPhoneNumbers().get(1)),
                "Пользователя с id 2 не существует.");
        assertThrows(ConflictException.class, () -> userService.deletePhoneNumber(1L, userDto.getPhoneNumbers().get(1)),
                "Нельзя удалять единственный номер телефона пользователя.");
    }

    @Test
    void shouldAddEmail() {
        createUser(1);
        UpdatedUserDto updateResult = userService.addEmail(1L, "NewEmail@mail.ru");

        assertThat(updateResult, notNullValue());
        assertThat(updateResult.getEmails().size(), is(3));
        assertThat(updateResult.getEmails().get(2), equalTo("NewEmail@mail.ru"));

        assertThrows(AlreadyExistsException.class, () -> userService.addEmail(1L, "NewEmail@mail.ru"),
                "Email NewEmail@mail.ru уже используется.");
        assertThrows(ValidationException.class, () -> userService.addEmail(1L, ""),
                "Email не может быть пустой строкой.");
        assertThrows(NotFoundException.class, () -> userService.addEmail(2L, "NewEmail2@mail.ru"),
                "Пользователя с id 2L не существует.");

    }


    @Test
    void shouldUpdateEmail() {
        UserDto userDto = createUser(1);

        UpdatedUserDto updatedUserDto = userService.updateEmail(1L, userDto.getEmails().get(0),
                "NewEmail@mail.ru");

        assertThat(updatedUserDto, notNullValue());
        assertThat(updatedUserDto.getEmails().size(), is(2));
        assertThat(updatedUserDto.getEmails().contains("NewEmail@mail.ru"), equalTo(true));
        assertThat(updatedUserDto.getEmails().contains(userDto.getEmails().get(0)),
                equalTo(false));

        assertThrows(ValidationException.class, () -> userService.updateEmail(1L, userDto.getEmails().get(0),
                ""), "Email не может быть пустой строкой.");
        assertThrows(AlreadyExistsException.class, () -> userService.updateEmail(1L, userDto.getEmails().get(0),
                "NewEmail@mail.ru"), "Email NewEmail@mail.ru уже используется.");
        assertThrows(ValidationException.class, () -> userService.updateEmail(1L, "",
                "NewEmail2@mail.ru"), "Email не может быть пустой строкой.");
        assertThrows(NotFoundException.class, () -> userService.updateEmail(1L, "NotFound@mail.ru",
                        "NewEmail2@mail.ru"),
                "Email NotFound@mail.ru не найден.");
        assertThrows(NotFoundException.class, () -> userService.updateEmail(2L, userDto.getEmails().get(0),
                        "NewEmail3@mail.ru"),
                "Пользователя с id 2 не существует.");

        createUser(2);

        assertThrows(ConflictException.class, () -> userService.updateEmail(2L, userDto.getEmails().get(1),
                        "NewEmail3@mail.ru"),
                "Пользователь с id 2 не может обновить email " + userDto.getEmails().get(1) +
                        " т.к. не является его владельцем.");
    }

    @Test
    void shouldDeleteEmail() {
        UserDto userDto = createUser(1);
        String email = userDto.getEmails().get(0);
        userService.deleteEmail(1L, email);
        TypedQuery<Email> query = entityManager
                .createQuery("Select e from Email e join e.user u where u.id = :id", Email.class);
        List<Email> emailList = query.setParameter("id", 1).getResultList();
        assertThat(emailList.size(), is(1));
        assertThat(emailList.get(0).getEmail(), equalTo(userDto.getEmails().get(1)));

        assertThrows(NotFoundException.class, () ->
                        userService.deleteEmail(2L, userDto.getEmails().get(1)),
                "Пользователя с id 2 не существует.");
        assertThrows(ConflictException.class, () -> userService.deleteEmail(1L, userDto.getEmails().get(1)),
                "Нельзя удалять единственный email пользователя.");
    }

    @Test
    void shouldSearch() {
        UserDto userDto1 = createUser(1);
        userDto1.setId(1L);
        UserDto userDto2 = createUser(2);
        userDto2.setId(2L);
        List<UserDto> userDtoList = userService.searchUsers(userDto1.getName(), userDto1.getBirthday().minusDays(1),
                userDto1.getPhoneNumbers().get(0), userDto1.getEmails().get(0), 0, 10, "desc");
        assertThat(userDtoList, notNullValue());
        assertThat(userDtoList.size(), is(1));
        assertThat(userDtoList.get(0), equalTo(userDto1));

        List<UserDto> userDtoList2 = userService.searchUsers(userDto1.getName(), null,
                null, null, 0, 10, null);
        assertThat(userDtoList2, notNullValue());
        assertThat(userDtoList2.size(), is(1));
        assertThat(userDtoList2.get(0), equalTo(userDto1));

        List<UserDto> userDtoList3 = userService.searchUsers(null, userDto1.getBirthday().minusDays(1),
                null, null, 0, 10, "asc");
        assertThat(userDtoList3, notNullValue());
        assertThat(userDtoList3.size(), is(2));
        assertThat(userDtoList3.get(0), equalTo(userDto1));
        assertThat(userDtoList3.get(1), equalTo(userDto2));

        List<UserDto> userDtoList4 = userService.searchUsers(null, null,
                userDto2.getPhoneNumbers().get(1), null, 0, 10, null);
        assertThat(userDtoList4, notNullValue());
        assertThat(userDtoList4.size(), is(1));
        assertThat(userDtoList4.get(0), equalTo(userDto2));

        List<UserDto> userDtoList5 = userService.searchUsers(null, null,
                null, userDto1.getEmails().get(0), 0, 10, null);
        assertThat(userDtoList5, notNullValue());
        assertThat(userDtoList5.size(), is(1));
        assertThat(userDtoList5.get(0), equalTo(userDto1));

        List<UserDto> userDtoList6 = userService.searchUsers(null, null,
                null, null, 0, 10, "desc");
        assertThat(userDtoList6, notNullValue());
        assertThat(userDtoList6.size(), is(2));
        assertThat(userDtoList6.get(0), equalTo(userDto2));
        assertThat(userDtoList6.get(1), equalTo(userDto1));

        createUser(3);
        createUser(4);
        createUser(5);
        List<UserDto> userDtoList7 = userService.searchUsers(null, null,
                null, null, 2, 2, "asc");
        assertThat(userDtoList7, notNullValue());
        assertThat(userDtoList7.get(0).getId(), is(3L));
        assertThat(userDtoList7.get(1).getId(), is(4L));

        List<UserDto> userDtoList8 = userService.searchUsers(userDto1.getName(), null,
                userDto2.getPhoneNumbers().get(0), null, 0, 10, "asc");
        assertThat(userDtoList8, notNullValue());
        assertThat(userDtoList8.size(), is(0));
    }

    @Test
    void shouldTransfer() {
        UserDto user = createUser(1);
        UserDto recipient = createUser(2);

        UserAfterTransfer userAfterTransfer = userService
                .transferOfMoneyToTheRecipient(1L, 2L, 500L);

        assertThat(userAfterTransfer, notNullValue());
        assertThat(userAfterTransfer.getUserId(), is(1L));
        assertThat(userAfterTransfer.getAccountBalance(), is(user.getAccountBalance() - 500));

        TypedQuery<User> queryForUser = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User updatedUser = queryForUser.setParameter("id", 1).getSingleResult();

        assertThat(updatedUser.getAccountBalance(), equalTo(userAfterTransfer.getAccountBalance()));

        TypedQuery<User> queryForRecipient = entityManager
                .createQuery("Select u from User u where u.id = :id", User.class);
        User recipientAfterTransfer = queryForRecipient.setParameter("id", 2).getSingleResult();

        assertThat(recipientAfterTransfer.getAccountBalance(), equalTo(recipient.getAccountBalance() + 500));

        assertThrows(NotFoundException.class,
                () -> userService.transferOfMoneyToTheRecipient(3L, 2L, 500L),
                "Пользователя с id 3 не существует.");

        assertThrows(NotFoundException.class,
                () -> userService.transferOfMoneyToTheRecipient(1L, 3L, 500L),
                "Пользователя с id 3 не существует.");

        assertThrows(ConflictException.class,
                () -> userService.transferOfMoneyToTheRecipient(1L, 2L, 700L),
                "У пользователя с id 1 не хватает средств для перевода в размере 700");
    }


    public UserDto createUser(Integer n) {
        UserDto userDto = UserDto.builder()
                .login("Login" + n)
                .name("Name and LastName" + n)
                .birthday(LocalDate.of(1990 + n, 01, 01))
                .accountBalance(1000L)
                .emails(List.of("Email" + n + "@mail.ru", "Gmail" + n + "@gmail.ru"))
                .phoneNumbers(List.of("+7999888776" + n, "+7555444332" + n))
                .build();

        userService.create(userDto);
        userDto.setId(1L);
        return userDto;
    }
}
