package ru.mis2022.controllers;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.dto.user.UserPasswordChangingDto;
import ru.mis2022.models.entity.Administrator;
import ru.mis2022.models.entity.Invite;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.User;
import ru.mis2022.service.entity.AdministratorService;
import ru.mis2022.service.entity.InviteService;
import ru.mis2022.service.entity.MailService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class AuthControllerIT extends ContextIT {

    RoleService roleService;
    AdministratorService administratorService;
    InviteService inviteService;
    @MockBean
    MailService mailService;

    @Autowired
    public AuthControllerIT(RoleService roleService, AdministratorService administratorService, InviteService inviteService) {
        this.roleService = roleService;
        this.administratorService = administratorService;
        this.inviteService = inviteService;
    }

    Role initRole(String roleName) {
        return roleService.save(Role.builder()
                .name(roleName)
                .build());
    }

    Administrator initAdmin(String email, Role role) {
        return administratorService.persist(new Administrator(
                email,
                "adminPassword",
                "adminFirstName",
                "adminLastName",
                "adminSurname",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    Invite initInvite(User user) {
        return inviteService.persist(user);
    }

    @AfterEach
    public void clear() {
        inviteService.deleteAll();
        administratorService.deleteAll();
        roleService.deleteAll();
    }

    // todo list 10 без аннотации Transactional над этим классом метод initInvite падает
    //  с ошибкой о том, что detached сущность была передана на persist
    //  понять почему это происходит и попытаться исправить, дабы убрать аннотацию Transactional
    @Test
    public void confirmEmailPasswordTest() throws Exception {
        Role adminRole = initRole("ADMIN");

        // Тест подтверждения по почте, ожидаем код 200 и регистрацию
        Administrator administrator1 = initAdmin("admin@mail.com", adminRole);
        Invite invite1 = initInvite(administrator1);
        UserPasswordChangingDto userPasswordChangingDto1 = new UserPasswordChangingDto(invite1.getToken(), "admpassadmpass123");

        mockMvc.perform(post("/api/auth/confirm/emailpassword")
                        .content(objectMapper.writeValueAsString(userPasswordChangingDto1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.id", Is.is(administrator1.getId().intValue())))
                .andExpect(jsonPath("$.data.firstName", Is.is(administrator1.getFirstName())))
                .andExpect(jsonPath("$.data.lastName", Is.is(administrator1.getLastName())))
                .andExpect(jsonPath("$.data.surName", Is.is(administrator1.getSurname())))
                .andExpect(jsonPath("$.data.birthday", Is.is(administrator1.getBirthday().toString())))
                .andExpect(jsonPath("$.data.email", Is.is(administrator1.getEmail())))
                .andExpect(jsonPath("$.data.roleName", Is.is(administrator1.getRole().getName())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Тест пароль меньше 10 символов, ждем 410
        Administrator administrator2 = initAdmin("admin2@mail.com", adminRole);
        Invite invite2 = initInvite(administrator2);
        UserPasswordChangingDto userPasswordChangingDto2 = new UserPasswordChangingDto(invite2.getToken(), "123");

        mockMvc.perform(post("/api/auth/confirm/emailpassword")
                        .content(objectMapper.writeValueAsString(userPasswordChangingDto2))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(410)))
                .andExpect(jsonPath("$.data", IsNull.nullValue()))
                .andExpect(jsonPath("$.text", Is.is("Пароль менее 10 символов")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Тест устаревшей ссылки, ожидаем 415
        Administrator administrator3 = initAdmin("admin3@mail.com", adminRole);
        Invite invite3 = initInvite(administrator3);
        invite3.setExpirationDate(LocalDateTime.now().minusYears(1));
        inviteService.save(invite3);
        UserPasswordChangingDto userPasswordChangingDto3 = new UserPasswordChangingDto(invite3.getToken(), "admpassadmpass123");

        mockMvc.perform(post("/api/auth/confirm/emailpassword")
                        .content(objectMapper.writeValueAsString(userPasswordChangingDto3))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(415)))
                .andExpect(jsonPath("$.data", IsNull.nullValue()))
                .andExpect(jsonPath("$.text", Is.is("Ссылка устарела")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Работает без аннотации @Transactional
        Invite qryInvite = entityManager.createQuery("""
                        SELECT i
                        FROM Invite i
                        LEFT JOIN User u
                            ON u.id = i.id
                        WHERE i.id = :id
                        """, Invite.class)
                .setParameter("id", invite2.getId())
                .getSingleResult();

        Assertions.assertEquals(qryInvite.getUser().getId(), administrator2.getId());

    }

    @Test
    public void passwordRecoveryForAnyUserTest() throws Exception {
        Role adminRole = initRole("ADMIN");
        Administrator administrator1 = initAdmin("admin1@mail.com", adminRole);

        // Юзер с таким email существует
        mockMvc.perform(post("/api/auth/passwordRecovery")
                        .content(administrator1.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)));

        // инвайт создан
        verify(mailService).sendRegistrationInviteByEmail(any(Invite.class), any(User.class));
    }

}
