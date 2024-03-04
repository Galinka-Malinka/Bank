package ru.develop.bank.service;

import ru.develop.bank.dto.NewUserDto;
import ru.develop.bank.dto.UpdatedUserDto;

public interface UserService {

    NewUserDto create(NewUserDto newUserDto);

    UpdatedUserDto addPhoneNumber(Long userId, String phoneNumber);

    UpdatedUserDto updatePhoneNumber(Long userId, String previousPhoneNumber, String newPhoneNumber);

    UpdatedUserDto deletePhoneNumber(Long userId, String phoneNumber);


}
