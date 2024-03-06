package ru.develop.bank.service;

import lombok.AllArgsConstructor;
import ru.develop.bank.model.User;

import java.util.TimerTask;

@AllArgsConstructor
public class CustomTask extends TimerTask {
    private final User user;

    public void run() {
        try {
            Long actualBalance = user.getAccountBalance();
            user.setAccountBalance(actualBalance * 105 / 100);
        } catch (Exception ex) {
            System.out.println("error running thread " + ex.getMessage());
        }
    }
}
