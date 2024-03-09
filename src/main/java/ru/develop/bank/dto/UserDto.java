package ru.develop.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Сущность пользователя")
public class UserDto {

    @Schema(description = "Идентификатор")
    Long id;

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
    @Schema(description = "Банковский счёт")
    Long accountBalance;

    @Schema(description = "Максимальное допустимое значение счёта для начисления процентов")
    Long limitOfInterestAccrual;

    @NotNull
    @Schema(description = "Список телефонов пользователя")
    List<String> phoneNumbers;

    @NotNull
    @Schema(description = "Список emails пользователя")
    List<String> emails;

}
