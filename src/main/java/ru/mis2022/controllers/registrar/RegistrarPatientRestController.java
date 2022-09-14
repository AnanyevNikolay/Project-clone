package ru.mis2022.controllers.registrar;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.patient.PatientDto;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.dto.PatientDtoService;

import java.util.List;

@RestController
@PreAuthorize("hasRole('REGISTRAR')")
@RequestMapping("/api/registrar/patient")
@RequiredArgsConstructor
public class RegistrarPatientRestController {

    private final PatientDtoService patientDtoService;

    @GetMapping
    public Response<List<PatientDto>> searchPatientByFirstNameOrLastNameOrPolisOrSnils(
            @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
            @RequestParam("polis") String polis, @RequestParam("snils") String snils,
            @RequestParam("offset") Integer offset) {
            int a = 1;
            return Response.ok(patientDtoService.findPatientsByFirstNameOrLastNameOrPolisOrSnilsPattern(
                    firstName, lastName, polis, snils, offset
            ));
    }

}
