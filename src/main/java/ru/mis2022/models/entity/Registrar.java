package ru.mis2022.models.entity;


import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import java.time.LocalDate;

/**
 * Registrar - Медрегистратор
 * регистрирует пациентов в системе
 * блокирует пациентов в системе
 * видит расписание всех врачей,
 * может записать/удалить пациента у любого врача
 * может переносить пациентов между врачами одного отделения
 * может выставлять врачам больничный
 */


@Entity
@AllArgsConstructor
@SuperBuilder
public class Registrar extends User {
    public Registrar(String email, String password, String firstName, String lastName, @Nullable String surname,
                   LocalDate birthday, Role role) {
        super(email, password, firstName, lastName, surname, birthday, role);
    }
}
