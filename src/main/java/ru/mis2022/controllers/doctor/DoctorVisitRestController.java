package ru.mis2022.controllers.doctor;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.visit.VisitDto;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.MedicalService;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.models.entity.Visit;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.AppealService;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.MedicalServiceService;
import ru.mis2022.service.entity.TalonService;
import ru.mis2022.service.entity.VisitService;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('DOCTOR')")
@RequestMapping("/api/doctor/visit")
public class DoctorVisitRestController {

    private final VisitService visitService;
    private final TalonService talonService;
    private final AppealService appealService;
    private final DepartmentService departmentService;
    private final MedicalServiceService medicalServiceService;

    @ApiOperation("Доктор создает посещение.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Посещение создано."),
            @ApiResponse(code = 410, message = "Талона не существует."),
            @ApiResponse(code = 411, message = "Обращения не существует."),
            @ApiResponse(code = 412, message = "Обращение закрыто."),
            @ApiResponse(code = 413, message = "Данный доктор не может создать посещение.")

    })
    @PostMapping("/create")
    public Response<VisitDto> createVisit(@RequestParam long talonId, @RequestParam long appealId) {
        Talon talon = talonService.findTalonById(talonId);
        ApiValidationUtils.expectedNotNull(talon, 410, "Талона не существует.");
        Appeal appeal = appealService.findAppealById(appealId);
        ApiValidationUtils.expectedNotNull(appeal, 411, "Обращения не существует.");
        ApiValidationUtils.expectedFalse(appeal.isClosed(), 412, "Обращение закрыто.");
        ApiValidationUtils.expectedTrue(departmentService.isExistByTalonIdAndAppealId(
                talonId, appealId),
                413,
                "Данный доктор не может создать посещение.");
        return Response.ok(visitService.createVisitByTalonIdAndAppealId(talon, appeal));
    }

    @ApiOperation("Доктор наполняет визит услугами")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Услуги заполнены"),
            @ApiResponse(code = 410, message = "Посещения с таким ID для данного доктора не существует"),
            @ApiResponse(code = 411, message = "Обращение закрыто"),
            @ApiResponse(code = 412, message = "Некоторые услуги не существуют или не могут быть оказаны в этом отделении")
    })
    @PatchMapping("/addServices/{visitId}")
    public Response<VisitDto> addServicesToVisit(@PathVariable long visitId,
                                                 @RequestParam boolean closeAppeal,
                                                 @RequestBody Set<Long> servicesIds) {
        long doctorId = ((Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        long departmentId = departmentService.findDepartmentByDoctorId(doctorId).getId();
        Visit visit = visitService.findByIdAndDoctorIdWithAppeal(visitId, doctorId);
        ApiValidationUtils.expectedNotNull(visit, 410, "Посещения с таким ID для данного доктора не существует");
        ApiValidationUtils.expectedFalse(visit.getAppeal().isClosed(), 411, "Обращение закрыто");
        Set<MedicalService> medicalServices =
                medicalServiceService.getMedicalServicesByIdsAndDepartment(servicesIds, departmentId);
        ApiValidationUtils.expectedEqual(medicalServices.size(), servicesIds.size(), 412,
                "Некоторые услуги не существуют или не могут быть оказаны в этом отделении");
        return Response.ok(visitService.addMedicalServicesInVisit(visit, medicalServices, closeAppeal));
    }
}
