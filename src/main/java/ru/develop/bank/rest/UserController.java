package ru.develop.bank.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.develop.bank.dto.UpdatedUserDto;
import ru.develop.bank.dto.UserAfterTransfer;
import ru.develop.bank.service.UserService;

import javax.validation.constraints.Email;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user/{userId}")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/phone")
    public UpdatedUserDto addPhoneNumber(@PathVariable Long userId,
                                         @RequestParam(value = "phone") String phoneNumber) {
        log.info("Добавление пользователю с id {} номера телефона: {}", userId, phoneNumber);
        return userService.addPhoneNumber(userId, phoneNumber);
    }

    @PatchMapping("/phone")
    public UpdatedUserDto updatePhoneNumber(@PathVariable Long userId,
                                            @RequestParam(value = "previous") String previousPhoneNumber,
                                            @RequestParam(value = "new") String newPhoneNumber) {
        log.info("Обновление у пользователя с id {} номера телефона {} на {}",
                userId, previousPhoneNumber, newPhoneNumber);
        return userService.updatePhoneNumber(userId, previousPhoneNumber, newPhoneNumber);
    }

    @DeleteMapping("/phone")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhoneNumber(@PathVariable Long userId,
                                  @RequestParam(value = "phone") String phoneNumber) {
        log.info("Удаление пользователем с id {} телефона {}", userId, phoneNumber);
        userService.deletePhoneNumber(userId, phoneNumber);
    }

    @PostMapping("/email")
    public UpdatedUserDto addEmail(@PathVariable Long userId,
                                   @RequestParam(value = "email") @Email String email) {
        log.info("Добавление пользователю с id {} email: {}", userId, email);
        return userService.addEmail(userId, email);
    }

    @PatchMapping("/email")
    public UpdatedUserDto updateEmail(@PathVariable Long userId,
                                      @RequestParam(value = "previous") String previousEmail,
                                      @RequestParam(value = "new") String newEmail) {
        log.info("Обновление у пользователя с id {} email {} на {}",
                userId, previousEmail, newEmail);
        return userService.updateEmail(userId, previousEmail, newEmail);
    }

    @DeleteMapping("/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmail(@PathVariable Long userId,
                            @RequestParam(value = "email") String email) {
        log.info("Удаление пользователем с id {} email {}", userId, email);
        userService.deleteEmail(userId, email);
    }

    @PatchMapping("/transfer/{recipientId}")
    public UserAfterTransfer transferOfMoneyToTheRecipient(@PathVariable Long userId,
                                                           @PathVariable Long recipientId,
                                                           @RequestParam(value = "sum") @Positive Long sum) {
        log.info("Перевод пользователем с id {} суммы {} получателю с id {}", userId, sum, recipientId);
        return userService.transferOfMoneyToTheRecipient(userId, recipientId, sum);
    }
}
