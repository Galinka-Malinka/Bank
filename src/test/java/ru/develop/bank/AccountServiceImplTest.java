package ru.develop.bank;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.develop.bank.dto.UserDto;
import ru.develop.bank.model.User;
import ru.develop.bank.service.AccountService;
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
public class AccountServiceImplTest {

    private final EntityManager entityManager;

    private final UserService userService;

    private final AccountService accountService;

    @Test
    void shouldAccrueInterest() {
        createUser(1);
        createUser(2);

       accountService.accrueInterest();

        TypedQuery<User> query = entityManager.createQuery("Select u from User u", User.class);
        List<User> users = query.getResultList();

        assertThat(users, notNullValue());
        assertThat(users.size(), is(2));
        assertThat(users.get(0).getAccountBalance(), is(1000L));

        try {
            Thread.sleep(59 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        TypedQuery<User> query2 = entityManager.createQuery("Select u from User u", User.class);
        List<User> users2 = query2.getResultList();
        assertThat(users2.get(0).getAccountBalance(), is(1050L));
    }

    public UserDto createUser(Integer n) {
        UserDto userDto = UserDto.builder()
                .login("Login" + n)
                .password("Password" + n)
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
