package ru.mis2022.controllers.doctor;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.visit.VisitDto;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.AppealService;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.TalonService;
import ru.mis2022.service.entity.VisitService;
import ru.mis2022.utils.validation.ApiValidationUtils;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('DOCTOR')")
@RequestMapping("/api/doctor/visit")
public class DoctorVisitRestController {

    private final VisitService visitService;
    private final TalonService talonService;
    private final AppealService appealService;
    private final DepartmentService departmentService;

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
}
