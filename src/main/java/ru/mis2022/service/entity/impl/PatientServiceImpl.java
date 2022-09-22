package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.repositories.PatientRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.service.entity.RoleService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    @Override
    public Patient findByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public Patient persist(Patient patient) {
        patient.setPassword(encoder.encode(patient.getPassword()));
        patient.setRole(roleRepository.findByName("PATIENT"));
        return patientRepository.save(patient);
    }

    @Override
    public Patient findPatientById(Long id) {
        return patientRepository.findPatientById(id);
    }

    @Override
    public boolean isExistById(Long id) {
        return patientRepository.existsById(id);
    }

    @Override
    public List<Patient> findPatientByFullName(String fullName) {
        return patientRepository.findPatientByFullName(fullName);
    }

    @Override
    public void deleteAll() {
        patientRepository.deleteAll();
    }

}
