package ru.mis2022.models.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import java.time.LocalDate;

/**
 * Economist - Экономист
 * Должен делать кучу всяких выгрузок и запросов в БД для отслеживания всевозможных
 * статистических форм в разрезе:
 * врач/отделение/специальность/учреждение/взрослый/детский...
 * Должен наполнять все справочники, формировать счета в МЗ
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
public class Economist extends User {

    public Economist(String email, String password, String firstName, String lastName, @Nullable String surname,
                     LocalDate birthday, Role role) {
        super(email, password, firstName, lastName, surname, birthday, role);
    }
}
