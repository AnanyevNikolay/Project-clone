package ru.mis2022.service.entity;

import ru.mis2022.models.dto.visit.VisitDto;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.models.entity.Visit;

import java.util.List;
import java.util.Optional;

public interface VisitService {

    Visit save(Visit visit);

    VisitDto createVisitByTalonIdAndAppealId(Talon talon, Appeal appeal);
}
