package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.dto.visit.VisitDto;
import ru.mis2022.models.dto.visit.converter.VisitDtoConverter;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.MedicalService;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.models.entity.Visit;
import ru.mis2022.repositories.TalonRepository;
import ru.mis2022.repositories.VisitRepository;
import ru.mis2022.service.entity.AppealService;
import ru.mis2022.service.entity.VisitService;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final TalonRepository talonRepository;
    private final VisitDtoConverter visitDtoConverter;
    private final AppealService appealService;

    @Override
    public Visit save(Visit visit) {
        return visitRepository.save(visit);
    }

    @Override
    @Transactional
    public VisitDto createVisitByTalonIdAndAppealId(Talon talon, Appeal appeal) {
        Visit visit = save(new Visit(talon.getTime().toLocalDate(), talon.getDoctor(), appeal, null));
        talonRepository.delete(talon);
        return visitDtoConverter.toDto(visit);
    }

    @Override
    public Visit findByIdAndDoctorIdWithAppeal(long visitId, long doctorId) {
        return visitRepository.findByIdAndDoctorIdWithAppeal(visitId, doctorId);
    }

    public VisitDto addMedicalServicesInVisit(Visit visit, Set<MedicalService> medicalServices, boolean closeAppeal) {
        visit.setMedicalServices(medicalServices);
        if (closeAppeal) {
            visit.getAppeal().setClosed(true);
            appealService.save(visit.getAppeal());
        }
        return visitDtoConverter.toDto(save(visit));
    }
}
