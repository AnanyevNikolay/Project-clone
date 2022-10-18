package ru.mis2022.controllers.economist;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.entity.Role;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.repositories.EconomistRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.service.entity.EconomistService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo list 6 написать метод clear() дабы избавиться от аннотации Transactional
//  в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с DoctorPatientRestControllerIT#registerPatientInTalon

class EconomistDepartmentRestControllerTest extends ContextIT {

    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    EconomistRepository economistRepository;
    @Autowired
    PasswordEncoder encoder;


    Department initDepartment(String name) {
        return departmentRepository.save(Department.builder()
                .name(name)
                .build());
    }

    Role initRole(String name) {
        return roleRepository.save(Role.builder()
                .name(name)
                .build());
    }
    Economist initEconomist(Role role) {
        Economist economist = new Economist(
                "economist1@email.com",
                String.valueOf("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role
        );
        economist.setPassword(encoder.encode(economist.getPassword()));
        return economistRepository.save(economist);
    }

    @AfterEach
    public void clear() {
        departmentRepository.deleteAll();
        economistRepository.deleteAll();
        roleRepository.deleteAll();
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


        // Проверка наличия в БД трех проинициализированных отделений
        Assertions.assertNotNull(entityManager
                .createQuery("select dep from Department dep where dep.id = :id")
                .setParameter("id", dept1.getId()));
        Assertions.assertNotNull(entityManager
                .createQuery("select dep from Department dep where dep.id = :id")
                .setParameter("id", dept2.getId()));
        Assertions.assertNotNull(entityManager
                .createQuery("select dep from Department dep where dep.id = :id")
                .setParameter("id", dept3.getId()));

    }
}