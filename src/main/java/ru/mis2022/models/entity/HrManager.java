package ru.mis2022.models.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import java.time.LocalDate;

/**
 * HrManager - Кадровик
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
public class HrManager extends User {
    public HrManager(String email, String password, String firstName, String lastName, @Nullable String surname,
                     LocalDate birthday, Role role) {
        super(email, password, firstName, lastName, surname, birthday, role);
    }
}
