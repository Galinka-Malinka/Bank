package ru.develop.bank.service;

import ru.develop.bank.dto.UpdatedUserDto;
import ru.develop.bank.dto.UserAfterTransfer;
import ru.develop.bank.dto.UserDto;

import java.time.LocalDate;
import java.util.List;

public interface UserService {

    UpdatedUserDto addPhoneNumber(Long userId, String phoneNumber);

    UpdatedUserDto updatePhoneNumber(Long userId, String previousPhoneNumber, String newPhoneNumber);

    void deletePhoneNumber(Long userId, String phoneNumber);

    UpdatedUserDto addEmail(Long userId, String email);

    UpdatedUserDto updateEmail(Long userId, String previousEmail, String newEmail);

    void deleteEmail(Long userId, String email);

    List<UserDto> searchUsers(String name, LocalDate birthday, String phoneNumber, String email,
                              Integer from, Integer size, String sort);

    UserAfterTransfer transferOfMoneyToTheRecipient(Long userId, Long recipientId, Long sum);

}
