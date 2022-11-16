package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.repositories.AppealRepository;
import ru.mis2022.service.entity.AppealService;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AppealServiceImpl implements AppealService {

    private final AppealRepository appealRepository;

    @Override
    public Appeal save(Appeal appeal) {
        return appealRepository.save(appeal);
    }

    @Override
    public Optional<List<Appeal>> getAppealsDtoByPatientId(long patientId) {
        return appealRepository.getAppealsDtoByPatientId(patientId);
    }

    @Override
    public List<Appeal> getOpenAppealsDtoByPatientId(long patientId, boolean isClosed, long doctorId) {
        return appealRepository.getOpenAppealsDtoByPatientId(patientId, isClosed, doctorId);
    }

    @Override
    public void deleteAll() {
        appealRepository.deleteAll();
    }

    @Override
    public Appeal findAppealById(Long id) {
        return appealRepository.findAppealById(id);
    }
}
