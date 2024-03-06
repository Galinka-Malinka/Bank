package ru.develop.bank.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.develop.bank.model.User;

public interface UserStorage extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    Boolean existsByLogin(String login);
}
