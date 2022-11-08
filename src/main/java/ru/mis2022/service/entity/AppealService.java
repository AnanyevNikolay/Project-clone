package ru.mis2022.service.entity;


import ru.mis2022.models.entity.Appeal;

import java.util.List;
import java.util.Optional;

public interface AppealService {

    Appeal save(Appeal appeal);

    Optional<List<Appeal>> getAppealsDtoByPatientId(long patientId);

    List<Appeal> getOpenAppealsDtoByPatientId(long patientId, boolean isClosed, long doctorId);

    void deleteAll();

    Appeal findAppealById(Long id);
}
