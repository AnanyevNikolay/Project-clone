package ru.mis2022.models.dto.visit;

import ru.mis2022.models.dto.service.MedicalServiceDto;

import java.util.List;

public record VisitDto(
        long visitId,
        String dateOfVisit,
        long doctorId,
        String doctorFullName,
        List<MedicalServiceDto> medicalServiceDtoList
) {}
