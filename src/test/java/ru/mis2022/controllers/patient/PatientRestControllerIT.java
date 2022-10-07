package ru.mis2022.controllers.patient;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.Role;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo list 15 написать метод clear() дабы избавиться от аннотации Transactional
//  в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с DoctorPatientRestControllerIT#registerPatientInTalon
@Transactional
public class PatientRestControllerIT extends ContextIT {

    @Autowired RoleService roleService;
    @Autowired PatientService patientService;

    Role initRole(String name) {
        return roleService.save(Role.builder()
                .name(name)
                .build());
    }

    Patient initPatient(Role role) {
        return patientService.persist(new Patient(
                "patient1@email.com",
                String.valueOf("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                "passport",
                "polis",
                "snils",
                "address"
        ));
    }

    @Test
    public void getCurrentUserTest() throws Exception {
        Role role = initRole("PATIENT");
        Patient patient = initPatient(role);

        accessToken = tokenUtil.obtainNewAccessToken(patient.getEmail(), "1", mockMvc);

        mockMvc.perform(get("/api/patient/mainPage/current")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.roleName", Is.is("PATIENT")))
                .andExpect(jsonPath("$.data.lastName", Is.is("l_name")))
                .andExpect(jsonPath("$.data.firstName", Is.is("f_name")))
                .andExpect(jsonPath("$.data.birthday", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data.passport", Is.is("passport")))
                .andExpect(jsonPath("$.data.polis", Is.is("polis")))
                .andExpect(jsonPath("$.data.snils", Is.is("snils")))
                .andExpect(jsonPath("$.data.address", Is.is("address")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }

}
