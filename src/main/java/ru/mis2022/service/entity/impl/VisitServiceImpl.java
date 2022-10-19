package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.entity.Visit;
import ru.mis2022.repositories.VisitRepository;
import ru.mis2022.service.entity.VisitService;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;

    @Override
    public Visit save(Visit visit) {
        return visitRepository.save(visit);
    }
}
