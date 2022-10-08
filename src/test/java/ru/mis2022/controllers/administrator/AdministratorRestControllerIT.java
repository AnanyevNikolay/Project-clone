package ru.mis2022.controllers.administrator;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Administrator;
import ru.mis2022.models.entity.Role;
import ru.mis2022.service.entity.AdministratorService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.util.ContextIT;
import ru.mis2022.utils.GenerateRandomString;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo list 4 написать метод clear() дабы избавиться от аннотации Transactional
//  в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с DoctorPatientRestControllerIT#registerPatientInTalon
@Transactional
public class AdministratorRestControllerIT extends ContextIT {

    RoleService roleService;
    AdministratorService administratorService;
    @MockBean
    GenerateRandomString generator;
    @Value("15")
    int randomPasswordLength;

    @Autowired
    public AdministratorRestControllerIT(RoleService roleService, AdministratorService administratorService) {
        this.roleService = roleService;
        this.administratorService = administratorService;
    }

    Role initRole(String name) {
        return roleService.save(Role.builder()
                .name(name)
                .build());
    }

    Administrator initAdministrator(Role role) throws Exception {
        return administratorService.persist(new Administrator(
                "administrator1@email.com",
                "1",
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    @Test
    public void getCurrentUserTest() throws Exception {
        Mockito.when(generator.getRndStr(randomPasswordLength)).thenReturn("12345");

        Role role = initRole("ADMIN");
        Administrator administrator = initAdministrator(role);
        accessToken = tokenUtil.obtainNewAccessToken(administrator.getEmail(), "12345", mockMvc);

        mockMvc.perform(get("/api/administrator/mainPage/current")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.roleName", Is.is("ADMIN")))
                .andExpect(jsonPath("$.data.lastName", Is.is("l_name")))
                .andExpect(jsonPath("$.data.firstName", Is.is("f_name")))
                .andExpect(jsonPath("$.data.birthday", Matchers.notNullValue()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }


}
