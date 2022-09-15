package ru.mis2022.service.dto;

import ru.mis2022.models.dto.patient.CurrentPatientDto;
import ru.mis2022.models.dto.patient.PatientDto;

import java.util.List;

public interface PatientDtoService {

    CurrentPatientDto getCurrentPatientDtoByEmail(String email);

    List<PatientDto> findPatientsByFirstNameOrLastNameOrPolisOrSnilsPattern(
            String firstName, String lastName, String polis, String snils, Integer offset, String sortBy);

}
