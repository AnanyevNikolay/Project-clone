package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.RoleService;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder encoder;
    private final RoleService roleService;

    @Override
    public Doctor findByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public Doctor persist(Doctor doctor) {
        doctor.setPassword(encoder.encode(doctor.getPassword()));
        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor merge(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Override
    public boolean isExistsById(long doctorId) {
        return doctorRepository.existsById(doctorId);
    }

    @Override
    public void deleteAll() {
        doctorRepository.deleteAll();
    }

    @Override
    public Doctor findByIdAndDepartment(long doctorId, long departmentId) {
        return doctorRepository.findByIdAndDepartment(doctorId, departmentId);
    }

    @Override
    public boolean countOfChiefDoctorInDepartment(long departmentId) {
        return doctorRepository.findByRoleForChief(departmentId) >= 2;
    }

    @Override
    @Transactional
    public Doctor changeRoleDoctor(Doctor doctor, String roleName) {
        doctor.setRole(roleService.findByName(roleName));
        return doctor;
    }

    @Override
    public Doctor findById(long doctorId) {
        return doctorRepository.findDoctorById(doctorId);
    }

    @Override
    public boolean findByIdForMain() {
        return doctorRepository.findByRoleForMain() >= 2;
    }

}