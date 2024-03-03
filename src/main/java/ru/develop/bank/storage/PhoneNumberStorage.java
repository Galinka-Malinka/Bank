package ru.develop.bank.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.develop.bank.model.PhoneNumber;

public interface PhoneNumberStorage extends JpaRepository<PhoneNumber, Integer> {
    Boolean existsByPhoneNumber(String phoneNumber);
}