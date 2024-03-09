package ru.develop.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Обновлённые контактные данные клиента")
public class UpdatedUserDto {

    @Schema(description = "Идентификатор пользователя")
    Long id;

    @Schema(description = "Обновлённый список телефонов")
    List<String> phoneNumbers;

    @Schema(description = "Обновлённый список emails")
    List<String> emails;

}
