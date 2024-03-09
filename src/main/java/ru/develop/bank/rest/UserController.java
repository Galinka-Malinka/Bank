package ru.develop.bank.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "API клиентов", description = "Возможность изменять контакты и осуществлять переводы")
public class UserController {
    private final UserService userService;

    @PostMapping("/phone")
    @Operation(summary = "Добавление номера телефона",
            description = "Позволяет добавить дополнительный номер телефона пользователя")
    @SecurityRequirement(name = "JWT")
    public UpdatedUserDto addPhoneNumber(@PathVariable
                                         @Parameter(description = "Идентификатор пользователя") Long userId,
                                         @RequestParam(value = "phone")
                                         @Parameter(description = "Номер телефона") String phoneNumber) {
        log.info("Добавление пользователю с id {} номера телефона: {}", userId, phoneNumber);
        return userService.addPhoneNumber(userId, phoneNumber);
    }

    @PatchMapping("/phone")
    @Operation(summary = "Изменение номера телефона", description = "Позволяет изменить номер телефона пользователя")
    @SecurityRequirement(name = "JWT")
    public UpdatedUserDto updatePhoneNumber(@PathVariable
                                            @Parameter(description = "Идентификатор пользователя") Long userId,
                                            @RequestParam(value = "previous")
                                            @Parameter(description = "Прежний номер телефона")
                                            String previousPhoneNumber,
                                            @RequestParam(value = "new")
                                            @Parameter(description = "Новый номер телефона") String newPhoneNumber) {
        log.info("Обновление у пользователя с id {} номера телефона {} на {}",
                userId, previousPhoneNumber, newPhoneNumber);
        return userService.updatePhoneNumber(userId, previousPhoneNumber, newPhoneNumber);
    }

    @DeleteMapping("/phone")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление номера телефона",
            description = "Позволяет удалить не единственный номер телефона пользователя")
    @SecurityRequirement(name = "JWT")
    public void deletePhoneNumber(@PathVariable @Parameter(description = "Идентификатор пользователя") Long userId,
                                  @RequestParam(value = "phone")
                                  @Parameter(description = "Номер телефона") String phoneNumber) {
        log.info("Удаление пользователем с id {} телефона {}", userId, phoneNumber);
        userService.deletePhoneNumber(userId, phoneNumber);
    }

    @PostMapping("/email")
    @Operation(summary = "Добавление email", description = "Позволяет добавить дополнительный email пользователя")
    @SecurityRequirement(name = "JWT")
    public UpdatedUserDto addEmail(@PathVariable @Parameter(description = "Идентификатор пользователя") Long userId,
                                   @RequestParam(value = "email")
                                   @Parameter(description = "Email") @Email String email) {
        log.info("Добавление пользователю с id {} email: {}", userId, email);
        return userService.addEmail(userId, email);
    }

    @PatchMapping("/email")
    @Operation(summary = "Изменение email", description = "Позволяет изменить email пользователя")
    @SecurityRequirement(name = "JWT")
    public UpdatedUserDto updateEmail(@PathVariable @Parameter(description = "Идентификатор пользователя") Long userId,
                                      @RequestParam(value = "previous")
                                      @Parameter(description = "Передыдущий email") String previousEmail,
                                      @RequestParam(value = "new")
                                      @Parameter(description = "Новый email") String newEmail) {
        log.info("Обновление у пользователя с id {} email {} на {}",
                userId, previousEmail, newEmail);
        return userService.updateEmail(userId, previousEmail, newEmail);
    }

    @DeleteMapping("/email")
    @Operation(summary = "Удаление email", description = "Позволяет удалить не единственный email пользователя")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "JWT")
    public void deleteEmail(@PathVariable @Parameter(description = "Идентификатор пользователя") Long userId,
                            @RequestParam(value = "email") @Parameter(description = "Email") String email) {
        log.info("Удаление пользователем с id {} email {}", userId, email);
        userService.deleteEmail(userId, email);
    }

    @PatchMapping("/transfer/{recipientId}")
    @Operation(summary = "Перевод денег",
            description = "Позволяет осуществлять перевод денег пользователя другому пользователю")
    @SecurityRequirement(name = "JWT")
    public UserAfterTransfer transferOfMoneyToTheRecipient(@PathVariable @Parameter(description =
            "Идентификатор пользователя отправителя")
                                                           Long userId,
                                                           @PathVariable @Parameter(description =
                                                                   "Идентификатор пользователя получателя")
                                                           Long recipientId,
                                                           @RequestParam(value = "sum")
                                                           @Parameter(description = "Сумма перевода")
                                                           @Positive Long sum) {
        log.info("Перевод пользователем с id {} суммы {} получателю с id {}", userId, sum, recipientId);
        return userService.transferOfMoneyToTheRecipient(userId, recipientId, sum);
    }
}
