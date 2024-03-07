package ru.develop.bank.jwt;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JwtResponse {

    public JwtResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;
}
