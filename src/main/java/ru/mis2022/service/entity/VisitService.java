package ru.mis2022.service.entity;

import ru.mis2022.models.dto.visit.VisitDto;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.MedicalService;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.models.entity.Visit;

import java.util.Set;

public interface VisitService {

    Visit save(Visit visit);

    VisitDto createVisitByTalonIdAndAppealId(Talon talon, Appeal appeal);

    Visit findByIdAndDoctorIdWithAppeal(long visitId, long doctorId);

    VisitDto addMedicalServicesInVisit(Visit visit, Set<MedicalService> medicalServices, boolean closeAppeal);
}
