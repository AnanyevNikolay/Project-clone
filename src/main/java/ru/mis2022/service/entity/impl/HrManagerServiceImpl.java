package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.HrManager;
import ru.mis2022.repositories.HrManagerRepository;
import ru.mis2022.service.entity.HrManagerService;

@Service
@RequiredArgsConstructor
public class HrManagerServiceImpl implements HrManagerService {

    private final HrManagerRepository hrManagerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public HrManager findByEmail(String email) {
        return hrManagerRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public HrManager persist(HrManager hrManager) {
        hrManager.setPassword(passwordEncoder.encode(hrManager.getPassword()));
        return hrManagerRepository.save(hrManager);
    }

    @Override
    public void deleteAll() {
        hrManagerRepository.deleteAll();
    }
}
