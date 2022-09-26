package ru.mis2022.controllers.patient;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.appeal.CurrentPatientAppealsDto;
import ru.mis2022.models.dto.appeal.converter.AppealDtoConverter;
import ru.mis2022.models.dto.patient.PatientAppealsDto;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.AppealService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
@RequestMapping("/api/patient/appeals")
public class PatientAppealRestController {
    private final AppealService appealService;
    private final AppealDtoConverter appealDtoConverter;

    @ApiOperation("Пациент получает историю обращений и посещений")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Метод возвращает историю обращений")
    })
    @GetMapping
    public Response<PatientAppealsDto> getCurrentPatientAppeals() {
        Patient patient = (Patient) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CurrentPatientAppealsDto> appealsDto = appealDtoConverter.convertAppealsListToAppealsDtoList(appealService.getAppealsDtoByPatientId(patient.getId()).orElse(null));
        return Response.ok(new PatientAppealsDto(
                patient.getId(),
                patient.getFirstName() + " " + patient.getLastName() + " " + patient.getSurname(),
                appealsDto
        ));
    }

}
