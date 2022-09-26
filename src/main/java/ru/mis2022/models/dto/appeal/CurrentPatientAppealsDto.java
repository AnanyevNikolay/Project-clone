package ru.mis2022.models.dto.appeal;

import ru.mis2022.models.dto.visit.VisitDto;

import java.util.List;

public record CurrentPatientAppealsDto(
    long appealId,
    String diseaseName,
    boolean status,
    List<VisitDto> visitDtoList
) {}


