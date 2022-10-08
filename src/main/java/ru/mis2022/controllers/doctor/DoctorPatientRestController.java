package ru.mis2022.controllers.doctor;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.patient.PatientDto;
import ru.mis2022.models.dto.patient.converter.PatientDtoConverter;
import ru.mis2022.models.dto.talon.TalonDto;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.service.entity.TalonService;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
@RequestMapping("/api/doctor/patient")

public class DoctorPatientRestController {
    private final PatientService patientService;
    private final PatientDtoConverter patientDtoConverter;
    private final TalonService talonService;

    @ApiOperation("Доктор получает пациентов по паттерну")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список пациентов")
    })
    @GetMapping("/stringPattern")
    public Response<List<PatientDto>> findPatientByFullName(
            @RequestParam(required = false, defaultValue = "") String stringPattern) {
        String fullNames = stringPattern.replaceAll("\\s+", "%");
        return Response.ok(patientDtoConverter.toPatientDto(patientService.findPatientByFullName(fullNames)));
    }

    @ApiOperation("Доктор записывает пациента на определенное время")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пациент записан"),
            @ApiResponse(code = 401, message = "Талона не существует"),
            @ApiResponse(code = 410, message = "Пациента не существует"),
            @ApiResponse(code = 415, message = "Талон занят"),
            @ApiResponse(code = 420, message = "Талон принадлежит другому доктору")

    })
    @PostMapping("/registerInTalon")
    public Response<TalonDto> registerPatientInTalon(@RequestParam Long talonId,
                                                     @RequestParam Long patientId) {
        Long currentDocId = ((Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        Talon talon = talonService.findTalonWithDoctorAndPatientByTalonId(talonId);

        ApiValidationUtils.expectedNotNull(talon, 401,
                "Талона не существует");
        ApiValidationUtils.expectedNotNull(talon.getDoctor(), 420,
                "У талона нет доктора");
        ApiValidationUtils.expectedEqual(talon.getDoctor().getId(), currentDocId, 425,
                "Талон принадлежит другому доктору");

        Patient patient = patientService.findPatientById(patientId);
        ApiValidationUtils.expectedNotNull(patient, 410,
                "Пациента не существует");
        ApiValidationUtils.expectedFalse( talon.getPatient() != null
                        && !patient.getId().equals(talon.getPatient().getId()) , 415,
                "Талон занят");

        return Response.ok(talonService.registerPatientInTalon(talon, patient));
    }

}
