package ru.mis2022.service.dto.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.mis2022.models.dto.patient.CurrentPatientDto;
import ru.mis2022.models.dto.patient.PatientDto;
import ru.mis2022.repositories.PatientRepository;
import ru.mis2022.service.dto.PatientDtoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientDtoServiceImpl implements PatientDtoService {
    private final PatientRepository patientRepository;
    @Override
    public CurrentPatientDto getCurrentPatientDtoByEmail(String email) {
        return patientRepository.getCurrentPatientDtoByEmail(email);
    }

    @Override
    public List<PatientDto> findPatientsByFirstNameOrLastNameOrPolisOrSnilsPattern(
            String firstName, String lastName, String polis, String snils, Integer offset) {
        Pageable pageable = PageRequest.of(offset, 10);
        return patientRepository.findPatientsByFirstNameOrLastNameOrPolisOrSnilsPattern(
                firstName, lastName, polis, snils, pageable);
    }

}
