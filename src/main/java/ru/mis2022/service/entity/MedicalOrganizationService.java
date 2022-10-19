package ru.mis2022.service.entity;

import ru.mis2022.models.entity.MedicalOrganization;

import java.util.List;


public interface MedicalOrganizationService {
    MedicalOrganization save(MedicalOrganization medicalOrganization);

    List<MedicalOrganization> findAll();

    void delete(Long id);

    boolean isExist(Long medicalOrganizationId);

    boolean isExistById(Long id);

    boolean isExistByName(String name);
}
