package ru.develop.bank.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.develop.bank.dto.NewUserDto;
import ru.develop.bank.model.User;

import java.util.List;

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
}
