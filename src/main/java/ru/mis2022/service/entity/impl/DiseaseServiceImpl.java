package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.dto.disease.DiseaseDto;
import ru.mis2022.models.dto.disease.converter.DiseaseDtoConverter;
import ru.mis2022.models.entity.Disease;
import ru.mis2022.repositories.DiseaseRepository;
import ru.mis2022.service.entity.DiseaseService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DiseaseServiceImpl implements DiseaseService {

    private final DiseaseRepository diseaseRepository;
    private final DiseaseDtoConverter diseaseDtoConverter;

    @Override
    public List<DiseaseDto> findAllDiseaseDto() {
        return diseaseRepository.findAllDiseaseDto();
    }

    @Override
    public boolean isExistByIdentifier(String identifier) {
        return diseaseRepository.existsByIdentifier(identifier);
    }

    @Override
    public boolean isExistById(Long id) {
        return diseaseRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        diseaseRepository.deleteById(id);
    }

    @Override
    public Disease save(Disease disease) {
        return diseaseRepository.save(disease);
    }

    @Override
    public Disease findDiseaseById(Long id) {
        return diseaseRepository.findDiseaseById(id);
    }

    @Override
    public void deleteAll() {
        diseaseRepository.deleteAll();
    }

    // Заблокировать/разблокировать заболевание!
    @Override
    public DiseaseDto changeDisabledDisease(Disease disease, boolean disabled) {
        disease.setDisabled(disabled);
        return diseaseDtoConverter.toDiseaseDto(save(disease));
    }

    @Override
    public boolean existsDiseaseByDiseaseIdAndDoctorId(long diseaseId, long doctorId) {
        return diseaseRepository.existsDiseaseByDiseaseIdAndDoctorId(diseaseId, doctorId);
    }
}
