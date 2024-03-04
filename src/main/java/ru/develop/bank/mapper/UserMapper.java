package ru.develop.bank.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.develop.bank.dto.NewUserDto;
import ru.develop.bank.dto.UpdatedUserDto;
import ru.develop.bank.model.Email;
import ru.develop.bank.model.PhoneNumber;
import ru.develop.bank.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User toUser(NewUserDto newUserDto) {
        return User.builder()
                .login(newUserDto.getLogin())
                .name(newUserDto.getName())
                .birthday(newUserDto.getBirthday())
                .accountBalance(newUserDto.getAccountBalance())
                .build();
    }

    public static NewUserDto toUserDto(User user, List<String> phoneNumbers, List<String> emails) {
        return NewUserDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .accountBalance(user.getAccountBalance())
                .phoneNumbers(phoneNumbers.stream().toList())
                .emails(emails.stream().toList())
                .build();
    }

    public static UpdatedUserDto toUpdatedUserDtoByPhone(Long userId, List<PhoneNumber> phoneNumbers) {
//            List<String> phoneNumbersList = new ArrayList<>();
//            for (PhoneNumber phoneNumber: phoneNumbers) {
//                phoneNumbersList.add(phoneNumber.getPhoneNumber());
//            }
//
//            List<String> list = phoneNumbers.stream().map(PhoneNumber::getPhoneNumber).toList();
            return UpdatedUserDto.builder()
                    .id(userId)
                    .phoneNumbers(phoneNumbers.stream().map(PhoneNumber::getPhoneNumber).toList())
                    .build();

    }

    public static UpdatedUserDto toUpdatedUserDtoByEmail(Long userId, List<Email> emails) {

            List<String> emailsList = new ArrayList<>();
            for (Email email: emails) {
                emailsList.add(email.getEmail());
            }
            return UpdatedUserDto.builder()
                    .id(userId)
                    .emails(emailsList)
                    .build();

    }

}
