package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Registrar;
import ru.mis2022.repositories.RegistrarRepository;
import ru.mis2022.service.entity.RegistrarService;

@Service
@RequiredArgsConstructor
public class RegistrarServiceImpl implements RegistrarService {

    private final RegistrarRepository registrarRepository;
    private final PasswordEncoder encoder;

    @Override
    public Registrar findByEmail(String email) {
        return registrarRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public Registrar persist(Registrar registrar) {
        registrar.setPassword(encoder.encode(registrar.getPassword()));
        return registrarRepository.save(registrar);
    }

    @Override
    public void deleteAll() {
        registrarRepository.deleteAll();
    }
}
