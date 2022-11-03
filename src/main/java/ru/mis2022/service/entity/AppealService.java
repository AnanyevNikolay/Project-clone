package ru.mis2022.service.entity;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mis2022.models.entity.Appeal;

import java.util.List;
import java.util.Optional;

public interface AppealService {

    Appeal save(Appeal appeal);

    Optional<List<Appeal>> getAppealsDtoByPatientId(long patientId);
    void deleteAll();

    Appeal findAppealById(Long id);
}
