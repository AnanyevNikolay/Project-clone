package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Administrator;

public interface AdministratorService {

    Administrator findByEmail (String email);

    Administrator findAdministratorById(Long id);

    Administrator persist (Administrator administrator);

    Administrator merge (Administrator administrator);
}
