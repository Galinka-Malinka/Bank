package ru.develop.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.develop.bank.model.User;
import ru.develop.bank.storage.UserStorage;

import java.util.List;

@Service
@EnableAsync
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserStorage userStorage;

    @Override
    @Async
    @Scheduled(fixedDelay = 60 * 1000)
    public void accrueInterest() {
        List<User> users = userStorage.findAll();
        for (User user : users) {
            Long balance = user.getAccountBalance();
            if (balance < user.getLimitOfInterestAccrual()) {
                user.setAccountBalance(balance + balance * 5 / 100);
                userStorage.save(user);
            }
        }
    }
}
