package ru.mis2022.models.dto.visit.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mis2022.models.dto.service.converter.MedicalServiceDtoConverter;
import ru.mis2022.models.dto.visit.VisitDto;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;

@Component
@AllArgsConstructor
public class VisitDtoConverter {

    private final MedicalServiceDtoConverter medicalServiceDtoConverter;

    public List<VisitDto> visitsToVisitDtoConverter(Set<Visit> visits) {
        if (visits == null) {
            return Collections.emptyList();
        }
        List<VisitDto> visitDtoList = new ArrayList<>();
        for (Visit visit : visits) {
            Doctor doctor = visit.getDoctor();
            visitDtoList.add(new VisitDto(
                    visit.getId(),
                    visit.getDayOfVisit().format(DATE_FORMATTER),
                    doctor.getId(),
                    doctor.getFirstName() + " " + doctor.getLastName() + " " + doctor.getSurname(),
                    medicalServiceDtoConverter.toMedicalServicesDtoWithoutIdentifier(visit.getMedicalServices())
            ));
        }
        visitDtoList.sort(Comparator.comparingLong(VisitDto::visitId));
        return visitDtoList;
    }

}
