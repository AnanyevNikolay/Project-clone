package ru.mis2022.controllers.registrar;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.department.DepartmentDto;
import ru.mis2022.models.dto.doctor.DoctorDto;
import ru.mis2022.models.dto.organization.MedicalOrganizationDto;
import ru.mis2022.models.dto.talon.TalonDto;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.dto.DepartmentDtoService;
import ru.mis2022.service.dto.DoctorDtoService;
import ru.mis2022.service.dto.MedicalOrganizationDtoService;
import ru.mis2022.service.dto.TalonDtoService;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.MedicalOrganizationService;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.util.List;


@RestController
@RequestMapping("/api/registrar/schedule")
@PreAuthorize("hasRole('REGISTRAR')")
@RequiredArgsConstructor
public class RegistrarScheduleRestController {
    private final MedicalOrganizationService medicalOrganizationService;
    private final MedicalOrganizationDtoService medicalOrganizationDtoService;
    private final DepartmentService departmentService;
    private final DepartmentDtoService departmentDtoService;
    private final DoctorService doctorService;
    private final DoctorDtoService doctorDtoService;
    private final TalonDtoService talonDtoService;

    @ApiOperation("Регистратор получает все медицинские организации")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список медицинских организаций"),
            @ApiResponse(code = 414, message = "Список медицинских организаций пуст!")
    })
    @GetMapping("/medicalOrganizations")
    public Response<List<MedicalOrganizationDto>> getAllMedicalOrganizations() {
        return Response.ok(medicalOrganizationDtoService.findAll());
    }

    @ApiOperation("Регистратор получает все отделения по переданной медицинской организации")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список департаментов"),
            @ApiResponse(code = 414, message = "Медицинской организации с таким id нет!"),
    })
    @PostMapping("/departments/{id}")
    public Response<List<DepartmentDto>> getAllDepartmentsByMedicalMedicalOrganizationId(@PathVariable Long id) {
        ApiValidationUtils
                .expectedTrue(medicalOrganizationService.isExistById(id),
                        414, "Медицинской организации с таким id нет!");
        return Response.ok(departmentDtoService.findAllByMedicalOrganizationId(id));
    }

    @ApiOperation("Регистратор получает всех врачей по переданному отделению")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список докторов"),
            @ApiResponse(code = 414, message = "Департамента с таким id нет!"),
    })
    @PostMapping("/doctors/{id}")
    public Response<List<DoctorDto>> getAllDoctorsByDepartmentId(@PathVariable Long id) {
        ApiValidationUtils
                .expectedTrue(departmentService.isExistById(id),
                        414, "Департамента с таким id нет!");
        return Response.ok(doctorDtoService.findAllByDepartmentId(id));
    }

    @ApiOperation("Регистратор получает все талоны по переданному доктору")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список талонов"),
            @ApiResponse(code = 414, message = "Доктора с таким id нет!"),
    })
    @PostMapping("/talons/{id}")
    public Response<List<TalonDto>> getAllTalonsByDoctorId(@PathVariable Long id) {
        ApiValidationUtils
                .expectedTrue(doctorService.isExistsById(id),
                        414, "Доктора с таким id нет!");
        return Response.ok(talonDtoService.findAllByDoctorId(id));
    }
}
