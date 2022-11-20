package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.dto.doctor.DoctorDto;
import ru.mis2022.models.dto.doctor.converter.DoctorDtoConverter;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Vacation;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.HrManagerService;
import ru.mis2022.service.entity.RoleService;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder encoder;
    private final RoleService roleService;
    private final DoctorDtoConverter doctorDtoConverter;

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

    @Override
    public List<DoctorDto> daysForVacations() {
        List<Doctor> doctors = doctorRepository.findAllWhoNeedVacation();
        List<DoctorDto> doctorDtoList = new ArrayList<>();

        for (Doctor d : doctors) {
            int ofEmployment = d.getPersonalHistory().getDateOfEmployment().getMonth().getValue();
            int allDaysOfVacations = 0;

            if (!d.getPersonalHistory().getVacations().isEmpty()) {
                for (Vacation v : d.getPersonalHistory().getVacations()) {
                    int daysOfVacations = v.getDateTo().getDayOfMonth() - v.getDateFrom().getDayOfMonth();
                    allDaysOfVacations += daysOfVacations;
                }
            }
            int daysForVacation = 3 * (12 - ofEmployment) - allDaysOfVacations;
            DoctorDto dto = DoctorDto.builder()
                    .firstName(d.getFirstName())
                    .lastName(d.getLastName())
                    .surname(d.getSurname())
                    .department(d.getDepartment().toString())
                    .daysForVacations(daysForVacation)
                    .build();
            doctorDtoList.add(dto);
        }
        return doctorDtoList;
    }
}