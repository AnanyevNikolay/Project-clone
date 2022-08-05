package ru.mis2022.controllers.economist;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.entity.Role;
import ru.mis2022.service.entity.EconomistService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.util.ContextIT;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EconomistRestControllerIT extends ContextIT {

    @Autowired RoleService roleService;
    @Autowired EconomistService economistService;

    Role initRole(String name) {
        return roleService.persist(Role.builder()
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
    }
}
