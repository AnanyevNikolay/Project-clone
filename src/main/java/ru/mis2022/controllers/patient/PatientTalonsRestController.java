package ru.mis2022.controllers.patient;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.talon.TalonDto;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.dto.TalonDtoService;
import ru.mis2022.service.entity.TalonService;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.util.List;


@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
@RequestMapping("/api/patient/talons")
public class PatientTalonsRestController {
    private final TalonService talonService;
    private final TalonDtoService talonDtoService;

    @ApiOperation("Авторизованный пациент получает все свои талоны на которые у него есть запись")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Получение всех талонов, занятых пациентом")
    })
    @GetMapping
    public Response<List<TalonDto>> getAllTalonsByCurrentPatient() {
        long patientId = ((Patient) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return Response.ok(talonDtoService.findAllByPatientId(patientId));
    }

    @ApiOperation("Пациент удаляет запись к врачу по id талона")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Запись по талону удалена"),
            @ApiResponse(code = 402, message = "Талона с таким id нет!"),
            @ApiResponse(code = 403, message = "Пациент не записан по этому талону"),
    })
    @PatchMapping("/{talonId}")
    public Response<Void> cancelRecordTalons(@PathVariable Long talonId) {
        Talon talon = talonService.findTalonById(talonId);
        ApiValidationUtils
                .expectedNotNull(talon, 402, "Талона с таким id нет!");
        long userId = ((Patient) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        ApiValidationUtils
                .expectedTrue(userId == talonService.findPatientIdByTalonId(talonId),
                        403, "Пациент не записан по этому талону");
        talon.setPatient(null);
        talonService.save(talon);
        return Response.ok();
    }

    @ApiOperation("Пациент записывается на талон")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Запись прошла успешно"),
            @ApiResponse(code = 402, message = "Талона с данным id не существует"),
            @ApiResponse(code = 403, message = "Талон уже занят"),
            @ApiResponse(code = 404, message = "У вас уже есть запись в данное отделение"),
            @ApiResponse(code = 405, message = "Врач на данный талон не назначен"),
            @ApiResponse(code = 406, message = "Вы уже записаны к врачу из данного отделения")
    })
    @PatchMapping("/recordOnTalon")
    public Response<TalonDto> recordOnTalon(Long talonId) {
        Talon talon = talonService.findTalonByIdWithDoctorAndPatient(talonId);
        ApiValidationUtils.expectedNotNull(talon,
                402, "Талона с данным id не существует");

        ApiValidationUtils.expectedNotNull(talon.getDoctor(),
                405, "Врач на данный талон не назначен");
        Doctor doctor = talon.getDoctor();

        ApiValidationUtils.expectedTrue(talon.getPatient() == null,
                403, "Талон уже занят");

        Patient patient = (Patient) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApiValidationUtils.expectedFalse(talonService.patientHaveTalonsFromDep(
                patient.getId(), doctor.getDepartment().getId()),
                406, "Вы уже записаны к врачу из данного отделения");
        return Response.ok(talonService.registerPatientInTalon(talon, patient));
    }
}
