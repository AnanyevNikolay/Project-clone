package ru.mis2022.service.entity;

import ru.mis2022.models.entity.HrManager;

public interface HrManagerService {

    HrManager findByEmail(String email);

    HrManager persist(HrManager hrManager);

    void deleteAll();
}
