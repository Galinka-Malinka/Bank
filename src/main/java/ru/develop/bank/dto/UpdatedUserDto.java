package ru.develop.bank.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatedUserDto {

    Long id;

    List<String> phoneNumbers;

    List<String> emails;

}
