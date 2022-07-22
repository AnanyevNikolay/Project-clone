package ru.mis2022.models.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.Set;

/**
 * PersonalHistory - Личные данные
 */

@Entity
@Setter
@Getter
@NoArgsConstructor
public class PersonalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Attestation> attestations;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Diploma> diplomas;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Vacation> vacations;

    private LocalDate dateOfEmployment;

    private LocalDate dateOfDismissal;
}
