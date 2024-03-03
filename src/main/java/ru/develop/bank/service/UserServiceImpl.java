package ru.develop.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.develop.bank.dto.NewUserDto;
import ru.develop.bank.exception.AlreadyExistsException;
import ru.develop.bank.mapper.UserMapper;
import ru.develop.bank.model.Email;
import ru.develop.bank.model.PhoneNumber;
import ru.develop.bank.model.User;
import ru.develop.bank.storage.EmailStorage;
import ru.develop.bank.storage.PhoneNumberStorage;
import ru.develop.bank.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final PhoneNumberStorage phoneNumberStorage;
    private final EmailStorage emailStorage;

    @Override
    public NewUserDto create(NewUserDto newUserDto) {

        List<String> phoneNumbers = newUserDto.getPhoneNumbers().stream().distinct().toList();
        for (String phoneNumber : phoneNumbers) {
            if (phoneNumberStorage.existsByPhoneNumber(phoneNumber)) {
                throw new AlreadyExistsException("Номер телефона " + phoneNumber + " уже импользуется.");
            }
        }

        List<String> emails = newUserDto.getEmails().stream().distinct().toList();
        for (String email : emails) {
            if (emailStorage.existsByEmail(email)) {
                throw new AlreadyExistsException("Email " + email + " уже используется.");
            }
        }

        if (userStorage.existsByLogin(newUserDto.getLogin())) {
            throw new AlreadyExistsException("Пользователь с логином " + newUserDto.getLogin() + " уже существует.");
        }

        User user = userStorage.save(UserMapper.toUser(newUserDto));
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
}
