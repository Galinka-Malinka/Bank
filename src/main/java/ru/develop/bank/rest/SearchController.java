package ru.develop.bank.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.develop.bank.dto.UserDto;
import ru.develop.bank.service.UserService;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "API поиска", description = "Возможность поиска клиентов")
public class SearchController {

    private final UserService userService;

    @GetMapping(path = "/user")
    @Operation(summary = "Поиск клиентов",
            description = "Позволяет искать клиентов с использованием фильтрации и пагинации")
    public List<UserDto> searchUsers(@RequestParam(value = "name", required = false)
                                     @Parameter(description = "ФИО") String name,
                                     @RequestParam(value = "birthday", required = false)
                                     @Parameter(description = "Дата рождения") LocalDate birthday,
                                     @RequestParam(value = "phone", required = false)
                                     @Parameter(description = "Телефон") String phoneNumber,
                                     @RequestParam(value = "email", required = false)
                                     @Parameter(description = "Email") @Email String email,
                                     @RequestParam(value = "from", required = false, defaultValue = "0")
                                     @Parameter(description = "Номер страницы") Integer from,
                                     @RequestParam(value = "size", required = false, defaultValue = "10")
                                     @Parameter(description = "Размер страницы") Integer size,
                                     @RequestParam(value = "sort", required = false)
                                     @Parameter(description = "Сортировка") String sort) {
        log.info("Поиск пользователя по параметрам: name {}, birthday {}, phoneNumber {}, email {}," +
                " from {}, size {}, sort {}", name, birthday, phoneNumber, email, from, size, sort);
        return userService.searchUsers(name, birthday, phoneNumber, email, from, size, sort);
    }

}
