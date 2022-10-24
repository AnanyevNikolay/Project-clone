package ru.mis2022.controllers.hrManager;


import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.dto.administrator.AdministratorDto;
import ru.mis2022.models.dto.administrator.converter.AdministratorDtoConverter;
import ru.mis2022.models.entity.Administrator;
import ru.mis2022.models.entity.HrManager;
import ru.mis2022.models.entity.Invite;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.User;
import ru.mis2022.repositories.AdministratorRepository;
import ru.mis2022.repositories.HrManagerRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.service.entity.MailService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class HrManagerAdminRestControllerIT extends ContextIT {
    @Autowired
    HrManagerRepository hrManagerRepository;
    @Autowired
    AdministratorRepository administratorRepository;
    @Autowired
    AdministratorDtoConverter administratorDtoConverter;
    @Autowired
    RoleRepository roleRepository;
    @MockBean
    MailService mailService;

    Role initRole(String name) {
        return roleRepository.save(Role.builder()
                .name(name)
                .build());
    }

    HrManager initHrManager(Role role) {
        return hrManagerRepository.save(new HrManager(
                "hrManager@email.com",
                passwordEncoder.encode(String.valueOf("1")),
                "f_name",
                "l_name",
                "surName",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    Administrator initAdministrator(Role role) {
        return administratorRepository.save(new Administrator(
                "administrator@email.com",
                passwordEncoder.encode(String.valueOf("1")),
                "f_name",
                "l_name",
                "surName",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    AdministratorDto initAdministratorDto(Long id, String email) {
        return new AdministratorDto(
                id,
                email,
                String.valueOf("2"),
                "f_name",
                "l_name",
                "surName",
                "01.01.1980",
                "ADMIN"
        );
    }

    @AfterEach
    public void clear() {
        hrManagerRepository.deleteAll();
        administratorRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void hrManagerCreateAdmin() throws Exception {
        Role roleHrManager = initRole("HR_MANAGER");
        HrManager hrManager = initHrManager(roleHrManager);
        Role roleAdministrator = initRole("ADMIN");
        AdministratorDto validDtoCreate = initAdministratorDto(null, "administrator2@email.com");
        AdministratorDto noValidIdDtoCreate = initAdministratorDto((long) 2, "administrator2@email.com");
        AdministratorDto noValidEmailDtoCreate = initAdministratorDto(null, "123456");
        AdministratorDto noValidExistEmailDtoCreate = initAdministratorDto(null, "administrator2@email.com");

        Mockito.doNothing().when(mailService).sendRegistrationInviteByEmail(Mockito.any(Invite.class), Mockito.any(User.class));

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);
        // Валидный ДТО администратора, создание администратора
        mockMvc.perform(post("/api/hr_manager/admin/createAdmin")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(validDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.id", Is.is(Matchers.notNullValue())))
                .andExpect(jsonPath("$.data.email", Is.is("administrator2@email.com")))
                .andExpect(jsonPath("$.data.lastName", Is.is("l_name")))
                .andExpect(jsonPath("$.data.firstName", Is.is("f_name")))
                .andExpect(jsonPath("$.data.surname", Is.is("surName")))
                .andExpect(jsonPath("$.data.role", Is.is("ADMIN")))
                .andExpect(jsonPath("$.data.birthday", Is.is("01.01.1980")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Не валидный id в ДТО администратора, создание администратора
        mockMvc.perform(post("/api/hr_manager/admin/createAdmin")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidIdDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(400)))
                .andExpect(jsonPath("$.text", Is.is("id должен быть равен null")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Не валидный email в ДТО администратора, создание администратора
        mockMvc.perform(post("/api/hr_manager/admin/createAdmin")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidEmailDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(400)))
                .andExpect(jsonPath("$.text", Is.is(
                        "email должен быть корректным адресом электронной почты")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Существующий email в ДТО администратора, создание администратора
        mockMvc.perform(post("/api/hr_manager/admin/createAdmin")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidExistEmailDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(412)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Такой адрес электронной почты уже используется!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

    }

    @Test
    public void hrManagerUpdateAdmin() throws Exception {
        Role roleHrManager = initRole("HR_MANAGER");
        HrManager hrManager = initHrManager(roleHrManager);
        Role roleAdministrator = initRole("ADMIN");
        Administrator administrator = initAdministrator(roleAdministrator);
        AdministratorDto validDtoUpdate = initAdministratorDto(administrator.getId(), "administrator3@email.com");
        AdministratorDto noValidIdDtoUpdate = initAdministratorDto(4567876L, "administrator3@email.com");
        AdministratorDto noValidExistIdDtoUpdate = initAdministratorDto((long) 100, "administrator3@email.com");
        AdministratorDto noValidEmailDtoUpdate = initAdministratorDto(administrator.getId(), "123456");

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        // Валидный ДТО администратора, обновление администратора
        mockMvc.perform(put("/api/hr_manager/admin/updateAdmin")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(validDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.id", Is.is(Matchers.notNullValue())))
                .andExpect(jsonPath("$.data.email", Is.is("administrator3@email.com")))
                .andExpect(jsonPath("$.data.lastName", Is.is("l_name")))
                .andExpect(jsonPath("$.data.firstName", Is.is("f_name")))
                .andExpect(jsonPath("$.data.surname", Is.is("surName")))
                .andExpect(jsonPath("$.data.role", Is.is("ADMIN")))
                .andExpect(jsonPath("$.data.birthday", Is.is("01.01.1980")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Не валидный id в ДТО администратора, обновление администратора
        mockMvc.perform(put("/api/hr_manager/admin/updateAdmin")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidIdDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(410)))
                .andExpect(jsonPath("$.text", Is.is(
                        "По переданному id администратор не найден.")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Не валидный email в ДТО администратора, обновление администратора
        mockMvc.perform(put("/api/hr_manager/admin/updateAdmin")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidEmailDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(400)))
                .andExpect(jsonPath("$.text", Is.is(
                        "email должен быть корректным адресом электронной почты")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Не существующий id в ДТО администратора, обновление администратора
        mockMvc.perform(put("/api/hr_manager/admin/updateAdmin")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidExistIdDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(410)))
                .andExpect(jsonPath("$.text", Is.is(
                        "По переданному id администратор не найден.")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

    }

}
