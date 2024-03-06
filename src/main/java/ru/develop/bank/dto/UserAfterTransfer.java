package ru.develop.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserAfterTransfer {

    Long userId;

    Long accountBalance;
}
