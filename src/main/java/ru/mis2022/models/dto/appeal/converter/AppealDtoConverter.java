package ru.mis2022.models.dto.appeal.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mis2022.models.dto.appeal.AppealDto;
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

    public List<AppealDto> convertAppealsListToAppealsDtoList(List<Appeal> appeals) {
        if (appeals == null) {
            return Collections.emptyList();
        }
        List<AppealDto> appealDtoList = new ArrayList<>();
        for (Appeal appeal : appeals) {
            appealDtoList.add(new AppealDto(
                    appeal.getId(),
                    appeal.getDisease().getName(),
                    appeal.isClosed(),
                    visitDtoConverter.visitsToVisitDtoConverter(appeal.getVisits())
            ));
        }
        appealDtoList.sort(Comparator.comparingLong(AppealDto::appealId));
        return appealDtoList;
    }

}
