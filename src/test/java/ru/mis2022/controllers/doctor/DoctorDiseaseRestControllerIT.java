package ru.mis2022.controllers.doctor;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Disease;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Role;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.DiseaseService;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo list 5 написать метод clear() дабы избавиться от аннотации Transactional
//  в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с DoctorPatientRestControllerIT#registerPatientInTalon
@Transactional
public class DoctorDiseaseRestControllerIT extends ContextIT {
    @Autowired
    DoctorService doctorService;
    @Autowired
    RoleService roleService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    DiseaseService diseaseService;

    Role initRole(String name) {
        return roleService.save(Role.builder()
                .name(name)
                .build());
    }

    Department initDepartment(String name) {
        return departmentService.save(Department.builder()
                .name(name)
                .build());
    }

    Doctor initDoctor(Role role, Department department, PersonalHistory personalHistory, String email) {
        return doctorService.persist(new Doctor(
                email,
                String.valueOf("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                department
        ));
    }

    Disease initDisease(String identifier, String name, Department department) {
        return diseaseService.save(Disease.builder()
                .identifier(identifier)
                .name(name)
                .department(department)
                .build());
    }

    @Test
    void getAllDiseaseByDoctorIdTest() throws Exception {
        Department department = initDepartment("Pediatrics");
        Department department1 = initDepartment("Surgery");
        Role role = initRole("DOCTOR");
        Doctor doctor = initDoctor(role, department, null, "Doctor@gmail.com");

        accessToken = tokenUtil.obtainNewAccessToken(doctor.getEmail(), "1", mockMvc);

        //Доктор с таким ид не найден
        mockMvc.perform(get("/api/doctor/disease/{doctorId}/getAllDisease", 88888)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(414)))
                .andExpect(jsonPath("$.text", Is.is("Доктора с таким id нет!")));

        //Списка заболеваний нет
        mockMvc.perform(get("/api/doctor/disease/{doctorId}/getAllDisease", doctor.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(0)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))


        //Вернуть все заболевания отделения доктора
        Disease disease1 = (initDisease("T01", "Headache", department));
        Disease disease2 = (initDisease("T02", "Backache", department));

        //сохранили болезни других отделений для проверки
        Disease disease3 = initDisease("G3", "Fracture", department1);
        Disease disease4 = initDisease("G76", "Injury", null);


        mockMvc.perform(get("/api/doctor/disease/{doctorId}/getAllDisease", doctor.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(2)))

                .andExpect(jsonPath("$.data[0].id", Is.is(disease1.getId().intValue())))
                .andExpect(jsonPath("$.data[0].identifier", Is.is(disease1.getIdentifier())))
                .andExpect(jsonPath("$.data[0].name", Is.is(disease1.getName())))


                .andExpect(jsonPath("$.data[1].id", Is.is(disease2.getId().intValue())))
                .andExpect(jsonPath("$.data[1].name", Is.is(disease2.getName())))
                .andExpect(jsonPath("$.data[1].identifier", Is.is(disease2.getIdentifier())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }
}
