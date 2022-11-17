package ru.mis2022.service.dto;

import ru.mis2022.models.dto.disease.DiseaseDto;

import java.util.List;

public interface DiseaseDtoService {

    List<DiseaseDto> findAllDiseaseDto();

    List<DiseaseDto> findDiseaseByDepartmentDoctors(Long docId, boolean disabled);

    List<DiseaseDto> findDiseaseWithoutDepartment();
}
