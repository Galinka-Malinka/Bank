package ru.develop.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.develop.bank.dto.NewUserDto;
import ru.develop.bank.dto.UpdatedUserDto;
import ru.develop.bank.exception.AlreadyExistsException;
import ru.develop.bank.exception.ConflictException;
import ru.develop.bank.exception.NotFoundException;
import ru.develop.bank.exception.ValidationException;
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
            checkNewPhoneNumber(phoneNumber);
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

    @Override
    public UpdatedUserDto addPhoneNumber(Long userId, String phoneNumber) {

        checkNewPhoneNumber(phoneNumber);

        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует."));

        phoneNumberStorage.save(PhoneNumber.builder()
                .user(user)
                .phoneNumber(phoneNumber)
                .build());

        List<PhoneNumber> phoneNumbers = phoneNumberStorage.findAllByUserId(userId);
        return UserMapper.toUpdatedUserDtoByPhone(userId, phoneNumbers);
    }

    @Override
    public UpdatedUserDto updatePhoneNumber(Long userId, String previousPhoneNumber, String newPhoneNumber) {
        checkNewPhoneNumber(newPhoneNumber);
        checkExistingPhoneNumber(previousPhoneNumber);
        checkTheExistenceOfTheUser(userId);
        PhoneNumber phoneNumber = phoneNumberStorage.findByPhoneNumber(previousPhoneNumber);
        checkThePhoneOwner(userId, phoneNumber);
        phoneNumberStorage.setPhoneNumber(newPhoneNumber, phoneNumber.getId());
        /* Аналог:
               phoneNumber.setPhoneNumber(newPhoneNumber);
               phoneNumberStorage.save(phoneNumber);*/
        List<PhoneNumber> phoneNumbers = phoneNumberStorage.findAllByUserId(userId);
        return UserMapper.toUpdatedUserDtoByPhone(userId, phoneNumbers);
    }

    @Override
    public void deletePhoneNumber(Long userId, String phoneNumber) {
        checkTheExistenceOfTheUser(userId);
        checkExistingPhoneNumber(phoneNumber);
        PhoneNumber phoneNumberBeingDeleted = phoneNumberStorage.findByPhoneNumber(phoneNumber);
        checkThePhoneOwner(userId, phoneNumberBeingDeleted);
        if (phoneNumberStorage.findAllByUserId(userId).size() == 1) {
            throw new ConflictException("Нельзя удалять единственный номер телефона пользователя.");
        }
        phoneNumberStorage.delete(phoneNumberBeingDeleted);
    }

    @Override
    public UpdatedUserDto addEmail(Long userId, String email) {
        checkNewEmail(email);
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует."));

        emailStorage.save(Email.builder()
                .user(user)
                .email(email)
                .build());

        List<Email> emailList = emailStorage.findAllByUserId(userId);
        return UserMapper.toUpdatedUserDtoByEmail(userId, emailList);
    }

    @Override
    public UpdatedUserDto updateEmail(Long userId, String previousEmail, String newEmail) {
        checkNewEmail(newEmail);
        checkExistingEmail(previousEmail);
        Email email = emailStorage.findByEmail(previousEmail);
        checkTheEmailOwner(userId, email);
        email.setEmail(newEmail);
        emailStorage.save(email);
        List<Email> emailList = emailStorage.findAllByUserId(userId);
        return UserMapper.toUpdatedUserDtoByEmail(userId, emailList);
    }

    @Override
    public void deleteEmail(Long userId, String email) {
        checkTheExistenceOfTheUser(userId);
        checkExistingEmail(email);
        Email emailBeingDeleted = emailStorage.findByEmail(email);
        checkTheEmailOwner(userId, emailBeingDeleted);
        if (emailStorage.findAllByUserId(userId).size() == 1) {
            throw new ConflictException("Нельзя удалять единственный email пользователя.");
        }
        emailStorage.delete(emailBeingDeleted);
    }

    public void checkNewPhoneNumber(String phoneNumber) {
        if (phoneNumber.isBlank()) {
            throw new ValidationException("Номер телефона не может быть пустой строкой.");
        }
        if (phoneNumberStorage.existsByPhoneNumber(phoneNumber)) {
            throw new AlreadyExistsException("Номер телефона " + phoneNumber + " уже используется.");
        }
    }

    public void checkExistingPhoneNumber(String phoneNumber) {
        if (phoneNumber.isBlank()) {
            throw new ValidationException("Номер телефона не может быть пустой строкой.");
        }
        if (!phoneNumberStorage.existsByPhoneNumber(phoneNumber)) {
            throw new NotFoundException("Номер телефона " + phoneNumber + " не найден.");
        }
    }

    public void checkThePhoneOwner(Long userId, PhoneNumber phoneNumber) {
        if (!phoneNumber.getUser().getId().equals(userId)) {
            throw new ConflictException("Пользователь с id " + userId + " не может обновить номер телефона " +
                    phoneNumber.getPhoneNumber() + " т.к. не является его владельцем.");
        }
    }

    public void checkTheExistenceOfTheUser(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователя с id " + userId + " не существует.");
        }
    }

    public void checkNewEmail(String email) {
        if (email.isBlank()) {
            throw new ValidationException("Email не может быть пустой строкой.");
        }
        if (emailStorage.existsByEmail(email)) {
            throw new AlreadyExistsException("Email " + email + " уже используется.");
        }
    }

    public void checkExistingEmail(String email) {
        if (email.isBlank()) {
            throw new ValidationException("Email не может быть пустой строкой.");
        }
        if (!emailStorage.existsByEmail(email)) {
            throw new NotFoundException("Email " + email + " не найден.");
        }
    }

    public void checkTheEmailOwner(Long userId, Email email) {
        if (!email.getUser().getId().equals(userId)) {
            throw new ConflictException("Пользователь с id " + userId + " не может обновить email " +
                    email.getEmail() + " т.к. не является его владельцем.");
        }
    }
}
