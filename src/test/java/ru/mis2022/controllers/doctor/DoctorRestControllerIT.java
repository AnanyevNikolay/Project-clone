package ru.mis2022.controllers.doctor;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.AfterEach;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Role;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.util.ContextIT;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
public class DoctorRestControllerIT extends ContextIT {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    DoctorRepository doctorRepository;

    Role initRole(String name) {
        return roleRepository.save(Role.builder()
                .name(name)
                .build());
    }

    Department initDepartment(String name) {
        return departmentRepository.save(Department.builder()
                .name(name)
                .build());
    }

    Doctor initDoctor(Role role, Department department, PersonalHistory personalHistory) {
        return doctorRepository.save(new Doctor(
                "doctor1@email.com",
                passwordEncoder.encode("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                department
        ));
    }

    @AfterEach
    void clear() {
        doctorRepository.deleteAll();
        roleRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    public void getCurrentUserTest() throws Exception {
        Role role = initRole("DOCTOR");
        Department department = initDepartment("Therapy");
        Doctor doctor1 = initDoctor(role, department, null);


        accessToken = tokenUtil.obtainNewAccessToken(doctor1.getEmail(), "1", mockMvc);

        mockMvc.perform(get("/api/doctor/mainPage/current")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.roleName", Is.is("DOCTOR")))
                .andExpect(jsonPath("$.data.lastName", Is.is("l_name")))
                .andExpect(jsonPath("$.data.firstName", Is.is("f_name")))
                .andExpect(jsonPath("$.data.departmentName", Is.is("Therapy")))
                .andExpect(jsonPath("$.data.birthday", Matchers.notNullValue()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

    }

}