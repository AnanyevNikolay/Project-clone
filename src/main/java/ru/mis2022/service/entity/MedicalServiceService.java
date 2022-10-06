package ru.mis2022.service.entity;

import ru.mis2022.models.entity.MedicalService;

import java.util.List;
import java.util.Optional;

public interface MedicalServiceService {

    MedicalService save(MedicalService medicalService);

    boolean isExistByIdentifier(String identifier);

    boolean isExistByName(String name);

    Optional<List<MedicalService>> getMedicalServicesDtoVisitedByCurrentPatientWithId(long id);

    void deleteAll();

    MedicalService getMedicalServiceById(Long id);

}
