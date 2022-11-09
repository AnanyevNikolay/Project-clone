package ru.mis2022.service.entity.impl;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.mis2022.models.dto.service.MedicalServiceDto;
import ru.mis2022.models.dto.service.converter.MedicalServiceDtoConverter;
import ru.mis2022.models.entity.MedicalService;
import ru.mis2022.repositories.MedicalServiceRepository;
import ru.mis2022.service.entity.MedicalServiceService;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class MedicalServiceServiceImpl implements MedicalServiceService {

    private final MedicalServiceRepository medicalServiceRepository;
    private final MedicalServiceDtoConverter medicalServiceDtoConverter;

    @Override
    public MedicalService save(MedicalService medicalService) {
        return medicalServiceRepository.save(medicalService);
    }

    @Override
    public boolean isExistByIdentifier(String identifier) {
        return medicalServiceRepository.existsByIdentifier(identifier);
    }

    @Override
    public boolean isExistByName(String name) {
        return medicalServiceRepository.existsByName(name);
    }

    @Override
    public void deleteAll() {
        medicalServiceRepository.deleteAll();
    }

    @Override
    public MedicalService getMedicalServiceById(Long id) {
        return medicalServiceRepository.getMedicalServiceById(id);
    }

    @Override
    public MedicalServiceDto changeMedicalServiceIsDisabled(MedicalService medicalService, boolean isDisabled) {
        medicalService.setDisabled(isDisabled);
        return medicalServiceDtoConverter.toMedicalServiceDto(save(medicalService));
    }
}
