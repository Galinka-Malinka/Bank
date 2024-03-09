package ru.develop.bank.sequrity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Аутентификационные данные клиента")
public class LoginUserDto {
    @NotNull
    @NotBlank
    @Schema(description = "Логин")
    private String login;

    @NotNull
    @NotBlank
    @Schema(description = "Пароль")
    private String password;
}
