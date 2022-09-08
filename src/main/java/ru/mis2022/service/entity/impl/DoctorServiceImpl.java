package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.dto.doctor.DoctorDto;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.service.entity.DoctorService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final PasswordEncoder encoder;

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
    public List<Doctor> findAllByDepartmentId(Long id) {
        return doctorRepository.findAllByDepartmentId(id);
    }

    @Override
    public Doctor findByDoctorId(Long id) {
        return doctorRepository.findDoctorById(id);
    }

    @Override
    public boolean isExistsById(long doctorId) {
        return doctorRepository.existsById(doctorId);
    }

    @Override
    public List<DoctorDto> findDoctorsDtoByDepartmentId(Long departmentId) {
        return doctorRepository.findAllByDepartmentIdDto(departmentId);
    }

}
