package ru.develop.bank.sequrity.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.develop.bank.dto.UserDto;
import ru.develop.bank.exception.AlreadyExistsException;
import ru.develop.bank.exception.NotFoundException;
import ru.develop.bank.exception.ValidationException;
import ru.develop.bank.mapper.UserMapper;
import ru.develop.bank.model.Email;
import ru.develop.bank.model.PhoneNumber;
import ru.develop.bank.model.User;
import ru.develop.bank.sequrity.dto.LoginUserDto;
import ru.develop.bank.sequrity.dto.RegisterUserDto;
import ru.develop.bank.storage.EmailStorage;
import ru.develop.bank.storage.PhoneNumberStorage;
import ru.develop.bank.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserStorage userStorage;
    private final PhoneNumberStorage phoneNumberStorage;
    private final EmailStorage emailStorage;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserDto signup(RegisterUserDto input) {
        List<String> phoneNumbers = input.getPhoneNumbers().stream().distinct().toList();
        for (String phoneNumber : phoneNumbers) {
            if (phoneNumber.isBlank()) {
                throw new ValidationException("Номер телефона не может быть пустой строкой.");
            }
            if (phoneNumberStorage.existsByPhoneNumber(phoneNumber)) {
                throw new AlreadyExistsException("Номер телефона " + phoneNumber + " уже используется.");
            }
        }

        List<String> emails = input.getEmails().stream().distinct().toList();
        for (String email : emails) {
            if (emailStorage.existsByEmail(email)) {
                throw new AlreadyExistsException("Email " + email + " уже используется.");
            }
        }

        if (userStorage.existsByLogin(input.getLogin())) {
            throw new AlreadyExistsException("Пользователь с логином " + input.getLogin() + " уже существует.");
        }

        User user = userStorage.save(User.builder()
                .login(input.getLogin())
                .password(passwordEncoder.encode(input.getPassword()))  //Закодировали password
                .name(input.getName())
                .birthday(input.getBirthday())
                .accountBalance(input.getAccountBalance())
                .limitOfInterestAccrual(input.getAccountBalance() * 207 / 100)
                .build());

        phoneNumbers.forEach(p -> phoneNumberStorage.save(
                PhoneNumber.builder()
                        .user(user)
                        .phoneNumber(p)
                        .build()));
        emails.forEach(e -> emailStorage.save(
                Email.builder()
                        .user(user)
                        .email(e)
                        .build()
        ));

        return UserMapper.toUserDto(user, phoneNumbers, emails);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.getLogin(), input.getPassword()));

        return userStorage.findByLogin(input.getLogin())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
