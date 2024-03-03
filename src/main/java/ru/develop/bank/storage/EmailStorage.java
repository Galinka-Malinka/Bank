package ru.develop.bank.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.develop.bank.model.Email;

public interface EmailStorage extends JpaRepository<Email, Integer> {
    Boolean existsByEmail(String email);
}
