package ru.develop.bank.rest;

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
public class SearchController {

    private final UserService userService;

    @GetMapping(path = "/user")
    public List<UserDto> searchUsers(@RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "birthday", required = false) LocalDate birthday,
                                     @RequestParam(value = "phone", required = false) String phoneNumber,
                                     @RequestParam(value = "email", required = false) @Email String email,
                                     @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                     @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                     @RequestParam(value = "sort", required = false) String sort) {
        log.info("Поиск пользователя по параметрам: name {}, birthday {}, phoneNumber {}, email {}," +
                " from {}, size {}, sort {}", name, birthday, phoneNumber, email, from, size, sort);
        return userService.searchUsers(name, birthday, phoneNumber, email, from, size, sort);
    }

}
