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

    //Возможно стоит добавить поле PASSWORD???

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "login", nullable = false, unique = true)
    String login;


    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "birthday", nullable = false)
    LocalDate birthday;

    @Column(name = "account_balance", nullable = false)
    @Min(0)
    Long accountBalance;

    @OneToMany
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    Set<PhoneNumber> phoneNumbers;

    @OneToMany
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    Set<Email> emails;

//    public void runTask() {
//
//        Calendar calendar = Calendar.getInstance();
//        LocalDateTime now = LocalDateTime.now();
//        calendar.set(Calendar.DAY_OF_WEEK, now.getDayOfMonth());
//        calendar.set(Calendar.HOUR_OF_DAY, now.getHour());
//        calendar.set(Calendar.MINUTE, now.getMinute());
//        calendar.set(Calendar.SECOND, now.getSecond());
//
//        Timer time = new Timer(); // Instantiate Timer Object
//
//        // Start running the task on Monday at 15:40:00, period is set to 8 hours
//        // if you want to run the task immediately, set the 2nd parameter to 0
//        Long balance = this.getAccountBalance();
//        while (this.getAccountBalance() <= balance*2.07) {
//            time.schedule(new CustomTask(this), calendar.getTime(), 1000);
//        }
//
//    }
}
