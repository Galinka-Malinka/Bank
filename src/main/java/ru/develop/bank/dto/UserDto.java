package ru.develop.bank.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    Long id;

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

    Long limitOfInterestAccrual;

    @NotNull
    List<String> phoneNumbers;

    @NotNull
    List<String> emails;

}
