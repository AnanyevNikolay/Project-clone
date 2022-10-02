package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Yet;

import java.time.LocalDate;
import java.util.List;

public interface YetService {

    Yet save(Yet yet);

    Yet existById(Long id);

    List<Yet> existYetDayFromDayTo(LocalDate dayFrom, LocalDate dayTo);

    List<Yet> findAll();

    void deleteById(Long id);

    List<Yet> existYetDayFromDayToExceptCurrentId(Long id, LocalDate dayFrom, LocalDate dayTo);

    List<Yet> findAllYetsBetweenDayFromAndDayTo(LocalDate dayFrom, LocalDate dayTo);

    boolean checkIfThereAnyDateOverlap(List<Yet> yets);

    void deleteAll();

}
