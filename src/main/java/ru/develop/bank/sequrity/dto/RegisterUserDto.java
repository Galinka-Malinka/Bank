package ru.develop.bank.sequrity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Данные пользователя для регистрации")
public class RegisterUserDto {

    @NotNull
    @NotBlank
    @Schema(description = "Логин")
    String login;

    @NotNull
    @NotBlank
    @Schema(description = "Пароль")
    String password;

    @NotNull
    @NotBlank
    @Schema(description = "ФИО")
    String name;

    @NotNull
    @Past
    @Schema(description = "Дата рождения")
    LocalDate birthday;

    @NotNull
    @Positive
    @Schema(description = "Первоначальный баланс")
    Long accountBalance;

    @NotNull
    @Schema(description = "Список телефонов")
    List<String> phoneNumbers;

    @NotNull
    @Schema(description = "Список emails")
    List<String> emails;


}
