package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Doctor;

public interface DoctorService {

    Doctor findByEmail(String email);

    Doctor persist(Doctor doctor);
}
