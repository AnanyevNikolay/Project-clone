package ru.mis2022.models.dto.disease;

import lombok.Builder;
import ru.mis2022.models.entity.Department;

@Builder
public record DiseaseDto(Long id, String identifier, String name, boolean disabled) {}
