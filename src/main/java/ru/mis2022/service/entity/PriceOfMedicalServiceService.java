package ru.mis2022.service.entity;

import ru.mis2022.models.dto.service.PriceOfMedicalServiceDto;
import ru.mis2022.models.entity.PriceOfMedicalService;
import ru.mis2022.models.entity.Yet;

import java.time.LocalDate;
import java.util.List;

public interface PriceOfMedicalServiceService {

    PriceOfMedicalService save(PriceOfMedicalService priceOfMedicalService);

    List<PriceOfMedicalService> findAllPricesBetweenDayFromAndDayTo(LocalDate dayFrom, LocalDate dayTo);

    boolean checkIfThereAnyDateOverlap(List<PriceOfMedicalService> medicalServices);

    boolean checkIfThereAnyActiveServiceWhileYetIsNot(List<PriceOfMedicalService> medicalServices, List<Yet> yets);

    void deleteAll();

    PriceOfMedicalService getPriceOfMedicalServiceBetweenDayFromAndDayToWithMedicalService(
            LocalDate dayFrom, LocalDate dayTo, Long medicalServiceId);

    PriceOfMedicalServiceDto setPriceByDtoWithMedicalService(PriceOfMedicalServiceDto priceOfMedicalServiceDto, Long id);
}
