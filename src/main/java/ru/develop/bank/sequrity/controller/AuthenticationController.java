package ru.develop.bank.sequrity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.develop.bank.dto.UserDto;
import ru.develop.bank.model.User;
import ru.develop.bank.sequrity.dto.LoginResponse;
import ru.develop.bank.sequrity.dto.LoginUserDto;
import ru.develop.bank.sequrity.dto.RegisterUserDto;
import ru.develop.bank.sequrity.service.AuthenticationService;
import ru.develop.bank.sequrity.service.JwtService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Служебный API", description = "Возможность регистрации и аутентификации пользователя")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    @Operation(summary = "Регистрация пользователя", description = "Позволяет зарегистрировать пользователя")
    public ResponseEntity<UserDto> register(@RequestBody
                                            @Parameter(description = "Данные пользователя для регистрации")
                                            RegisterUserDto registerUserDto) {
        UserDto registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    @Operation(summary = "Аутентификация пользователя",
            description = "Позволяет зарегистрированному пользователю получить токен и его срок годности")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody
                                                      @Parameter(description = "Аутентификационные данные клиента")
                                                      LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();
        return ResponseEntity.ok(loginResponse);
    }
}
