package ru.mis2022.controllers.doctor;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Role;
import ru.mis2022.service.entity.DepartmentService;
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
public class ChiefDoctorRestControllerT extends ContextIT {

    RoleService roleService;

    DoctorService doctorService;

    DepartmentService departmentService;

    @Autowired
    public ChiefDoctorRestControllerT(RoleService roleService, DoctorService doctorService, DepartmentService departmentService) {
        this.roleService = roleService;
        this.doctorService = doctorService;
        this.departmentService = departmentService;
    }

    Role initRole(String roleName) {
        return roleService.save(Role.builder()
                .name(roleName)
                .build());
    }

    Doctor initDoctor(Role role, Department department, PersonalHistory personalHistory) {
        return doctorService.persist(new Doctor(
                "patient1@email.com",
                String.valueOf("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                department
        ));
    }

    Department initDepartement(String name) {
        return departmentService.save(Department.builder()
                .name(name)
                .build());
    }

    @Test
    public void getCurrentUserTest() throws Exception {
        Role role = initRole("CHIEF_DOCTOR");
        Department department = initDepartement("Therapy");
        Doctor doctor = initDoctor(role, department, null);

        accessToken = tokenUtil.obtainNewAccessToken(doctor.getEmail(), "1", mockMvc);

        mockMvc.perform(get("/api/chief-doctor/mainPage/current")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.roleName", Is.is("CHIEF_DOCTOR")))
                .andExpect(jsonPath("$.data.lastName", Is.is("l_name")))
                .andExpect(jsonPath("$.data.firstName", Is.is("f_name")))
                .andExpect(jsonPath("$.data.departmentName", Is.is("Therapy")))
                .andExpect(jsonPath("$.data.birthday", Matchers.notNullValue()));
        //          .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }
}
