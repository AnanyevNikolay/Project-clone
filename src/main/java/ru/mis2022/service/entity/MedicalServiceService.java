package ru.mis2022.service.entity;

import ru.mis2022.models.dto.service.MedicalServiceDto;
import ru.mis2022.models.entity.MedicalService;

import java.util.Set;

public interface MedicalServiceService {

    MedicalService save(MedicalService medicalService);

    boolean isExistByIdentifier(String identifier);

    boolean isExistByName(String name);

    void deleteAll();

    MedicalService getMedicalServiceById(Long id);

    MedicalServiceDto changeMedicalServiceIsDisabled(MedicalService medicalService, boolean isDisabled);

    Set<MedicalService> getMedicalServicesByIdsAndDepartment(Set<Long> ids, long departmentId);
}
