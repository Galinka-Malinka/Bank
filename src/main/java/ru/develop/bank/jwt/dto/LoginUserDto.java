package ru.develop.bank.jwt.dto;

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
public class LoginUserDto {
    @NotNull
    @NotBlank
    private String login;

    @NotNull
    @NotBlank
    private String password;
}
