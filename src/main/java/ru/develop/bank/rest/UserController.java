package ru.develop.bank.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.develop.bank.dto.UpdatedUserDto;
import ru.develop.bank.service.UserService;

import javax.validation.constraints.NotBlank;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/{userId}/phone")
    public UpdatedUserDto addPhoneNumber(@PathVariable Long userId,
                                         @RequestParam(value = "phone") String phoneNumber) {
        log.info("Добавление пользователю с id {} номера телефона: {}", userId, phoneNumber);
        return userService.addPhoneNumber(userId, phoneNumber);
    }

}
