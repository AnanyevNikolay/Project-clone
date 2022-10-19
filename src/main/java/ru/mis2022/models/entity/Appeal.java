package ru.mis2022.models.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.Set;

/**
 * Appeal - Обращение
 * <p>
 * Пациент обращается с заболеванием. И лечат заболевание.
 * При первом посещении устанавливается заболевание, которое будут лечить.
 * Обращение может лечиться как в одно посещение, так и в несколько.
 * Врач, оказывающий услуги в рамках одного обращения, может меняться(заболел), но не должна меняться специализация врача.
 * Любой из врачей, участвовавших в лечении пациента, может закрыть/открыть обращение.
 * Пока обращение не закрыто, врач может его править (только те посещения, где лечил он).
 * Когда обращение закрыто, оно может попасть в счет и пока оно в счете, его нельзя править никому.(isClosed = true)
 * Обращение может быть удалено из сформированного счета для модификации
 * Любой из врачей, участвовавших в лечении пациента может открыть обращение для устранения ошибок, если оно не в счете
 * Закрытое обращение нельзя модифицировать
 */

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id")
    private Disease disease;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appeal")
    private Set<Visit> visits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private boolean isClosed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate localDate;

    public Appeal(Patient patient, Disease disease, LocalDate localDate) {
        this.patient = patient;
        this.disease = disease;
        this.localDate = localDate;
        visits = null;
        account = null;
    }

    public Appeal(Patient patient, Disease disease, Set<Visit> visits, Account account, boolean isClosed, LocalDate localDate) {
        this.patient = patient;
        this.disease = disease;
        this.visits = visits;
        this.account = account;
        this.isClosed = isClosed;
        this.localDate = localDate;
    }
}
