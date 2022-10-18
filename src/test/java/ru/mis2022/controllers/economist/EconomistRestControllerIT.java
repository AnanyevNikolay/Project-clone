package ru.mis2022.controllers.economist;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.entity.Role;
import ru.mis2022.repositories.EconomistRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo list 6 написать метод clear() дабы избавиться от аннотации Transactional
//  в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с DoctorPatientRestControllerIT#registerPatientInTalon

public class EconomistRestControllerIT extends ContextIT {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    EconomistRepository economistRepository;
    @Autowired
    PasswordEncoder encoder;

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
        economistRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void getCurrentUserTest() throws Exception {
        Role role = initRole("ECONOMIST");
        Economist economist = initEconomist(role);

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        mockMvc.perform(get("/api/economist/mainPage/current")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.roleName", Is.is("ECONOMIST")))
                .andExpect(jsonPath("$.data.lastName", Is.is("l_name")))
                .andExpect(jsonPath("$.data.firstName", Is.is("f_name")))
                .andExpect(jsonPath("$.data.birthday", Matchers.notNullValue()));
        //             .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        Economist currentEconomist =
                entityManager.createQuery("select e from Economist e", Economist.class).getSingleResult();

        // Проверка идентичности сохраненного пользователя
        Assertions.assertEquals(currentEconomist, economist);
    }
}
