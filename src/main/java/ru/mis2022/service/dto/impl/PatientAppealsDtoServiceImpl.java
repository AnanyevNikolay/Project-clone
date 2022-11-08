package ru.mis2022.service.dto.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.dto.appeal.CurrentPatientAppealsDto;
import ru.mis2022.models.dto.appeal.converter.AppealDtoConverter;
import ru.mis2022.service.dto.PatientAppealsDtoService;
import ru.mis2022.service.entity.AppealService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientAppealsDtoServiceImpl implements PatientAppealsDtoService {
    private final AppealService appealService;
    private final AppealDtoConverter converter;

    @Override
    public List<CurrentPatientAppealsDto> getOpenAppealsDtoByPatientId(long patientId,
                                                                       boolean isClosed,
                                                                       long doctorId) {
        return converter.convertAppealsListToAppealsDtoList(
                appealService.getOpenAppealsDtoByPatientId(patientId, isClosed, doctorId));
    }
}
