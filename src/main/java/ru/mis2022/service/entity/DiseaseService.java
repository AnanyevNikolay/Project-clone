package ru.mis2022.service.entity;

import ru.mis2022.models.dto.disease.DiseaseDto;
import ru.mis2022.models.entity.Disease;

import java.util.List;

public interface DiseaseService {
    List<DiseaseDto> findAllDiseaseDto();
    boolean isExistByIdentifier(String identifier);
    boolean isExistById(Long id);
    void deleteById (Long id);
    Disease save(Disease disease);
    Disease findDiseaseById(Long id);
    void deleteAll();
    Disease findDiseaseByIdAndChangeDisabled(Long diseaseId);
}
