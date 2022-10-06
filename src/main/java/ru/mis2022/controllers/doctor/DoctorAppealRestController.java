package ru.mis2022.controllers.doctor;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.appeal.AppealDto;
import ru.mis2022.models.dto.appeal.converter.AppealDtoConverter;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.Disease;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.AppealService;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.DiseaseService;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
@RequestMapping("/api/doctor/appeal")
public class DoctorAppealRestController {
    private final PatientService patientService;
    private final DiseaseService diseaseService;
    private final AppealService appealService;
    private final AppealDtoConverter converter;
    private final DepartmentService departmentService;

    @ApiOperation("Доктор создает обращение по заболеванию без посещения и счета")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Обращение по заболеванию создано"),
            @ApiResponse(code = 410, message = "Пациент не существует"),
            @ApiResponse(code = 411, message = "Заболевание не существует"),
            @ApiResponse(code = 412, message = "Заболевание не лечится в текущем отделении")
    })
    @PostMapping("/create")
    public Response<AppealDto> addAppeal(@RequestParam Long patientId,
                                         @RequestParam Long diseaseId,
                                         @RequestParam Long departmentId) {
        Patient patient = patientService.findPatientById(patientId);

        ApiValidationUtils
                .expectedNotNull(patient, 410, "Пациент не существует");

        Disease disease = diseaseService.findDiseaseById(diseaseId);
        ApiValidationUtils
                .expectedNotNull(disease, 411, "Заболевание не существует");

        Doctor currentDoc = ((Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        ApiValidationUtils
                .expectedEqual(departmentService.findDepartmentById(departmentId), departmentService.findDepartmentByDoctorId(currentDoc.getId()), 412,
                "Заболевание не лечится в текущем отделении");

        return Response.ok(
                converter.toAppealDto(
                        appealService.save(new Appeal(patient, disease, LocalDate.now()))));
    }
}
