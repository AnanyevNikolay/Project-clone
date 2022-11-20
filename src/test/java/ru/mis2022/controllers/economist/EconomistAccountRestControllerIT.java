package ru.mis2022.controllers.economist;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.Account;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.entity.Role;
import ru.mis2022.repositories.AccountRepository;
import ru.mis2022.repositories.AppealRepository;
import ru.mis2022.repositories.EconomistRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    @Autowired
    AppealRepository appealRepository;

    Account initAccount(LocalDate dateTo, String name) {
        return accountRepository.save(Account
                .builder()
                .date(dateTo)
                .name(name)
                .build());
    }

    Account initAccount1(boolean isFormed, LocalDate dateTo, String name) {
        return accountRepository.save(Account
                .builder()
                .isFormed(isFormed)
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

    Appeal initAppeal(boolean isClosed, LocalDate localDate, Account account) {
        return appealRepository.save(Appeal
                .builder()
                .isClosed(isClosed)
                .localDate(localDate)
                .account(account)
                .build());
    }

    @AfterEach
    void clear() {
        accountRepository.deleteAll();
        economistRepository.deleteAll();
        roleRepository.deleteAll();
        appealRepository.deleteAll();
    }

    @Test
    public void updateAccount() throws Exception {
        Role roleEconomist = initRole("ECONOMIST");
        Economist economist = initEconomist(roleEconomist);

        LocalDate dateAccount1 = LocalDate.now().plusDays(10); // 2022-11-25
        LocalDate dateAccount2 = LocalDate.now().minusMonths(1); // 2022-10-15

        LocalDate dateAppeal1 = LocalDate.now().plusDays(5);  // 2022-11-20
        LocalDate dateAppeal2 = LocalDate.now().plusDays(10); // 2022-11-25
        LocalDate dateAppeal3 = LocalDate.now().minusDays(30); // 2022-10-16
        LocalDate dateAppeal4 = LocalDate.now().minusMonths(1); // 2022-10-15

        // не сформированный счет
        Account accountNotFormed = initAccount1(false, dateAccount1, "nameTest");
        // сформированный счет, не попадает
        Account accountFormed = initAccount1(true, dateAccount1, "nameTest");
        // не сформированный счет с другой датой
        Account accountNotFormedAndWithAnotherDate = initAccount1(false, dateAccount2, "nameTest");

        // закрытое обращение, не имеющее счета, с датой попадающей в счет - accountNotFormed
        Appeal appeal1 = initAppeal(true, dateAppeal1, null);
        // закрытое обращение, не имеющее счета, с датой на границе, попадающей в счет - accountNotFormed
        Appeal appeal2 = initAppeal(true, dateAppeal2, null);
        // открытое обращение - не должно никуда попасть
        Appeal appeal3 = initAppeal(false, dateAppeal2, null);
        // закрытое обращение с датой на границе, не попадающей ни в один счет
        Appeal appeal5 = initAppeal(true, dateAppeal3, null);
        // закрытое обращение с датой попадающей в другой счет - accountNotFormedAndWithAnotherDate
        Appeal appeal6 = initAppeal(true, dateAppeal4, null);

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

//        Все нормально
        mockMvc.perform(put("/api/economist/account/updateAccount/{accountId}", accountNotFormed.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data.isFormed", Is.is(false)))
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
        ;

        // Проверка на добавление account_id в appeal
        Appeal appealCheck1 = entityManager.createQuery("""
            SELECT a
            FROM Appeal a
            JOIN Account acc
            ON a.account.id = acc.id
            WHERE a.id = :appealId
            """, Appeal.class)
                .setParameter("appealId", appeal1.getId())
                .getSingleResult();

        Assertions.assertEquals(accountNotFormed.getId(), appealCheck1.getAccount().getId());

        //  Проверка на добавление account_id в appeal
        Appeal appealCheck2 = entityManager.createQuery("""
            SELECT a
            FROM Appeal a
            JOIN Account acc
            ON a.account.id = acc.id
            WHERE a.id = :appealId
            """, Appeal.class)
                .setParameter("appealId", appeal2.getId())
                .getSingleResult();

        Assertions.assertEquals(accountNotFormed.getId(), appealCheck2.getAccount().getId());

        mockMvc.perform(put("/api/economist/account/updateAccount/{accountId}", accountNotFormedAndWithAnotherDate.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.data.isFormed", Is.is(false)))
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
        ;

        //  Проверка на связь сущностей по account id (на добавление account_id в appeal)
        Appeal appealCheck3 = entityManager.createQuery("""
            SELECT a
            FROM Appeal a
            JOIN Account acc
            ON a.account.id = acc.id
            WHERE a.id = :appealId
            """, Appeal.class)
                .setParameter("appealId", appeal6.getId())
                .getSingleResult();

        Assertions.assertEquals(accountNotFormedAndWithAnotherDate.getId(), appealCheck3.getAccount().getId());

        // Здесь проверяется, что account_id не присвоился appeal, потому что не подошло по дате
        Appeal appealCheck4 = entityManager.createQuery("""
            SELECT DISTINCT a
            FROM Appeal a
            JOIN Account acc
            ON a.id = :appealId
            """, Appeal.class)
                .setParameter("appealId", appeal5.getId())
                .getSingleResult();

        Assertions.assertNull(appealCheck4.getAccount());

//          Не нормально, счет уже сформирован
        mockMvc.perform(put("/api/economist/account/updateAccount/{accountId}", accountFormed.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code").value(412))
                .andExpect(jsonPath("$.text", Is.is("Счет уже сформирован")))
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
        ;
//          Не норм, счета с таким ID не существует.
        mockMvc.perform(put("/api/economist/account/updateAccount/{accountId}", 9876)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code").value(411))
                .andExpect(jsonPath("$.text", Is.is("Счет не найден")))
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
        ;
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