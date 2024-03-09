package ru.develop.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Обновлённые данные счёта клиента после трансфера")
public class UserAfterTransfer {

    @Schema(description = "Идентификатор пользователя")
    Long userId;

    @Schema(description = "Остаток на банковском счету пользователя")
    Long accountBalance;
}
