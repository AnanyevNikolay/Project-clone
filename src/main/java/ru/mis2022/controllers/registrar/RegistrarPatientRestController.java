package ru.mis2022.controllers.registrar;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.patient.PatientDto;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.dto.PatientDtoService;
import ru.mis2022.utils.enums.patient.PatientSortingEnum;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@PreAuthorize("hasRole('REGISTRAR')")
@RequestMapping("/api/registrar/patient")
@RequiredArgsConstructor
public class RegistrarPatientRestController {

    private final PatientDtoService patientDtoService;

    @ApiOperation("Регистратор получает пользователей по паттерну имени, или фамилии, или полиса, или снилса," +
            "или если все эти значения null, то регистратор получит всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список найденных по паттерну пользователей"),
            @ApiResponse(code = 404, message = "Пользователей по этому паттерну не найдено"),
            @ApiResponse(code = 422, message = "Неверно указан номер страницы")
    })
    @GetMapping
    public Response<List<PatientDto>> searchPatientByFirstNameOrLastNameOrPolisOrSnils(
            @RequestParam("firstName") @Nullable String firstName,
            @RequestParam("lastName") @Nullable String lastName,
            @RequestParam("polis") @Nullable String polis,
            @RequestParam("snils") @Nullable String snils,
            @RequestParam("offset") @Nullable Integer offset,
            @RequestParam("size") @Nullable Integer size,
            @RequestParam("sortBy") @Nullable PatientSortingEnum sortBy) {
        if (offset == null) offset = 0;
        if (size == null) size = 10;
        if (sortBy == null) sortBy = PatientSortingEnum.ID;
        ApiValidationUtils.expectedTrue(offset >= 0, 422, "Неверно указан номер страницы");
        Optional<List<PatientDto>> patientsDto = patientDtoService.findPatientsByFirstNameOrLastNameOrPolisOrSnilsPattern(
                firstName, lastName, polis, snils, offset, size, sortBy
        );
        return Response.ok(patientsDto.orElse(Collections.emptyList()));
    }

}
