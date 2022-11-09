package ru.mis2022.models.dto.service;

import lombok.Builder;

@Builder
public record MedicalServiceDto(Long id,String identifier,String name, boolean isDisabled) {}