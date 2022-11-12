package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Patient;

import java.util.List;


public interface PatientService {

    Patient findByEmail(String email);

    Patient persist(Patient patient);

    Patient findPatientById(Long id);

    boolean isExistById(Long id);

    List<Patient> findPatientByFullName (String fullName);

    void deleteAll();

}
