package ru.develop.bank.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.develop.bank.dto.UserDto;
import ru.develop.bank.dto.UpdatedUserDto;
import ru.develop.bank.model.Email;
import ru.develop.bank.model.PhoneNumber;
import ru.develop.bank.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User toUser(UserDto userDto) {
        return User.builder()
                .login(userDto.getLogin())
                .name(userDto.getName())
                .birthday(userDto.getBirthday())
                .accountBalance(userDto.getAccountBalance())
                .build();
    }

    public static UserDto toUserDto(User user, List<String> phoneNumbers, List<String> emails) {
        return UserDto.builder()
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
        return UpdatedUserDto.builder()
                .id(userId)
                .phoneNumbers(phoneNumbers.stream().map(PhoneNumber::getPhoneNumber).toList())
                .build();

    }

    public static UpdatedUserDto toUpdatedUserDtoByEmail(Long userId, List<Email> emails) {

        List<String> emailsList = new ArrayList<>();
        for (Email email : emails) {
            emailsList.add(email.getEmail());
        }
        return UpdatedUserDto.builder()
                .id(userId)
                .emails(emailsList)
                .build();

    }
}
