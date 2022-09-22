package ru.mis2022.models.dto.patient.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.mis2022.models.dto.patient.PatientDto;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.service.entity.RoleService;

import java.util.ArrayList;
import java.util.List;

@Component
@Service
@RequiredArgsConstructor
public class PatientDtoConverter {

    private final RoleRepository roleRepository;
    public PatientDto toDto(Patient entity) {
        if (entity == null) {
            return null;
        }
        return PatientDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .surName(entity.getSurname())
                .birthday(entity.getBirthday())
                .roleName(entity.getRole().getName())
                .passport(entity.getPassport())
                .polis(entity.getPolis())
                .snils(entity.getSnils())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .build();
    }

    public Patient toEntity(PatientDto dto) {

        return Patient.builder()
                .id(dto.id())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .surname(dto.surName())
                .birthday(dto.birthday())
                .role(roleRepository.findByName(dto.roleName()))
                .passport(dto.passport())
                .polis(dto.polis())
                .snils(dto.snils())
                .email(dto.email())
                .password(dto.password())
                .build();
    }

    public List<PatientDto> toPatientDto (List<Patient> patients) {
        List<PatientDto> patientDtos = new ArrayList<>();
        for (Patient patient: patients) {
            patientDtos.add(PatientDto.builder()
                    .id(patient.getId())
                    .firstName(patient.getFirstName())
                    .lastName(patient.getLastName())
                    .surName(patient.getSurname())
                    .birthday(patient.getBirthday())
                    .passport(patient.getPassport().replaceAll("\\s+","").substring(6))
                    .polis(patient.getPolis().substring(12))
                    .snils(patient.getSnils().replaceAll("[^A-Za-zА-Яа-я0-9]", "").substring(7))
                    .build());
        }
        return patientDtos;
    }

}
