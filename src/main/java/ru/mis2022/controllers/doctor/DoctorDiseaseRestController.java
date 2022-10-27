package ru.mis2022.controllers.doctor;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.disease.DiseaseDto;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.dto.DiseaseDtoService;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.util.List;


@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
@RequestMapping("/api/doctor/disease")
public class DoctorDiseaseRestController {
    private final DoctorService doctorService;
    private final DiseaseDtoService diseaseDtoService;

    @ApiOperation("Получение всех доступных заболеваний отделения доктора.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список всех заболеваний отделения доктора."),
            @ApiResponse(code = 414, message = "Доктора с таким id нет!"),
    })
    @GetMapping("/{doctorId}/getAllDisease")
    public Response<List<DiseaseDto>> getAllDiseaseByDepartmentsId(@PathVariable Long doctorId) {
        ApiValidationUtils
                .expectedTrue(doctorService.isExistsById(doctorId),
                        414, "Доктора с таким id нет!");
        return Response.ok(diseaseDtoService.findDiseaseByDepartmentDoctors(doctorId, false));
    }
}
