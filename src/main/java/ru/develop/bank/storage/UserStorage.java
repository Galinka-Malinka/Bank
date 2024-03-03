package ru.develop.bank.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.develop.bank.model.User;

public interface UserStorage extends JpaRepository<User, Long> {
    Boolean existsByLogin(String login);
}
