package ru.mis2022.controllers.doctor;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.disease.DiseaseDto;
import ru.mis2022.models.dto.disease.converter.DiseaseDtoConverter;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.DiseaseService;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.utils.validation.ApiValidationUtils;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('CHIEF_DOCTOR')")
@RequestMapping("/api/chief/doctor")
public class ChiefDoctorDiseaseRestController {

    private final DiseaseService diseaseService;
    private final DoctorService doctorService;
    private final DiseaseDtoConverter diseaseDtoConverter;

    @ApiOperation("Заведущий отделения блокирует заболевание от отделения.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Заболевание от отеделения заблокировано."),
            @ApiResponse(code = 410, message = "Заболевания не существует."),
            @ApiResponse(code = 411, message = "Заболеваним не занимается данный доктор.")
    })
    @PatchMapping("/changeDisabledOnTrue/{id}")
    public Response<DiseaseDto> changeDisabledOnTrue (@PathVariable("id") Long diseaseId) {
        ApiValidationUtils.expectedNotNull(
                diseaseService.findDiseaseById(diseaseId),
                410,
                "Заболевания не существует.");
        Doctor doctor = ((Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        ApiValidationUtils.expectedEqual(
                doctorService.findByEmail(doctor.getEmail()).getDepartment().getId(),
                diseaseService.findDiseaseById(diseaseId).getDepartment().getId(),
                411,
                "Заболеваним не занимается данный доктор.");
        return Response.ok(diseaseDtoConverter.toDiseaseDto(diseaseService.findDiseaseByIdAndChangeDisabled(diseaseId)));
    }

    @ApiOperation("Заведущий отделения разблокирует заболевание от отделения.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Заболевание от отеделения разблокировано."),
            @ApiResponse(code = 410, message = "Заболевания не существует."),
            @ApiResponse(code = 411, message = "Заболеваним не занимается данный доктор.")
    })
    @PatchMapping("/changeDisabledOnFalse/{id}")
    public Response<DiseaseDto> changeDisabledOnFalse (@PathVariable("id") Long diseaseId) {
        return changeDisabledOnTrue(diseaseId);
    }
}
