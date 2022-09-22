package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Administrator;
import ru.mis2022.repositories.AdministratorRepository;
import ru.mis2022.service.entity.AdministratorService;
import ru.mis2022.utils.GenerateRandomString;

@Service
@RequiredArgsConstructor
public class AdministratorServiceImpl implements AdministratorService {

    private final PasswordEncoder encoder;
    private final AdministratorRepository administratorRepository;
    private final GenerateRandomString generator;
    @Value("15")
    private int randomPasswordLength;

    @Override
    public Administrator findByEmail(String email) {
        return administratorRepository.findByEmail(email);
    }

    @Override
    public Administrator findAdministratorById(Long id) {
        return administratorRepository.findAdministratorById(id);
    }

    @Override
    @Transactional
    public Administrator persist(Administrator administrator) {
        administrator.setPassword(encoder.encode(generator.getRndStr(randomPasswordLength)));
        administrator = administratorRepository.save(administrator);
        return administrator;
    }

    @Override
    @Transactional
    public Administrator merge(Administrator administrator) {
        administrator.setPassword(encoder.encode(administrator.getPassword()));
        return administratorRepository.save(administrator);
    }

    @Override
    public boolean isExistById(Long id) {
        return administratorRepository.existsById(id);
    }

    @Override
    public void deleteAll() {
        administratorRepository.deleteAll();
    }

}
