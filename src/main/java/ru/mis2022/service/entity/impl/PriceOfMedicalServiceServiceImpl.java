package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.entity.PriceOfMedicalService;
import ru.mis2022.models.entity.Yet;
import ru.mis2022.repositories.PriceOfMedicalServiceRepository;
import ru.mis2022.service.entity.PriceOfMedicalServiceService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PriceOfMedicalServiceServiceImpl implements PriceOfMedicalServiceService {

    private final PriceOfMedicalServiceRepository priceOfMedicalServiceRepository;

    @Override
    public PriceOfMedicalService save(PriceOfMedicalService priceOfMedicalService) {
        return priceOfMedicalServiceRepository.save(priceOfMedicalService);
    }

    @Override
    public List<PriceOfMedicalService> findAllPricesBetweenDayFromAndDayTo(LocalDate dayFrom, LocalDate dayTo) {
        return priceOfMedicalServiceRepository.findAllPricesBetweenDayFromAndDayTo(dayFrom, dayTo);
    }

    @Override
    public boolean checkIfThereAnyDateOverlap(List<PriceOfMedicalService> medicalServices) {
        medicalServices.sort(Comparator.comparing(PriceOfMedicalService::getDayTo));

        LocalDate endDate = null;

        for (PriceOfMedicalService medicalService : medicalServices) {
            if (endDate == null) {
                endDate = medicalService.getDayTo();
            } else {
                if (endDate.isAfter(medicalService.getDayFrom())) {
                    return true;
                } else {
                    endDate = medicalService.getDayTo();
                }
            }
        }

        return false;
    }

    @Override
    public boolean checkIfThereAnyActiveServiceWhileYetIsNot(List<PriceOfMedicalService> medicalServices, List<Yet> yets) {
        yets.sort(Comparator.comparing(Yet::getDayTo));
        medicalServices.sort(Comparator.comparing(PriceOfMedicalService::getDayTo));

        List<SpacesInYetDates> spacesInYetsDates = new ArrayList<>();

        LocalDate endDayYet = null;

        for (Yet yet : yets) {
            if (endDayYet != null) {
                LocalDate currentDayFromYet = yet.getDayFrom();
                if (currentDayFromYet.getDayOfYear() - endDayYet.getDayOfYear() > 1) {
                    spacesInYetsDates.add(new SpacesInYetDates(endDayYet, currentDayFromYet));
                }
            }
            endDayYet = yet.getDayTo();
        }

        for (PriceOfMedicalService medicalService : medicalServices) {
            for (SpacesInYetDates spacesInYetDate : spacesInYetsDates) {
                if (spacesInYetDate.dateFrom.isBefore(medicalService.getDayFrom()) ||
                spacesInYetDate.dateTo.isAfter(medicalService.getDayTo())) return true;
            }
        }
        return false;
    }
    private record SpacesInYetDates(LocalDate dateFrom, LocalDate dateTo) { }

    @Override
    public void deleteAll() {
        priceOfMedicalServiceRepository.deleteAll();
    }
}
