package ru.mis2022.models.dto.patient;

import java.util.List;

public record PatientAppealsDto (
        long patientId,
        String patientFullName,
        List<ru.mis2022.models.dto.appeal.CurrentPatientAppealsDto> appealDtoList
) {}
