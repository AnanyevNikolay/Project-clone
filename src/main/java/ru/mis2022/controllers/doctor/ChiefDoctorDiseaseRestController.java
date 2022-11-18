package ru.mis2022.controllers.doctor;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.disease.DiseaseDto;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Disease;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.dto.DiseaseDtoService;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.DiseaseService;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('CHIEF_DOCTOR')")
@RequestMapping("/api/chief-doctor/disease")
public class ChiefDoctorDiseaseRestController {
    private final DiseaseService diseaseService;

    private final DiseaseDtoService diseaseDtoService;

    private final DepartmentService departmentService;

    @ApiOperation("Заведущий отделения блокирует заболевание от отделения.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Заболевание от отеделения заблокировано."),
            @ApiResponse(code = 410, message = "Заболевания не существует."),
            @ApiResponse(code = 411, message = "Заболеванием не занимается данный доктор.")
    })
    @PatchMapping("/changeDisabledOnTrue/{diseaseId}")
    public Response<DiseaseDto> changeDisabledOnTrue(@PathVariable Long diseaseId) {
        Long doctorId = ((Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Disease disease = diseaseService.findDiseaseById(diseaseId);
        validationEndpoint(doctorId, disease);
        return Response.ok(diseaseService.changeDisabledDisease(disease, true));
    }

    @ApiOperation("Заведущий отделения разблокирует заболевание от отделения.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Заболевание от отеделения разблокировано."),
            @ApiResponse(code = 410, message = "Заболевания не существует."),
            @ApiResponse(code = 411, message = "Заболеванием не занимается данный доктор.")
    })
    @PatchMapping("/changeDisabledOnFalse/{diseaseId}")
    public Response<DiseaseDto> changeDisabledOnFalse(@PathVariable Long diseaseId) {
        Long doctorId = ((Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Disease disease = diseaseService.findDiseaseById(diseaseId);
        validationEndpoint(doctorId, disease);
        return Response.ok(diseaseService.changeDisabledDisease(disease, false));
    }

    private void validationEndpoint(Long doctorId, Disease disease) {
        ApiValidationUtils.expectedNotNull(
                disease,
                410,
                "Заболевания не существует.");

        ApiValidationUtils.expectedTrue(
                diseaseService.existsDiseaseByDiseaseIdAndDoctorId(disease.getId(), doctorId),
                411,
                "Заболеванием не занимается данный доктор.");
    }

    @ApiOperation("Заведующий отделения получает список заболеваний не связанных с отделениями")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список заболеваний не связанных с отделениями"),
    })
    @GetMapping("/listDiseaseWithoutDepartment")
    public Response<List<DiseaseDto>> getAllDiseasesWithoutDepartment() {
        return Response.ok(diseaseDtoService.findDiseaseWithoutDepartment());
    }

    @ApiOperation("Заведующий отделением связывает болезни с отделением доктора")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Связываем болезни с отделением доктора"),
            @ApiResponse(code = 401, message = "Болезни не существует или она уже связана с отделением"),
    })
    @PostMapping("/diseasesForDoctor")
    public Response<DiseaseDto> diseaseWithDoctorDepartment(long diseaseId) {
        Disease disease = diseaseService.findByIdWithoutDepartment(diseaseId);
        ApiValidationUtils.expectedNotNull(disease, 401, "Болезни не существует или она уже связана с отделением");
        Doctor doctor = ((Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Department department = doctor.getDepartment();
        return Response.ok(diseaseService.addDiseaseToDepartment(disease, department));
    }


    /*
    создать эндпоинт который принимает ид заболевания в параметрах.
    Он должен проверить что заболевание существует и принадлежит отделению.
    Потом он должен проверить, что нет ни одного обращения в отделении доктора,
    связанного с этим заболеванием. После этого надо отвязать заболевание от отделения
     */
    @ApiOperation("Заведущий отделения открепляет заболевание от отделения.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Заболевание откреплено от отеделения."),
            @ApiResponse(code = 410, message = "Заболевания не существует."),
            @ApiResponse(code = 411, message = "Заболеванием не занимается данный доктор."),
            @ApiResponse(code = 412, message = "Есть открытые обращения по данному заболеванию.")
    })
    @PatchMapping("/detachDisease/{diseaseId}")
    public Response<DiseaseDto> detachDisease(@PathVariable Long diseaseId) {
        Long doctorId = ((Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Disease disease = diseaseService.findDiseaseById(diseaseId);
        validationEndpoint(doctorId, disease);
        List<Appeal> app = diseaseService.existsAppealsByDiseaseId(diseaseId);
        ApiValidationUtils.expectedTrue(
                app.isEmpty(),
                412,
                "Есть открытые обращения по данному заболеванию."
        );
        return Response.ok(diseaseService.detachDisease(disease));
    }

}
