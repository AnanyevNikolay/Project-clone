package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Economist;


public interface EconomistService {

    Economist findByEmail(String email);

    Economist persist(Economist economist);

    void deleteAll();
}
