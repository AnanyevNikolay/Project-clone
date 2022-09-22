package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Visit;

import java.util.List;
import java.util.Optional;

public interface VisitService {

    Visit save(Visit visit);

    Optional<List<Visit>> getVisitsOfCurrentPatientById(long id);
}
