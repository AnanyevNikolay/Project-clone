package ru.mis2022.models.dto.service.converter;

import org.springframework.stereotype.Component;
import ru.mis2022.models.dto.service.PriceOfMedicalServiceDto;
import ru.mis2022.models.entity.MedicalService;
import ru.mis2022.models.entity.PriceOfMedicalService;

import java.math.RoundingMode;

@Component
public class PriceMedicalServiceDtoConverter {

    public PriceOfMedicalService dtoToEntity(PriceOfMedicalServiceDto priceOfMedicalServiceDto, MedicalService medicalService) {
        return new PriceOfMedicalService(
                priceOfMedicalServiceDto.price().setScale(2, RoundingMode.HALF_UP),
                priceOfMedicalServiceDto.dayFrom(),
                priceOfMedicalServiceDto.dayTo(),
                medicalService
        );
    }

    public PriceOfMedicalServiceDto entityToDto(PriceOfMedicalService priceOfMedicalService) {
        return new PriceOfMedicalServiceDto(
                priceOfMedicalService.getYet(),
                priceOfMedicalService.getDayFrom(),
                priceOfMedicalService.getDayTo()
        );
    }

}
