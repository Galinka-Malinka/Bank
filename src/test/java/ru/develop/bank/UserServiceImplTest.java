package ru.develop.bank;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.develop.bank.dto.NewUserDto;
import ru.develop.bank.dto.UpdatedUserDto;
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
        NewUserDto newUserDto = createUser();
        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", 1).getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getId(), is(1L));
        assertThat(user.getLogin(), equalTo(newUserDto.getLogin()));
        assertThat(user.getBirthday(), equalTo(newUserDto.getBirthday()));
        assertThat(user.getAccountBalance(), is(newUserDto.getAccountBalance()));

        TypedQuery<PhoneNumber> queryForPhoneNumbers = entityManager.createQuery(
                "Select p from PhoneNumber p where p.phoneNumber in :list", PhoneNumber.class);
        List<PhoneNumber> savedPhoneNumbers = queryForPhoneNumbers.setParameter("list",
                newUserDto.getPhoneNumbers()).getResultList();
        assertThat(savedPhoneNumbers, notNullValue());
        assertThat(savedPhoneNumbers.size(), is(2));
        assertThat(savedPhoneNumbers.get(0).getPhoneNumber(), equalTo(newUserDto.getPhoneNumbers().get(0)));
        assertThat(savedPhoneNumbers.get(1).getPhoneNumber(), equalTo(newUserDto.getPhoneNumbers().get(1)));

        TypedQuery<Email> queryForEmails = entityManager.createQuery(
                "Select e from Email e where e.email in :list", Email.class);
        List<Email> savedEmails = queryForEmails.setParameter("list", newUserDto.getEmails()).getResultList();
        assertThat(savedEmails, notNullValue());
        assertThat(savedEmails.size(), is(2));
        assertThat(savedEmails.get(0).getEmail(), equalTo(newUserDto.getEmails().get(0)));
        assertThat(savedEmails.get(1).getEmail(), equalTo(newUserDto.getEmails().get(1)));

        assertThrows(AlreadyExistsException.class, () -> userService.create(newUserDto),
                "Номер телефона " + newUserDto.getPhoneNumbers().get(0) + " уже используется.");
        newUserDto.setPhoneNumbers(List.of("+73332221100"));
        assertThrows(AlreadyExistsException.class, () -> userService.create(newUserDto),
                "Email " + newUserDto.getEmails().get(0) + " уже используется.");
        newUserDto.setEmails(List.of("NewEmail@mail.ru"));
        assertThrows(AlreadyExistsException.class, () -> userService.create(newUserDto),
                "Пользователь с логином " + newUserDto.getLogin() + " уже существует.");
    }

    @Test
    void shouldAddPhoneNumber() {
        createUser();
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
        NewUserDto newUserDto = createUser();

        UpdatedUserDto updatedUserDto = userService.updatePhoneNumber(1L, newUserDto.getPhoneNumbers().get(0),
                "+73332221100");

        assertThat(updatedUserDto, notNullValue());
        assertThat(updatedUserDto.getPhoneNumbers().size(), is(2));
        assertThat(updatedUserDto.getPhoneNumbers().contains("+73332221100"), equalTo(true));
        assertThat(updatedUserDto.getPhoneNumbers().contains(newUserDto.getPhoneNumbers().get(0)),
                equalTo(false));

        assertThrows(ValidationException.class, () -> userService.updatePhoneNumber(1L,
                        newUserDto.getPhoneNumbers().get(0), ""),
                "Номер телефона не может быть пустой строкой.");
        assertThrows(AlreadyExistsException.class, () -> userService.updatePhoneNumber(1L,
                        newUserDto.getPhoneNumbers().get(0), newUserDto.getPhoneNumbers().get(1)),
                "Номер телефона " + newUserDto.getPhoneNumbers().get(1) + " уже используется.");
        assertThrows(ValidationException.class, () -> userService.updatePhoneNumber(1L, "",
                "+73332221199"), "Номер телефона не может быть пустой строкой.");
        assertThrows(NotFoundException.class, () -> userService.updatePhoneNumber(1L,
                        "+78529634174", "+73332221199"),
                "Номер телефона +78529634174 не найден.");
        assertThrows(NotFoundException.class, () -> userService.updatePhoneNumber(2L,
                        newUserDto.getPhoneNumbers().get(0), "+73332221199"),
                "Пользователя с id 2 не существует.");

        userService.create(NewUserDto.builder()
                .login("Login2")
                .name("Name2 and LastName2")
                .birthday(LocalDate.of(1992, 1, 1))
                .accountBalance(1000L)
                .emails(List.of("NewMail@mail.ru"))
                .phoneNumbers(List.of("+75846529966"))
                .build());

        assertThrows(ConflictException.class, () -> userService.updatePhoneNumber(2L,
                        newUserDto.getPhoneNumbers().get(1), "+73332221199"),
                "Пользователь с id 2 не может обновить номер телефона " + newUserDto.getPhoneNumbers().get(0) +
                        " т.к. не является его владельцем.");
    }

    @Test
    void shouldDeletePhoneNumber() {
        NewUserDto newUserDto = createUser();
        String phoneNumber = newUserDto.getPhoneNumbers().get(0);
        userService.deletePhoneNumber(1L, phoneNumber);
        TypedQuery<PhoneNumber> query = entityManager
                .createQuery("Select p from PhoneNumber p join p.user u where u.id = :id", PhoneNumber.class);
        List<PhoneNumber> phoneNumbers = query.setParameter("id", 1).getResultList();
        assertThat(phoneNumbers.size(), is(1));
        assertThat(phoneNumbers.get(0).getPhoneNumber(), equalTo(newUserDto.getPhoneNumbers().get(1)));

        assertThrows(NotFoundException.class, () ->
                userService.deletePhoneNumber(2L, newUserDto.getPhoneNumbers().get(1)),
                "Пользователя с id 2 не существует.");

    }

    public NewUserDto createUser() {
        NewUserDto newUserDto = NewUserDto.builder()
                .login("Login")
                .name("Name and LastName")
                .birthday(LocalDate.of(1990, 01, 01))
                .accountBalance(1000L)
                .emails(List.of("Email@mail.ru", "Gmail@gmail.ru"))
                .phoneNumbers(List.of("+79998887766", "+75554443322"))
                .build();

        userService.create(newUserDto);
        newUserDto.setId(1L);
        return newUserDto;
    }
}
