package ru.mis2022.controllers.registrar;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mis2022.models.dto.patient.PatientDto;
import org.springframework.web.bind.annotation.PostMapping;
import ru.mis2022.feign.PatientRequestDtoTS;
import ru.mis2022.feign.PatientResponseDtoTS;
import ru.mis2022.feign.TestSystemService;
import ru.mis2022.models.dto.patient.converter.PatientDtoConverter;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.dto.PatientDtoService;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.service.entity.UserService;
import ru.mis2022.utils.enums.patient.PatientSortingEnum;
import ru.mis2022.utils.validation.ApiValidationUtils;
import ru.mis2022.utils.validation.OnCreate;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@PreAuthorize("hasRole('REGISTRAR')")
@RequestMapping("/api/registrar/patient")
@RequiredArgsConstructor
public class RegistrarPatientRestController {

    private final PatientDtoService patientDtoService;

    private final PatientService patientService;
    private final UserService userService;

    private final PatientDtoConverter patientDtoConverter;

    private final TestSystemService testSystemService;

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

    @ApiOperation("Регистратор получает актуальные данные пациента из СМО")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные из СМО получены"),
            @ApiResponse(code = 402, message = "Неверные данные для авторизации в СМО")
    })
    @PostMapping("/findPeople")
    public Response<PatientResponseDtoTS> findPeople(@RequestBody PatientRequestDtoTS requestDto) {
        ApiValidationUtils
                .expectedTrue(testSystemService.updateToken(), 402, "Test system authorisation error");
        return Response.ok(testSystemService.findPeople(requestDto));
    }
    @ApiOperation("Регистратор создает нового пациента")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пациент добавлен в базу"),
            @ApiResponse(code = 400, message = "Некорректные данные переданы в ДТО"),
            @ApiResponse(code = 415, message = "Пациент с данным логином уже существует в базе"),
    })
    @Validated(OnCreate.class)
    @PostMapping("/create")
    public Response<PatientDto> createPatient(@Valid @RequestBody PatientDto patientDto) {
        ApiValidationUtils.expectedFalse(
                userService.existsByEmail(patientDto.email()),
                415, "Пациент с данным логином уже существует в базе");
        return Response.ok(patientDtoConverter.toDto(
                patientService.persist(patientDtoConverter.toEntity(patientDto))
        ));
    }

}
