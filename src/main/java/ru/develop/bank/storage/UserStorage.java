package ru.develop.bank.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.develop.bank.model.User;

import java.util.Optional;

@Repository
public interface UserStorage extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    Boolean existsByLogin(String login);

    Optional<User> findByLogin(String login);
}
