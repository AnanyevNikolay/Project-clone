package ru.mis2022.controllers.registrar;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.patient.PatientDto;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.dto.PatientDtoService;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.util.List;

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
            @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
            @RequestParam("polis") String polis, @RequestParam("snils") String snils,
            @RequestParam("offset") Integer offset) {
        ApiValidationUtils.expectedTrue(offset >= 0, 422, "Неверно указан номер страницы");
        List<PatientDto> patientsDto = patientDtoService.findPatientsByFirstNameOrLastNameOrPolisOrSnilsPattern(
                firstName, lastName, polis, snils, offset
        );
        ApiValidationUtils.expectedFalse(patientsDto.isEmpty(), 404, "Пользователей по этому паттерну не найдено");
        return Response.ok(patientsDto);
    }

}
