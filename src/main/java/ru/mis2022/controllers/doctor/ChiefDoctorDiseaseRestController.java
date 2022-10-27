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
import ru.mis2022.models.entity.Disease;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.DiseaseService;
import ru.mis2022.utils.validation.ApiValidationUtils;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('CHIEF_DOCTOR')")
@RequestMapping("/api/chief-doctor/disease")
public class ChiefDoctorDiseaseRestController {

    private final DiseaseService diseaseService;
    private final DiseaseDtoConverter diseaseDtoConverter;

    @ApiOperation("Заведущий отделения блокирует заболевание от отделения.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Заболевание от отеделения заблокировано."),
            @ApiResponse(code = 410, message = "Заболевания не существует."),
            @ApiResponse(code = 411, message = "Заболеваним не занимается данный доктор.")
    })
    @PatchMapping("/changeDisabledOnTrue/{diseaseId}")
    public Response<DiseaseDto> changeDisabledOnTrue(@PathVariable Long diseaseId) {
        Doctor doctor = ((Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Disease disease = diseaseService.findDiseaseById(diseaseId);
        validationEndpoint(doctor, disease);
        return Response.ok(diseaseDtoConverter.toDiseaseDto(diseaseService.changeDisabledDisease(disease, true)));
    }

    @ApiOperation("Заведущий отделения разблокирует заболевание от отделения.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Заболевание от отеделения разблокировано."),
            @ApiResponse(code = 410, message = "Заболевания не существует."),
            @ApiResponse(code = 411, message = "Заболеваним не занимается данный доктор.")
    })
    @PatchMapping("/changeDisabledOnFalse/{diseaseId}")
    public Response<DiseaseDto> changeDisabledOnFalse(@PathVariable Long diseaseId) {
        Doctor doctor = ((Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Disease disease = diseaseService.findDiseaseById(diseaseId);
        validationEndpoint(doctor, disease);
        return Response.ok(diseaseDtoConverter.toDiseaseDto(diseaseService.changeDisabledDisease(disease, false)));
    }

    private void validationEndpoint(Doctor doctor, Disease disease) {
        ApiValidationUtils.expectedNotNull(
                disease,
                410,
                "Заболевания не существует.");

        ApiValidationUtils.expectedTrue(
                diseaseService.existsDiseaseByDiseaseIdAndDoctorId(disease.getId(), doctor.getId()),
                411,
                "Заболеваним не занимается данный доктор.");
    }
}
