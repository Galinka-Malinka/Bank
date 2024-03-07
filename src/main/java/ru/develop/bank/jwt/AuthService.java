package ru.develop.bank.jwt;

import io.jsonwebtoken.Claims;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.develop.bank.exception.AuthException;
import ru.develop.bank.exception.NotFoundException;
import ru.develop.bank.model.User;
import ru.develop.bank.storage.UserStorage;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserStorage userStorage;
    private final Map<String, String> refreshStorage = new HashMap<>();// переделать в PostgresSQL
    private final JwtProvider jwtProvider;

    public JwtResponse login(@NonNull JwtRequest authRequest)  {
        if (!userStorage.existsByLogin(authRequest.getLogin())) {
            throw new NotFoundException("Пользователя с логином " + authRequest.getLogin() + " не существуетю");
        }
        final User user = userStorage.findByLogin(authRequest.getLogin());
        if (user.getPassword().equals(authRequest.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getLogin(), refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Неправильный пароль.");
        }
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                if (!userStorage.existsByLogin(login)) {
                    throw new NotFoundException("Пользователя с логином " + login + " не существуетю");
                }
                final User user = userStorage.findByLogin(login);
                final String accessToken = jwtProvider.generateAccessToken(user);
                return new JwtResponse(accessToken, null);
            }
        }
        return new JwtResponse(null, null);
    }

    public JwtResponse refresh(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                if (!userStorage.existsByLogin(login)) {
                    throw new NotFoundException("Пользователя с логином " + login + " не существуетю");
                }
                final User user = userStorage.findByLogin(login);
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getLogin(), newRefreshToken);
                return new JwtResponse(accessToken, refreshToken);
            }
        }
        throw new AuthException("Невалидный JWT токен");
    }


    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}

