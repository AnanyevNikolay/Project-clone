package ru.mis2022.service.dto;

import ru.mis2022.models.dto.appeal.CurrentPatientAppealsDto;

import java.util.List;

public interface PatientAppealsDtoService {

    List<CurrentPatientAppealsDto> getOpenAppealsDtoByPatientId(long patientId,
                                                                boolean isClosed,
                                                                long doctorId);

}
