package ru.mis2022.models.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Department - Отделение
 * <p>
 * От типа отделения зависят услуги оказываемые в нем.
 * В нашем ЛПУ есть отделения: терапевтическое, хирургическое, ортодонтическое
 * в каждом отделении есть:
 * список врачей одной специальности, оказывающие одинаковые услуги,
 * заведующий отделением, который тоже является врачом этого отделения,
 * список заболеваний которые лечат в отделении
 * список услуг оказываеммых по ОМС (некоторые услуги могут оказываться в разных отделениях)
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

//    private List<Doctor> doctors;

//    private List<Disease> diseases;

//    private List<MedicalService> medicalServices;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_organization_id")
    private MedicalOrganization medicalOrganization;


}
