package ru.mis2022.controllers.economist;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import ru.mis2022.models.entity.Account;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.entity.Role;
import ru.mis2022.repositories.AccountRepository;
import ru.mis2022.repositories.EconomistRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.models.entity.Role.RolesEnum.ECONOMIST;
import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;

public class EconomistAccountRestControllerIT extends ContextIT {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    EconomistRepository economistRepository;
    @Autowired
    RoleRepository roleRepository;

    Account initAccount(LocalDate dateTo, String name) {
        return accountRepository.save(Account
                .builder()
                .date(dateTo)
                .name(name)
                .build());
    }

    Economist initEconomist(Role role) {
        return economistRepository.save(new Economist(
                "economist1@email.com",
                passwordEncoder.encode("1"),
                "firstName",
                "lastName",
                "surname",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    Role initRole(String roleName) {
        return roleRepository.save(Role
                .builder()
                .name(roleName)
                .build());
    }

    @AfterEach
    void clear() {
        accountRepository.deleteAll();
        economistRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void createEmptyAccountTest() throws Exception {
        Role roleEconomist = initRole(ECONOMIST.name());
        Economist economist = initEconomist(roleEconomist);
        LocalDate dateTo1 = LocalDate.of(2020, 6, 4);
        Account account = initAccount(dateTo1, "name1");

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        // Нормальный сценарий!
        mockMvc.perform(post("/api/economist/account/create")
                        .param("lastDate", dateTo1.format(DATE_FORMATTER))
                        .param("name", "name1")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data.name").value(account.getName()))
                .andExpect(jsonPath("$.data.data").value(account.getDate().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data.money").value(Matchers.nullValue()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        Economist qryEconomist = entityManager.createQuery("""
                        SELECT e
                        FROM Economist e
                        LEFT JOIN Role r
                            ON e.role.id = r.id
                        WHERE r.id = :id
                        """, Economist.class)
                .setParameter("id", roleEconomist.getId())
                .getSingleResult();

        Assertions.assertEquals(qryEconomist.getId(), economist.getId());
        Assertions.assertEquals(qryEconomist.getRole().getId(), roleEconomist.getId());

    }

    @Test
    public void getAccountsByRangeDataOrGetAllAccountsTest() throws Exception {
        Role roleEconomist = initRole(ECONOMIST.name());
        Economist economist = initEconomist(roleEconomist);

        LocalDate dateFrom1 = LocalDate.now().minusDays(10);
        LocalDate dateTo1 = LocalDate.now();

        Account account1 = initAccount(LocalDate.now().minusDays(9), "name1");
        Account account2 = initAccount(LocalDate.now().minusDays(8), "name2");

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        // Нормальный сценарий, две даты заданы!
        mockMvc.perform(get("/api/economist/account/getByRangeDataOrGetAllAccounts")
                        .param("dateFrom", dateFrom1.format(DATE_FORMATTER))
                        .param("dateTo", dateTo1.format(DATE_FORMATTER))
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.size()").value(2))

                .andExpect(jsonPath("$.data[0].id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[0].name").value(account1.getName()))
                .andExpect(jsonPath("$.data[0].data").value(account1.getDate().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data[0].money").value(Matchers.nullValue()))

                .andExpect(jsonPath("$.data[1].id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[1].name").value(account2.getName()))
                .andExpect(jsonPath("$.data[1].data").value(account2.getDate().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data[1].money").value(Matchers.nullValue()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        LocalDate dateTo2 = LocalDate.now().minusDays(1);
        Account account3 = initAccount(LocalDate.now().minusDays(7), "name3");

        // Задана только вторая дата, Нормальный сценарий
        mockMvc.perform(get("/api/economist/account/getByRangeDataOrGetAllAccounts")
                        .param("dateTo", dateTo2.format(DATE_FORMATTER))
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.size()").value(3))

                .andExpect(jsonPath("$.data[0].id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[0].name").value(account1.getName()))
                .andExpect(jsonPath("$.data[0].data").value(account1.getDate().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data[0].money").value(Matchers.nullValue()))

                .andExpect(jsonPath("$.data[1].id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[1].name").value(account2.getName()))
                .andExpect(jsonPath("$.data[1].data").value(account2.getDate().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data[1].money").value(Matchers.nullValue()))

                .andExpect(jsonPath("$.data[2].id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[2].name").value(account3.getName()))
                .andExpect(jsonPath("$.data[2].data").value(account3.getDate().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data[2].money").value(Matchers.nullValue()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        LocalDate dateFrom2 = LocalDate.now().minusDays(7);
        Account account4 = initAccount(LocalDate.now().minusDays(6), "name4");

        // Задана только первая дата. Нормальный сценарий
        mockMvc.perform(get("/api/economist/account/getByRangeDataOrGetAllAccounts")
                        .param("dateFrom", dateFrom2.format(DATE_FORMATTER))
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.size()").value(2))

                .andExpect(jsonPath("$.data[0].id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[0].name").value(account3.getName()))
                .andExpect(jsonPath("$.data[0].data").value(account3.getDate().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data[0].money").value(Matchers.nullValue()))

                .andExpect(jsonPath("$.data[1].id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[1].name").value(account4.getName()))
                .andExpect(jsonPath("$.data[1].data").value(account4.getDate().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data[1].money").value(Matchers.nullValue()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Ни одна дата не задана, Нормальный сценарий!
        mockMvc.perform(get("/api/economist/account/getByRangeDataOrGetAllAccounts")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.size()").value(4))

                .andExpect(jsonPath("$.data[0].id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[0].name").value(account1.getName()))
                .andExpect(jsonPath("$.data[0].data").value(account1.getDate().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data[0].money").value(Matchers.nullValue()))

                .andExpect(jsonPath("$.data[1].id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[1].name").value(account2.getName()))
                .andExpect(jsonPath("$.data[1].data").value(account2.getDate().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data[1].money").value(Matchers.nullValue()))

                .andExpect(jsonPath("$.data[2].id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[2].name").value(account3.getName()))
                .andExpect(jsonPath("$.data[2].data").value(account3.getDate().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data[2].money").value(Matchers.nullValue()))

                .andExpect(jsonPath("$.data[3].id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[3].name").value(account4.getName()))
                .andExpect(jsonPath("$.data[3].data").value(account4.getDate().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data[3].money").value(Matchers.nullValue()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        LocalDate dateFrom3 = LocalDate.now().plusDays(1);
        LocalDate dateTo3 = LocalDate.now().minusDays(1);

        // Неверная последовательность указанных дат. 417 код
        mockMvc.perform(get("/api/economist/account/getByRangeDataOrGetAllAccounts")
                        .param("dateFrom", dateFrom3.format(DATE_FORMATTER))
                        .param("dateTo", dateTo3.format(DATE_FORMATTER))
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(422)))
                .andExpect(jsonPath("$.text", Is.is("Неверная последовательность указанных дат.")));

        LocalDate dateFrom4 = LocalDate.now().minusYears(5);
        LocalDate dateTo4 = LocalDate.now().minusYears(2);

        // Нормальный сценарий, ни одна сущность не найдена!
        mockMvc.perform(get("/api/economist/account/getByRangeDataOrGetAllAccounts")
                        .param("dateFrom", dateFrom4.format(DATE_FORMATTER))
                        .param("dateTo", dateTo4.format(DATE_FORMATTER))
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.size()").value(0));

        Economist qryEconomist = entityManager.createQuery("""
                        SELECT e
                        FROM Economist e
                        LEFT JOIN Role r
                            ON e.role.id = r.id
                        WHERE r.id = :id
                        """, Economist.class)
                .setParameter("id", roleEconomist.getId())
                .getSingleResult();

        Assertions.assertEquals(qryEconomist.getId(), economist.getId());
        Assertions.assertEquals(qryEconomist.getRole().getId(), roleEconomist.getId());

    }

}
