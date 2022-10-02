package ru.mis2022.service.dto;

import ru.mis2022.enums.patient.PatientSortingEnum;
import ru.mis2022.models.dto.patient.CurrentPatientDto;
import ru.mis2022.models.dto.patient.PatientDto;

import java.util.List;
import java.util.Optional;

public interface PatientDtoService {

    CurrentPatientDto getCurrentPatientDtoByEmail(String email);

    Optional<List<PatientDto>> findPatientsByFirstNameOrLastNameOrPolisOrSnilsPattern(
            String firstName, String lastName, String polis,
            String snils, Integer offset, Integer size,
            PatientSortingEnum sortBy);

}
