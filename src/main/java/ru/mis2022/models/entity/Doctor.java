package ru.mis2022.models.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.util.List;

/**
 * Doctor - Врач
 * состоит в отделении, оказывает услуги пациентам
 * создает свое расписание, которое продляется на 28 дней вперед
 * Каждый рабочий день выдает 8 талонов
 * может записывать к себе пациентов на 28 дней вперед
 * <p>
 * Заведующий отделением
 * это врач у которого есть функционал смотреть статистику в рамках его отделения
 * оказывает медицинские услуги как остальные врачи
 * <p>
 * Главный врач
 * это врач у которого есть функционал смотреть статистику в рамках ЛПУ/отделений
 * оказывает медицинские услуги как остальные врачи
 * на него не создается расписание
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Doctor extends User {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_history_id", referencedColumnName = "id")
    private PersonalHistory personalHistory;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "doctor")
    private List<Talon> talons;


    public Doctor(String email, String password, String firstName, String lastName, @Nullable String surname,
                  LocalDate birthday, Role role, Department department) {
        super(email, password, firstName, lastName, surname, birthday, role);
        this.department = department;
    }

    public Doctor(String firstName, String email, Role role, PersonalHistory personalHistory) {
        super(firstName, email, role, personalHistory);
    }
}
