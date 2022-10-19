package ru.mis2022.models.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
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
 * Visit Посещение
 * Каждый раз, когда пациент приходит на прием в рамках обращения, врач оказывает услуги.
 */

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private LocalDate dayOfVisit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appeal_id")
    private Appeal appeal;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "visit")
    private Set<MedicalService> medicalServices;

    public Visit(LocalDate dayOfVisit, Doctor doctor, Appeal appeal, Set<MedicalService> medicalServices) {
        this.dayOfVisit = dayOfVisit;
        this.doctor = doctor;
        this.appeal = appeal;
        this.medicalServices = medicalServices;
    }
}
