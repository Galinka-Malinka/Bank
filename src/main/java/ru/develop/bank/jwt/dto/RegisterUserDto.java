package ru.develop.bank.jwt.dto;

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
public class RegisterUserDto {

    @NotNull
    @NotBlank
    String login;

    @NotNull
    @NotBlank
    String password;

    @NotNull
    @NotBlank
    String name;

    @NotNull
    @Past
    LocalDate birthday;

    @NotNull
    @Positive
    Long accountBalance;

    @NotNull
    List<String> phoneNumbers;

    @NotNull
    List<String> emails;


}
