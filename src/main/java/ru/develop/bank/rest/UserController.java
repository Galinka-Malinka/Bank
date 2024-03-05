package ru.develop.bank.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.develop.bank.dto.UpdatedUserDto;
import ru.develop.bank.service.UserService;

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

    @PatchMapping("/{userId}/phone")
    public UpdatedUserDto updatePhoneNumber(@PathVariable Long userId,
                                            @RequestParam(value = "previous") String previousPhoneNumber,
                                            @RequestParam(value = "new") String newPhoneNumber) {
        log.info("Обновление у пользователя с id {} номера телефона {} на {}",
                userId, previousPhoneNumber, newPhoneNumber);
        return userService.updatePhoneNumber(userId, previousPhoneNumber, newPhoneNumber);
    }

}
