package ru.mis2022.models.dto.service.converter;

import org.springframework.stereotype.Component;
import ru.mis2022.models.dto.service.MedicalServiceDto;
import ru.mis2022.models.entity.MedicalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Component
public class MedicalServiceDtoConverter {

    public MedicalServiceDto toMedicalServiceDto(MedicalService medicalService) {
        return MedicalServiceDto.builder()
                .identifier(medicalService.getIdentifier())
                .name(medicalService.getName())
                .id(medicalService.getId())
                .isDisabled(medicalService.isDisabled())
                .build();
    }

    public List<MedicalServiceDto> toMedicalServicesDtoWithoutIdentifier(Set<MedicalService> medicalServices) {
        List<MedicalServiceDto> medicalServiceDtoList = new ArrayList<>();
        if (medicalServices == null) {
            return Collections.emptyList();
        }
        for (MedicalService medicalService : medicalServices) {
            medicalServiceDtoList.add(MedicalServiceDto.builder()
                    .name(medicalService.getName())
                    .identifier(medicalService.getIdentifier())
                    .id(medicalService.getId())
                    .build());
        }
        medicalServiceDtoList.sort(Comparator.comparingLong(MedicalServiceDto::id));
        return medicalServiceDtoList;
    }
}
