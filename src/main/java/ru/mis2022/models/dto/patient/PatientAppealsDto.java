package ru.mis2022.models.dto.patient;

import ru.mis2022.models.dto.appeal.AppealDto;

import java.util.List;

public record PatientAppealsDto (
    long patientId,
    String patientFullName,
    List<AppealDto> appealDtoList
) {}
