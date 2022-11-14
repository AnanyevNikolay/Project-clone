package ru.mis2022.models.dto.appeal.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mis2022.models.dto.appeal.AppealDto;
import ru.mis2022.models.dto.appeal.CurrentPatientAppealsDto;
import ru.mis2022.models.dto.visit.converter.VisitDtoConverter;
import ru.mis2022.models.entity.Appeal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
@AllArgsConstructor
public class AppealDtoConverter {

    private final VisitDtoConverter visitDtoConverter;

    public List<AppealDto> toListAppealDto(List<Appeal> appealList) {
        List<AppealDto> appealDtoList = new ArrayList<>();
        appealList.forEach(appeal -> appealDtoList.add(toAppealDto(appeal)));
        return appealDtoList;
    }

    public List<CurrentPatientAppealsDto> convertAppealsListToAppealsDtoList(List<Appeal> appeals) {
        if (appeals == null) {
            return Collections.emptyList();
        }
        List<CurrentPatientAppealsDto> appealDtoList = new ArrayList<>();
        for (Appeal appeal : appeals) {
            appealDtoList.add(new CurrentPatientAppealsDto(
                    appeal.getId(),
                    appeal.getDisease().getName(),
                    appeal.isClosed(),
                    visitDtoConverter.visitsToVisitDtoConverter(appeal.getVisits())
            ));
        }
        appealDtoList.sort(Comparator.comparingLong(CurrentPatientAppealsDto::appealId));
        return appealDtoList;
    }

    public AppealDto toAppealDto(Appeal appeal) {
        return AppealDto.builder()
                .id(appeal.getId())
                .account(appeal.getAccount())
                .visits(appeal.getVisits())
                .localDate(appeal.getLocalDate())
                .isClosed(appeal.isClosed())
                .patientId(appeal.getPatient().getId())
                .diseaseId(appeal.getDisease().getId())
                .build();
    }

}
