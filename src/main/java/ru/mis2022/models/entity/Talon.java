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
import java.time.LocalDateTime;
/**
 * Talon - Талон на прием к врачу
 * на каждый день их создается 8
 * талон удаляется при явке пациента, и у пациента создается обращение/посещение
 */


@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Talon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private Doctor doctor;

    public Talon(LocalDateTime time, Doctor doctor, Patient patient) {
        this.time = time;
        this.doctor = doctor;
        this.patient = patient;
    }
    public Talon(LocalDateTime time, Doctor doctor) {
        this.time = time;
        this.doctor = doctor;
    }
}
