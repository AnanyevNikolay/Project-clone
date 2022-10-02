package ru.mis2022.models.entity;


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
import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class PriceOfMedicalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double yet;

    private LocalDate dayFrom;

    private LocalDate dayTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_service_id")
    private MedicalService medicalService;

    public PriceOfMedicalService(Double yet, LocalDate dayFrom, LocalDate dayTo, MedicalService medicalService) {
        this.yet = yet;
        this.dayFrom = dayFrom;
        this.dayTo = dayTo;
        this.medicalService = medicalService;
    }
}
