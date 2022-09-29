package ru.mis2022.models.dto.disease.converter;

import org.springframework.stereotype.Component;
import ru.mis2022.models.dto.disease.DiseaseDto;
import ru.mis2022.models.entity.Disease;

@Component
public class DiseaseDtoConverter {

    public DiseaseDto toDiseaseDto(Disease disease) {
        return DiseaseDto.builder()
                .identifier(disease.getIdentifier())
                .name(disease.getName())
                .id(disease.getId())
                .build();
    }

    public Disease toEntity(DiseaseDto diseaseDto) {
        return Disease.builder()
                .id(diseaseDto.id())
                .name(diseaseDto.name())
                .identifier(diseaseDto.identifier())
                .build();
    }
}
