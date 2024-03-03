package ru.develop.bank.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "birthday", nullable = false)
    LocalDate birthday;

    @Column(name = "account_balance", nullable = false)
    @Min(0)
    Double accountBalance;

    @OneToMany
    @JoinColumn(name = "user_id")
    Set<PhoneNumber> phoneNumbers;

    @OneToMany
    @JoinColumn(name = "user_id")
    Set<Email> emails;
}
