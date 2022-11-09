package ru.mis2022.models.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * MedicalService - Медицинская услуга
 * есть прейскурант всех услуг которые оказывает ЛПУ,
 * у каждой услуги есть уникальный идентификатор, например K321101
 * услугу может вкл/выкл экономист при помощи поля isDisabled
 * экономист наполняет список услуг каждого отделения из общего
 * Услуга может стоить 0 УЕТ, что означает, что она бесплатная.
 * Каждая услуга стоит определенный УЕТ в фиксированный период времени, кратный дням,
 * например:
 * с 28.07.2021 по 25.09.2021 цена 0.25
 * с 26.09.2021 по 03.12.2021 цена 0.30
 * с 04.12.2021 по null       цена null (означает - с 4.12.21 услуга не оказывается)
 * с 01.01.2022 по null       цена 0.20 (означает - с 01.01.2022 по настоящее услуга оказывается)
 */

@Entity
@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MedicalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identifier;

    private String name;

    @Column(name = "is_disabled")
    private boolean isDisabled;

    @OneToMany(mappedBy = "medicalService", fetch = FetchType.LAZY)
    private List<PriceOfMedicalService> prices;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    public MedicalService(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }
}