package ru.mis2022.service.dto.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.dto.disease.DiseaseDto;
import ru.mis2022.repositories.DiseaseRepository;
import ru.mis2022.service.dto.DiseaseDtoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiseaseDtoServiceImpl implements DiseaseDtoService {

    private final DiseaseRepository diseaseRepository;

    @Override
    public List<DiseaseDto> findAllDiseaseDto() {
        return diseaseRepository.findAllDiseaseDto();
    }

    @Override
    public List<DiseaseDto> findDiseaseByDepartmentDoctors(Long docId, boolean disabled) {
        return diseaseRepository.findDiseaseByDepartmentDoctors(docId, disabled);
    }

    @Override
    public List<DiseaseDto> findDiseaseWithoutDepartment() {
        return diseaseRepository.findDiseaseWithoutDepartment();
    }
}
