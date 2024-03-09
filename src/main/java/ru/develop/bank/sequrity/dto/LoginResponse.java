package ru.develop.bank.sequrity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Токен и его срок годности, предоставленные клиенту")
public class LoginResponse {

    @Schema(description = "Токен")
    private String token;

    @Schema(description = "Срок годности токена")
    private long expiresIn;

    public String getToken() {
        return token;
    }
}
