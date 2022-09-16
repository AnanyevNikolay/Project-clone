package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Registrar;

public interface RegistrarService {
    Registrar findByEmail(String email);

    Registrar persist(Registrar registrar);

    void deleteAll();
}
