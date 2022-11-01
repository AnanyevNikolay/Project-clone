package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Doctor;


public interface DoctorService {

    Doctor findByEmail(String email);

    Doctor persist(Doctor doctor);

    Doctor merge(Doctor doctor);

    boolean isExistsById(long doctorId);

    void deleteAll();
    Doctor findByIdAndDepartment(long doctorId, long departmentId);

    boolean countOfChiefDoctorInDepartment(long departmentId);

    Doctor changeRoleDoctor(Doctor doctor, String roleName);

    Doctor findById(long doctorId);

    boolean findByIdForMain();

}
