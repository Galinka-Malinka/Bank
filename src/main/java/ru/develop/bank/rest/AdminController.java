package ru.develop.bank.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.develop.bank.dto.UserDto;
import ru.develop.bank.service.UserService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
@Slf4j
public class AdminController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Добавление пользователя " + userDto);
        return userService.create(userDto);
    }

}
