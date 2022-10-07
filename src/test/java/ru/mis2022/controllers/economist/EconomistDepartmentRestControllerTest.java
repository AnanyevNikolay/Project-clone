package ru.mis2022.controllers.economist;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.entity.Role;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.EconomistService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo list 9 написать метод clear() дабы избавиться от аннотации Transactional
//  в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с DoctorPatientRestControllerIT#registerPatientInTalon
@Transactional
class EconomistDepartmentRestControllerTest extends ContextIT {
    @Autowired
    DepartmentService departmentService;
    @Autowired
    RoleService roleService;
    @Autowired
    EconomistService economistService;
    Department initDepartment(String name) {
        return departmentService.save(Department.builder()
                .name(name)
                .build());
    }

    Role initRole(String name) {
        return roleService.save(Role.builder()
                .name(name)
                .build());
    }
    Economist initEconomist(Role role) {
        return economistService.persist(new Economist(
                "economist1@email.com",
                String.valueOf("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role
        ));
    }


    @Test
    void getAllDepartments()  throws Exception{
        Role role = initRole("ECONOMIST");
        Economist economist = initEconomist(role);

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        // В базе нет департаментов
        mockMvc.perform(get("/api/economist/departments")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(0));

        Department dept1 = initDepartment("Therapy1");
        Department dept2 = initDepartment("Therapy2");
        Department dept3 = initDepartment("Therapy3");

        // В базе три департамента
        mockMvc.perform(get("/api/economist/departments")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.size()").value(3))

                .andExpect(jsonPath("$.data[0].id", Is.is(dept1.getId().intValue())))
                .andExpect(jsonPath("$.data[0].name", Is.is(dept1.getName())))

                .andExpect(jsonPath("$.data[1].id", Is.is(dept2.getId().intValue())))
                .andExpect(jsonPath("$.data[1].name", Is.is(dept2.getName())))

                .andExpect(jsonPath("$.data[2].id", Is.is(dept3.getId().intValue())))
                .andExpect(jsonPath("$.data[2].name", Is.is(dept3.getName())));

    }
}