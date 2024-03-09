package ru.develop.bank;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.develop.bank.exception.AlreadyExistsException;
import ru.develop.bank.model.Email;
import ru.develop.bank.model.PhoneNumber;
import ru.develop.bank.model.User;
import ru.develop.bank.sequrity.dto.LoginUserDto;
import ru.develop.bank.sequrity.dto.RegisterUserDto;
import ru.develop.bank.sequrity.service.AuthenticationService;

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
public class AuthenticationServiceTest {
    private final EntityManager entityManager;
    private final AuthenticationService authenticationService;


    @Test
    void shouldCreateUser() {
        RegisterUserDto registerUserDto = RegisterUserDto.builder()
                .login("Login")
                .password("Password")
                .name("Name and LastName")
                .birthday(LocalDate.of(1990, 01, 01))
                .accountBalance(1000L)
                .emails(List.of("Email@mail.ru", "Gmail@gmail.ru"))
                .phoneNumbers(List.of("+79998887766", "+75554443322"))
                .build();

        authenticationService.signup(registerUserDto);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", 1).getSingleResult();
        assertThat(user, notNullValue());
        assertThat(user.getId(), is(1L));
        assertThat(user.getLogin(), equalTo(registerUserDto.getLogin()));
        assertThat(user.getBirthday(), equalTo(registerUserDto.getBirthday()));
        assertThat(user.getAccountBalance(), is(registerUserDto.getAccountBalance()));

        TypedQuery<PhoneNumber> queryForPhoneNumbers = entityManager.createQuery(
                "Select p from PhoneNumber p where p.phoneNumber in :list", PhoneNumber.class);
        List<PhoneNumber> savedPhoneNumbers = queryForPhoneNumbers.setParameter("list",
                registerUserDto.getPhoneNumbers()).getResultList();
        assertThat(savedPhoneNumbers, notNullValue());
        assertThat(savedPhoneNumbers.size(), is(2));
        assertThat(savedPhoneNumbers.get(0).getPhoneNumber(), equalTo(registerUserDto.getPhoneNumbers().get(0)));
        assertThat(savedPhoneNumbers.get(1).getPhoneNumber(), equalTo(registerUserDto.getPhoneNumbers().get(1)));

        TypedQuery<Email> queryForEmails = entityManager.createQuery(
                "Select e from Email e where e.email in :list", Email.class);
        List<Email> savedEmails = queryForEmails.setParameter("list", registerUserDto.getEmails()).getResultList();
        assertThat(savedEmails, notNullValue());
        assertThat(savedEmails.size(), is(2));
        assertThat(savedEmails.get(0).getEmail(), equalTo(registerUserDto.getEmails().get(0)));
        assertThat(savedEmails.get(1).getEmail(), equalTo(registerUserDto.getEmails().get(1)));

        assertThrows(AlreadyExistsException.class, () -> authenticationService.signup(registerUserDto),
                "Номер телефона " + registerUserDto.getPhoneNumbers().get(0) + " уже используется.");
        registerUserDto.setPhoneNumbers(List.of("+73332221100"));
        assertThrows(AlreadyExistsException.class, () -> authenticationService.signup(registerUserDto),
                "Email " + registerUserDto.getEmails().get(0) + " уже используется.");
        registerUserDto.setEmails(List.of("NewEmail@mail.ru"));
        assertThrows(AlreadyExistsException.class, () -> authenticationService.signup(registerUserDto),
                "Пользователь с логином " + registerUserDto.getLogin() + " уже существует.");
    }

    @Test
    void shouldAuthenticate() {
        RegisterUserDto registerUserDto = RegisterUserDto.builder()
                .login("Login")
                .password("Password")
                .name("Name and LastName")
                .birthday(LocalDate.of(1990, 01, 01))
                .accountBalance(1000L)
                .emails(List.of("Email@mail.ru", "Gmail@gmail.ru"))
                .phoneNumbers(List.of("+79998887766", "+75554443322"))
                .build();

        authenticationService.signup(registerUserDto);

        User user = authenticationService.authenticate(LoginUserDto.builder()
                .login(registerUserDto.getLogin())
                .password(registerUserDto.getPassword())
                .build());

        assertThat(user, notNullValue());
        assertThat(user.getId(), is(1L));
        assertThat(user.getLogin(), equalTo(registerUserDto.getLogin()));
    }
}