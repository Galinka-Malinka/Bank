package ru.develop.bank.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.develop.bank.model.PhoneNumber;

import java.util.List;

public interface PhoneNumberStorage extends JpaRepository<PhoneNumber, Integer> {
    Boolean existsByPhoneNumber(String phoneNumber);

    List<PhoneNumber> findAllByUserId(Long userId);

    PhoneNumber findByPhoneNumber(String phoneNumber);

    @Transactional
    @Modifying
    @Query("update PhoneNumber as p set p.phoneNumber = ?1 where p.id = ?2")
    void setPhoneNumber(String newPhoneNumber, Integer phoneNumberId);

}
