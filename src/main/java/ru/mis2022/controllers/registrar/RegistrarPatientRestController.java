package ru.mis2022.controllers.registrar;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.response.Response;

import java.util.List;

@RestController
@PreAuthorize("hasRole('REGISTRAR')")
@RequestMapping("/api/registrar/patient")
public class RegistrarPatientRestController {

    public Response<List<Patient>> searchPatientByFirstNameOrLastNameOrPolisOrSnils(
            @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
            @RequestParam("polis") String polis, @RequestParam("snils") String snils,
            @RequestParam("offset") Integer offset) {

    }

}
