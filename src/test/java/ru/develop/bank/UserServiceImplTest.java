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
        List<String> phoneNumbers = List.of("+79998887766", "+75554443322");
        List<String> emails = List.of("Email@mail.ru", "Gmail@gmail.ru");
        NewUserDto newUserDto = NewUserDto.builder()
                .login("Login")
                .name("Name and LastName")
                .birthday(LocalDate.of(1990, 01, 01))
                .accountBalance(1000L)
                .emails(emails)
                .phoneNumbers(phoneNumbers)
                .build();

        userService.create(newUserDto);
        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", 1).getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getId(), is(1L));
        assertThat(user.getLogin(), equalTo(newUserDto.getLogin()));
        assertThat(user.getBirthday(), equalTo(newUserDto.getBirthday()));
        assertThat(user.getAccountBalance(), is(newUserDto.getAccountBalance()));

        TypedQuery<PhoneNumber> queryForPhoneNumbers = entityManager.createQuery(
                "Select p from PhoneNumber p where p.phoneNumber in :list", PhoneNumber.class);
        List<PhoneNumber> savedPhoneNumbers = queryForPhoneNumbers.setParameter("list", phoneNumbers).getResultList();
        assertThat(savedPhoneNumbers, notNullValue());
        assertThat(savedPhoneNumbers.size(), is(2));
        assertThat(savedPhoneNumbers.get(0).getPhoneNumber(), equalTo(phoneNumbers.get(0)));
        assertThat(savedPhoneNumbers.get(1).getPhoneNumber(), equalTo(phoneNumbers.get(1)));

        TypedQuery<Email> queryForEmails = entityManager.createQuery(
                "Select e from Email e where e.email in :list", Email.class);
        List<Email> savedEmails = queryForEmails.setParameter("list", emails).getResultList();
        assertThat(savedEmails, notNullValue());
        assertThat(savedEmails.size(), is(2));
        assertThat(savedEmails.get(0).getEmail(), equalTo(emails.get(0)));
        assertThat(savedEmails.get(1).getEmail(), equalTo(emails.get(1)));

        assertThrows(AlreadyExistsException.class, () -> userService.create(newUserDto),
                "Номер телефона " + phoneNumbers.get(0) + " уже используется.");
        newUserDto.setPhoneNumbers(List.of("+73332221100"));
        assertThrows(AlreadyExistsException.class, () -> userService.create(newUserDto),
                "Email " + emails.get(0) + " уже используется.");
        newUserDto.setEmails(List.of("NewEmail@mail.ru"));
        assertThrows(AlreadyExistsException.class, () -> userService.create(newUserDto),
                "Пользователь с логином " + newUserDto.getLogin() + " уже существует.");
    }

    @Test
    void shouldUpdateTheUsersPhoneNumber() {
        List<String> phoneNumbers = List.of("+79998887766", "+75554443322");
        List<String> emails = List.of("Email@mail.ru", "Gmail@gmail.ru");
        NewUserDto newUserDto = NewUserDto.builder()
                .login("Login")
                .name("Name and LastName")
                .birthday(LocalDate.of(1990, 01, 01))
                .accountBalance(1000L)
                .emails(emails)
                .phoneNumbers(phoneNumbers)
                .build();

        userService.create(newUserDto);
        UpdatedUserDto updateResult = userService.addPhoneNumber(1L, "+71112223344");

        assertThat(updateResult, notNullValue());
        assertThat(updateResult.getPhoneNumbers().size(), is(3));
        assertThat(updateResult.getPhoneNumbers().get(2), equalTo("+71112223344"));

        assertThrows(NotFoundException.class, () -> userService.addPhoneNumber(2L, "+71112223344"),
                "Пользователя с id 2L не существует.");
        assertThrows(ValidationException.class, () -> userService.addPhoneNumber(1L, ""),
                "Номер телефона не может быть пустой строкой.");


    }
}
