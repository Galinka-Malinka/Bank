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
public class NewUserDto {

    Long id;

    @NotNull
    @NotBlank
    String login;

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
