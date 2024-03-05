package ru.develop.bank.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.develop.bank.model.Email;

import java.util.List;

public interface EmailStorage extends JpaRepository<Email, Integer> {
    Boolean existsByEmail(String email);

    List<Email> findAllByUserId(Long userId);

    Email findByEmail(String email);
}
