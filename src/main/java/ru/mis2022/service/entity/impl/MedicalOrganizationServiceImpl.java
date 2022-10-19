package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.entity.MedicalOrganization;
import ru.mis2022.repositories.MedicalOrganizationRepository;
import ru.mis2022.service.entity.MedicalOrganizationService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MedicalOrganizationServiceImpl implements MedicalOrganizationService {

    private final MedicalOrganizationRepository medicalOrganizationRepository;

    @Override
    public MedicalOrganization save(MedicalOrganization medicalOrganization) {
        return medicalOrganizationRepository.save(medicalOrganization);
    }

    @Override
    public List<MedicalOrganization> findAll() {
        return medicalOrganizationRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        medicalOrganizationRepository.deleteById(id);
    }

    @Override
    public boolean isExist(Long medicalOrganizationId) {
        return medicalOrganizationRepository.existsById(medicalOrganizationId);
    }

    @Override
    public boolean isExistById(Long id) {
        return medicalOrganizationRepository.existsById(id);
    }

    @Override
    public boolean isExistByName(String name) {
        return medicalOrganizationRepository.existsByName(name);
    }
}
