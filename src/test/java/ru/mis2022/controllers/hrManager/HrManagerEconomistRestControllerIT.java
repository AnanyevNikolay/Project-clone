package ru.mis2022.controllers.hrManager;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import ru.mis2022.models.dto.economist.EconomistDto;
import ru.mis2022.models.dto.economist.converter.EconomistDtoConverter;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.entity.HrManager;
import ru.mis2022.models.entity.Invite;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.User;
import ru.mis2022.repositories.EconomistRepository;
import ru.mis2022.repositories.HrManagerRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.service.entity.MailService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HrManagerEconomistRestControllerIT extends ContextIT {
    @Autowired
    HrManagerRepository hrManagerRepository;
    @Autowired
    EconomistRepository economistRepository;
    @Autowired
    EconomistDtoConverter economistDtoConverter;
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
                passwordEncoder.encode("1"),
                "f_name",
                "l_name",
                "surName",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    EconomistDto initEconomistDto(Long id, String email) {
        return new EconomistDto(
                id,
                email,
                "2",
                "f_name",
                "l_name",
                "surName",
                "01.01.1980",
                "ECONOMIST"
        );
    }

    @AfterEach
    public void clear() {
        hrManagerRepository.deleteAll();
        economistRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void hrManagerCreateEconomistTest() throws Exception {
        Role roleHrManager = initRole("HR_MANAGER");
        HrManager hrManager = initHrManager(roleHrManager);
        initRole("ECONOMIST");
        EconomistDto validDtoCreate = initEconomistDto(null, "economist2@email.com");
        EconomistDto noValidIdDtoCreate = initEconomistDto((long) 2, "economist2@email.com");
        EconomistDto noValidEmailDtoCreate = initEconomistDto(null, "123456");
        EconomistDto noValidExistEmailDtoCreate = initEconomistDto(null, "economist2@email.com");

        Mockito.doNothing().when(mailService).sendRegistrationInviteByEmail(Mockito.any(Invite.class), Mockito.any(User.class));

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        // Валидный ДТО экономиста, создание экономиста
        mockMvc.perform(post("/api/hr_manager/economist/createEconomist")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(validDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.id", Is.is(Matchers.notNullValue())))
                .andExpect(jsonPath("$.data.email", Is.is("economist2@email.com")))
                .andExpect(jsonPath("$.data.lastName", Is.is("l_name")))
                .andExpect(jsonPath("$.data.firstName", Is.is("f_name")))
                .andExpect(jsonPath("$.data.surname", Is.is("surName")))
                .andExpect(jsonPath("$.data.role", Is.is("ECONOMIST")))
                .andExpect(jsonPath("$.data.birthday", Is.is("01.01.1980")));

        //Не валидный id в ДТО экономиста, создание экономиста
        mockMvc.perform(post("/api/hr_manager/economist/createEconomist")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidIdDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(400)))
                .andExpect(jsonPath("$.text", Is.is("id должен быть равен null")));

        //Не валидный email в ДТО экономиста, создание экономиста
        mockMvc.perform(post("/api/hr_manager/economist/createEconomist")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidEmailDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(400)))
                .andExpect(jsonPath("$.text", Is.is(
                        "email должен быть корректным адресом электронной почты")));

        //Существующий email в ДТО экономиста, создание экономиста
        mockMvc.perform(post("/api/hr_manager/economist/createEconomist")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidExistEmailDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(412)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Такой адрес электронной почты уже используется!")));

    }

}
