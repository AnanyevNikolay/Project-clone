package ru.mis2022.controllers.doctor;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.*;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @AfterEach
    protected void clear() {
        doctorService.deleteAll();
        roleService.deleteAll();
        departmentService.deleteAll();
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

        Doctor qryDoctor = entityManager.createQuery("""
                        SELECT doc
                        FROM Doctor doc
                        LEFT JOIN Department dep
                            ON doc.department.id = dep.id
                        LEFT JOIN Role rol
                            on doc.role.id = rol.id
                        WHERE dep.id = :depId
                            AND rol.id = :rolId
                        """, Doctor.class)
                .setParameter("depId", department.getId())
                .setParameter("rolId", role.getId())
                .getSingleResult();

        Assertions.assertEquals(qryDoctor.getId(), doctor.getId());
        Assertions.assertEquals(qryDoctor.getDepartment().getId(), doctor.getDepartment().getId());
        Assertions.assertEquals(qryDoctor.getRole().getId(), doctor.getRole().getId());
    }
}
