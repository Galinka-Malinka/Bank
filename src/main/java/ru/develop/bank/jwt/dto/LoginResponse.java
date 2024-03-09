package ru.develop.bank.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoginResponse {

    private String token;

    private long expiresIn;

    public String getToken() {
        return token;
    }
}
