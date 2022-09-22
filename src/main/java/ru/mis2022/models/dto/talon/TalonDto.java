package ru.mis2022.models.dto.talon;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mis2022.models.dto.patient.PatientDto;
import ru.mis2022.models.dto.patient.converter.PatientDtoConverter;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.service.entity.RoleService;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TalonDto {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime time;

    private Long doctorId;

    private PatientDto patient;
    private RoleRepository roleRepository;

    public TalonDto(Long id, LocalDateTime time, Long doctorId, Patient patient) {
        PatientDtoConverter patientDtoConverter = new PatientDtoConverter(roleRepository);
        this.id = id;
        this.time = time;
        this.doctorId = doctorId;
        this.patient = patientDtoConverter.toDto(patient);
    }
}

