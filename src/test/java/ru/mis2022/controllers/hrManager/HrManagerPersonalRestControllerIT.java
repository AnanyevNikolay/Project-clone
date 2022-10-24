package ru.mis2022.controllers.hrManager;


import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.HrManager;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.User;
import ru.mis2022.repositories.HrManagerRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.repositories.UserRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.aspectj.runtime.internal.Conversions.intValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class HrManagerPersonalRestControllerIT extends ContextIT {

    @Autowired
    HrManagerRepository hrManagerRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    Role initRole(String name) {
        return roleRepository.save(Role.builder()
                .name(name)
                .build());
    }

    HrManager initHrManager(Role role) {
        return hrManagerRepository.save(new HrManager(
                "hrManager@email.com",
                passwordEncoder.encode(String.valueOf("1")),
                "Иванов",
                "Иван",
                "Иванович",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    User initUser(String firstName, String lastName, String email, Role role, LocalDate birthday) {
        return userRepository.save(new User(
                email, null, firstName, lastName, null, birthday, role));
    }
    String getFullName(User user) {
        return user.getLastName() + " " + user.getFirstName();
    }

    @AfterEach
    public void clear() {
        hrManagerRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void findUsersByFullName() throws Exception {
        Role roleHrManager = initRole("HR_MANAGER");
        Role rolePatient = initRole("PATIENT");
        HrManager hrManager = initHrManager(roleHrManager);
        User user1 = initUser("Александр", "Александров","email99", roleHrManager, null);
        User user2 = initUser("Николай", "Комаров", "email100", roleHrManager, null);
        User user3 = initUser("Николай", "Васильев","email101", roleHrManager, null);
        User user4 = initUser("Даниил", "Данилов","email102", rolePatient, null);
        User user5 = initUser("Ирина", "Данилова","email103", rolePatient, null);
        User user6 = initUser("Василий", "Прохоров","email104", roleHrManager, null);
        User user7 = initUser("Ирина", "Коробова","email105", roleHrManager, null);
        User user8 = initUser("Василий", "Александров","email106", roleHrManager, null);
        User user9 = initUser("Сергей", "Сергеев","email107", roleHrManager, null);
        User user10 = initUser("Александр", "Коротков","email108", roleHrManager, null);

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);
        //Сортировка осуществляется в последовательности: фамилия - имя - id

        //Вывод списка сотрудников в имени или фамилии которых есть "алек"
        String fullName = "алек";
        mockMvc.perform(get("/api/hr_manager/allUsers")
                        .header("Authorization", accessToken)
                        .param("fullName", fullName)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(3)))
                //Александров Александр
                .andExpect(jsonPath("$.data[0].id", Is.is(intValue(user1.getId()))))
                //Александров Василий
                .andExpect(jsonPath("$.data[1].id", Is.is(intValue(user8.getId()))))
                //Коротков Александр
                .andExpect(jsonPath("$.data[2].id", Is.is(intValue(user10.getId()))));
//               .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Вывод списка сотрудников в фамилии которых есть "ров", в имени "сил"
        String fullName1 = "ров сил";
        mockMvc.perform(get("/api/hr_manager/allUsers")
                        .header("Authorization", accessToken)
                        .param("fullName", fullName1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(2)))
                //Александров Василий
                .andExpect(jsonPath("$.data[0].id", Is.is(intValue(user8.getId()))))
                //Прохоров Василий
                .andExpect(jsonPath("$.data[1].id", Is.is(intValue(user6.getId()))));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Вывод списка сотрудников в фамилии которых есть "ков", в имени "сил"
        String fullName2 = "ков сил";
        mockMvc.perform(get("/api/hr_manager/allUsers")
                        .header("Authorization", accessToken)
                        .param("fullName", fullName2)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(0)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Вывод списка сотрудников(весь список) если передан null,
        //проверка на отсутствие пациентов в списке персонала
        String fullName3 = null;
        mockMvc.perform(get("/api/hr_manager/allUsers")
                        .header("Authorization", accessToken)
                        .param("fullName", fullName3)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(9)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Вывод списка сотрудников(пустой список) если передано более одного пробела
        String fullName4 = "  ";
        mockMvc.perform(get("/api/hr_manager/allUsers")
                        .header("Authorization", accessToken)
                        .param("fullName", fullName4)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(9)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

    }

    @Test
    public void findAllBirthdayInRange() throws Exception {
        Role roleHrManager = initRole("HR_MANAGER");
        Role rolePatient = initRole("PATIENT");
        HrManager hrManager = initHrManager(roleHrManager);
        User user1 = initUser("Александр", "Александров","email99", roleHrManager,
                LocalDate.now().plusDays(28));
        User user2 = initUser("Николай", "Комаров", "email100", roleHrManager,
                LocalDate.now().plusDays(40));
        User user3 = initUser("Николай", "Васильев","email101", roleHrManager,
                LocalDate.now().plusDays(7));
        User user4 = initUser("Даниил", "Данилов","email102", rolePatient,
                LocalDate.now().plusDays(11));
        User user5 = initUser("Ирина", "Данилова","email103", rolePatient,
                LocalDate.now().plusDays(25));
        User user6 = initUser("Василий", "Прохоров","email104", roleHrManager,
                LocalDate.now().plusDays(77));
        User user7 = initUser("Ирина", "Коробова","email105", roleHrManager,
                LocalDate.now().plusDays(3));
        User user8 = initUser("Василий", "Александров","email106", roleHrManager,
                LocalDate.now().plusDays(100));
        User user9 = initUser("Сергей", "Сергеев","email107", roleHrManager,
                LocalDate.now().plusDays(10));
        User user10 = initUser("Александр", "Коротков","email108", roleHrManager,
                LocalDate.now().plusDays(44));

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        //ПОЛУЧАЕМ СПИСОК ИМЕНИННИКОВ НА 20 ДНЕЙ ВПЕРЕД (4)
        mockMvc.perform(get("/api/hr_manager/findBirthdayInRange")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("daysCount", objectMapper.writeValueAsString(20))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(4)))

                //ИВАН ИВАНОВ
                .andExpect(jsonPath("$.data[0].id", Is.is(hrManager.getId().intValue())))
                //ИРИНА КОРОБОВА
                .andExpect(jsonPath("$.data[1].id", Is.is(user7.getId().intValue())))
                //НИКОЛАЙ ВАСИЛЬЕВ
                .andExpect(jsonPath("$.data[2].id", Is.is(user3.getId().intValue())))
                //СЕРГЕЙ СЕРГЕЕВ
                .andExpect(jsonPath("$.data[3].id", Is.is(user9.getId().intValue())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //ПОЛУЧАЕМ СПИСОК ИМЕНИННИКОВ НА ДЕФОЛТНОЕ ЗНАЧЕНИЕ = 30 (5)
        mockMvc.perform(get("/api/hr_manager/findBirthdayInRange")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(5)))

                //ИВАН ИВАНОВ
                .andExpect(jsonPath("$.data[0].id", Is.is(hrManager.getId().intValue())))
                //ИРИНА КОРОБОВА
                .andExpect(jsonPath("$.data[1].id", Is.is(user7.getId().intValue())))
                //НИКОЛАЙ ВАСИЛЬЕВ
                .andExpect(jsonPath("$.data[2].id", Is.is(user3.getId().intValue())))
                //СЕРГЕЙ СЕРГЕЕВ
                .andExpect(jsonPath("$.data[3].id", Is.is(user9.getId().intValue())))
                //АЛЕКСАНДР АЛЕКСАНДРОВ
                .andExpect(jsonPath("$.data[4].id", Is.is(user1.getId().intValue())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }
}
