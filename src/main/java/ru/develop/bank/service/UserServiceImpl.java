package ru.develop.bank.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.develop.bank.dto.UpdatedUserDto;
import ru.develop.bank.dto.UserAfterTransfer;
import ru.develop.bank.dto.UserDto;
import ru.develop.bank.exception.AlreadyExistsException;
import ru.develop.bank.exception.ConflictException;
import ru.develop.bank.exception.NotFoundException;
import ru.develop.bank.exception.ValidationException;
import ru.develop.bank.mapper.UserMapper;
import ru.develop.bank.model.Email;
import ru.develop.bank.model.PhoneNumber;
import ru.develop.bank.model.QUser;
import ru.develop.bank.model.User;
import ru.develop.bank.storage.EmailStorage;
import ru.develop.bank.storage.PhoneNumberStorage;
import ru.develop.bank.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
//@EnableAsync
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final PhoneNumberStorage phoneNumberStorage;
    private final EmailStorage emailStorage;

    @Override
    public UserDto create(UserDto userDto) {

        List<String> phoneNumbers = userDto.getPhoneNumbers().stream().distinct().toList();
        for (String phoneNumber : phoneNumbers) {
            checkNewPhoneNumber(phoneNumber);
        }

        List<String> emails = userDto.getEmails().stream().distinct().toList();
        for (String email : emails) {
            if (emailStorage.existsByEmail(email)) {
                throw new AlreadyExistsException("Email " + email + " уже используется.");
            }
        }

        if (userStorage.existsByLogin(userDto.getLogin())) {
            throw new AlreadyExistsException("Пользователь с логином " + userDto.getLogin() + " уже существует.");
        }

        User user = userStorage.save(UserMapper.toUser(userDto));
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

    //    @Async
//    @Scheduled
    public void addInterest(User user) {
        Long firstBalance = user.getAccountBalance();
        Thread accrualOfInterest = new Thread(new Runnable() {
            @Override
            public void run() {
                while (user.getAccountBalance() < (firstBalance * 2.07)) {
                    try {
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!1 try !!!!!!!!!!!!!!!");
                        Thread.sleep(60 * 1000);
                        System.out.println("@@@@@@@@@@@@@@@@@@@222 after sleep @@@@@@@@@@@@@@@@@@@@@@2");
                        Long actualBalance = user.getAccountBalance();
                        user.setAccountBalance(actualBalance + actualBalance * 5 / 100);
                        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$" + user.getAccountBalance());
                        userStorage.save(user);
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        });
        accrualOfInterest.start();
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

    @Override
    public List<UserDto> searchUsers(String name, LocalDate birthday, String phoneNumber, String email,
                                     Integer from, Integer size, String sort) {
        List<UserDto> userDtoList = new ArrayList<>();
        int page = from / size;
        Pageable sortedAndPageable;

        if (sort != null && sort.equals("asc")) {
            sortedAndPageable = PageRequest.of(page, size, Sort.by("id").ascending());
        } else if (sort != null && sort.equals("desc")) {
            sortedAndPageable = PageRequest.of(page, size, Sort.by("id").descending());
        } else {
            sortedAndPageable = PageRequest.of(page, size);
        }

        QUser user = QUser.user;
        BooleanBuilder where = new BooleanBuilder();

        if (name != null) {
            where.and(user.name.like(name));
        }
        if (birthday != null) {
            where.and(user.birthday.after(birthday));
        }
        if (phoneNumber != null) {
            where.and(user.phoneNumbers.any().phoneNumber.contains(phoneNumber));
        }
        if (email != null) {
            where.and(user.emails.any().email.contains(email));
        }

        Iterable<User> foundUsers = userStorage.findAll(where, sortedAndPageable);

        for (User foundUser : foundUsers) {
            List<String> phoneNumbers = phoneNumberStorage.findAllByUserId(foundUser.getId())
                    .stream().map(PhoneNumber::getPhoneNumber).toList();
            List<String> emails = emailStorage.findAllByUserId(foundUser.getId())
                    .stream().map(Email::getEmail).toList();
            userDtoList.add(UserMapper.toUserDto(foundUser, phoneNumbers, emails));
        }
        return userDtoList;
    }

    @Override
    @Transactional
    public UserAfterTransfer transferOfMoneyToTheRecipient(Long userId, Long recipientId, Long sum) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует."));
        Long usersBalance = user.getAccountBalance();
        if (sum > usersBalance) {
            throw new ConflictException("У пользователя с id " + userId +
                    " не хватает средств для перевода в размере " + sum);
        }
        User recipient = userStorage.findById(recipientId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id " + recipientId + " не существует."));
        Long recipientsBalance = recipient.getAccountBalance();

        Long usersNewBalance = usersBalance - sum;
        user.setAccountBalance(usersNewBalance);
        recipient.setAccountBalance(recipientsBalance + sum);
        userStorage.save(user);
        userStorage.save(recipient);
        return UserAfterTransfer.builder().userId(userId).accountBalance(usersNewBalance).build();
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


