package ru.mis2022.controllers.economist;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.dto.economist.DatesToCheckDto;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.entity.MedicalService;
import ru.mis2022.models.entity.PriceOfMedicalService;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.Yet;
import ru.mis2022.service.entity.EconomistService;
import ru.mis2022.service.entity.MedicalServiceService;
import ru.mis2022.service.entity.PriceOfMedicalServiceService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.service.entity.YetService;
import ru.mis2022.util.ContextIT;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EconomistCheckRestControllerIT extends ContextIT {
    PriceOfMedicalServiceService priceOfMedicalServiceService;
    YetService yetService;
    EconomistService economistService;
    RoleService roleService;
    MedicalServiceService medicalServiceService;

    @Autowired
    public EconomistCheckRestControllerIT(YetService yetService,
                                          EconomistService economistService,
                                          RoleService roleService,
                                          PriceOfMedicalServiceService priceOfMedicalServiceService,
                                          MedicalServiceService medicalServiceService) {
        this.yetService = yetService;
        this.economistService = economistService;
        this.roleService = roleService;
        this.priceOfMedicalServiceService = priceOfMedicalServiceService;
        this.medicalServiceService = medicalServiceService;
    }

    Role initRole(String name) {
        return roleService.save(new Role(name));
    }

    Economist initEconomist(Role role, String email) {
        return economistService.persist(new Economist(
                email,
                "economistPassword",
                "economistFirstName",
                "economistLastName",
                "economistSurname",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    Yet initYet(LocalDate dayFrom, LocalDate dayTo) {
        return yetService.save(new Yet(
                70.00,
                dayFrom,
                dayTo
        ));
    }

    PriceOfMedicalService initPriceOfMedicalService(LocalDate dayFrom, LocalDate dayTo, MedicalService medicalService) {
        return priceOfMedicalServiceService.save(new PriceOfMedicalService(
                BigDecimal.valueOf(1.0),
                dayFrom,
                dayTo,
                medicalService
        ));
    }

    MedicalService initMedicalService() {
        return medicalServiceService.save(new MedicalService(
                "identifier",
                "medicalService"
        ));
    }

    @AfterEach
    void clear() {
        economistService.deleteAll();
        roleService.deleteAll();
        yetService.deleteAll();
        priceOfMedicalServiceService.deleteAll();
        medicalServiceService.deleteAll();
    }


    @Test
    public void checkPricesByYetTest() throws Exception {
        MedicalService medicalService = initMedicalService();

        Role economistRole = initRole("ECONOMIST");
        Economist economist = initEconomist(economistRole, "economist@mail.com");

        LocalDate dateFrom1 = LocalDate.of(2022, 1, 1);
        LocalDate dateTo1 = LocalDate.of(2022, 2, 1);
        LocalDate dateFrom2 = LocalDate.of(2022, 2, 1);
        LocalDate dateTo2 = LocalDate.of(2022, 3, 1);
        LocalDate dateFrom3 = LocalDate.of(2022, 3, 1);
        LocalDate dateTo3 = LocalDate.of(2022, 4, 1);

        initYet(dateFrom1, dateTo1);
        initYet(dateFrom2, dateTo2);
        initYet(dateFrom3, dateTo3);

        initPriceOfMedicalService(dateFrom1.plusDays(5), dateTo1.minusDays(20), medicalService);
        initPriceOfMedicalService(dateFrom1.plusDays(25), dateTo2.minusDays(25), medicalService);
        initPriceOfMedicalService(dateFrom2.plusDays(27), dateTo3.minusDays(20), medicalService);

        // Сохраняем три уеты и три цены без наложений, ждем код 200

        DatesToCheckDto datesToCheckDto = new DatesToCheckDto(dateFrom1, dateTo3);
        accessToken = tokenUtil.obtainNewAccessToken(economist.getUsername(), "economistPassword", mockMvc);
        mockMvc.perform(post("/api/economist/yetChecks/datesOverlap")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(datesToCheckDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data", IsNull.nullValue()))
                .andExpect(jsonPath("$.code", Is.is(200)))
//                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
        ;

        yetService.deleteAll();
        priceOfMedicalServiceService.deleteAll();

        initYet(dateFrom1, dateTo1);
        initYet(dateFrom1, dateTo1);
        initYet(dateFrom2, dateTo2);

        initPriceOfMedicalService(dateFrom1.plusDays(5), dateTo1.minusDays(20), medicalService);
        initPriceOfMedicalService(dateFrom1.plusDays(25), dateTo2.minusDays(25), medicalService);
        initPriceOfMedicalService(dateFrom2.plusDays(10), dateTo2.minusDays(10), medicalService);

        // Сохраняем наложение ует, цены не накладываются, ждем 409

        mockMvc.perform(post("/api/economist/yetChecks/datesOverlap")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(datesToCheckDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.data", IsNull.nullValue()))
                .andExpect(jsonPath("$.code", Is.is(409)))
                .andExpect(jsonPath("$.text", Is.is("Есть наложение УЕТ друг на друга или наложение цены услуг")))
//                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
        ;

        yetService.deleteAll();
        priceOfMedicalServiceService.deleteAll();

        initYet(dateFrom1, dateTo1);
        initYet(dateFrom2, dateTo2);
        initYet(dateFrom3, dateTo3);

        initPriceOfMedicalService(dateFrom1.plusDays(5), dateFrom1.plusDays(10), medicalService);
        initPriceOfMedicalService(dateFrom1.plusDays(9), dateFrom1.plusDays(20), medicalService);
        initPriceOfMedicalService(dateFrom2.plusDays(9), dateTo2.plusDays(20), medicalService);

        // сохраняем нормальную ует, и цены накладываются, ждем 409

        mockMvc.perform(post("/api/economist/yetChecks/datesOverlap")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(datesToCheckDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.data", IsNull.nullValue()))
                .andExpect(jsonPath("$.code", Is.is(409)))
                .andExpect(jsonPath("$.text", Is.is("Есть наложение УЕТ друг на друга или наложение цены услуг")))
//                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
        ;

        yetService.deleteAll();
        priceOfMedicalServiceService.deleteAll();

        LocalDate dayFromWithSpace = LocalDate.of(2022, 4, 1);
        LocalDate dayToWithSpace = LocalDate.of(2022, 5, 1);

        initYet(dateFrom1, dateTo1);
        initYet(dateFrom2, dateTo2);
        initYet(dayFromWithSpace, dayToWithSpace);

        initPriceOfMedicalService(dateFrom1.plusDays(5), dateTo1.minusDays(20), medicalService);
        initPriceOfMedicalService(dateFrom2.minusDays(5), dateTo2.minusDays(25), medicalService);
        initPriceOfMedicalService(dateFrom3.minusDays(3), dateTo3.minusDays(20), medicalService);

        datesToCheckDto = new DatesToCheckDto(dateFrom1, dayToWithSpace);

        // Сохраняем ует с пробелом и ценой в этот пробел, ждем 417

        mockMvc.perform(post("/api/economist/yetChecks/datesOverlap")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(datesToCheckDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.data", IsNull.nullValue()))
                .andExpect(jsonPath("$.code", Is.is(417)))
                .andExpect(jsonPath("$.text", Is.is("Есть действующие услуги, когда УЕТ не задано")))
//                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
        ;

        yetService.deleteAll();
        priceOfMedicalServiceService.deleteAll();

        Yet yet = initYet(dateFrom3, dateTo3);
        initYet(dateFrom2, dateTo2);
        initYet(dateFrom1, dateTo1);

        PriceOfMedicalService price = initPriceOfMedicalService(dateFrom2.plusDays(27), dateTo3.minusDays(20), medicalService);
        initPriceOfMedicalService(dateFrom1.plusDays(25), dateTo2.minusDays(25), medicalService);
        initPriceOfMedicalService(dateFrom1.plusDays(5), dateTo1.minusDays(20), medicalService);

        // Цены и ует нормальные, но сохранены в обратом порядке, ожидаем 200

        mockMvc.perform(post("/api/economist/yetChecks/datesOverlap")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(datesToCheckDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data", IsNull.nullValue()))
                .andExpect(jsonPath("$.code", Is.is(200)))
//                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
        ;


        // Ручное удаление

        Economist economistQry = entityManager.createQuery("""
                        SELECT e
                        FROM Economist e
                            LEFT JOIN FETCH e.role
                        WHERE e.id = :id
                        """, Economist.class)
                .setParameter("id", economist.getId())
                .getSingleResult();

        Assertions.assertEquals(economistQry.getId(), economist.getId());

        Yet yetQry = entityManager.createQuery("""
                        SELECT y
                        FROM Yet y
                        WHERE y.id = :id
                        """, Yet.class)
                .setParameter("id", yet.getId())
                .getSingleResult();
        Assertions.assertEquals(yetQry.getId(), yet.getId());

        PriceOfMedicalService priceQry = entityManager.createQuery("""
                        SELECT pm
                        FROM PriceOfMedicalService pm
                            LEFT JOIN FETCH pm.medicalService
                        WHERE pm.id = :id
                        """, PriceOfMedicalService.class)
                .setParameter("id", price.getId())
                .getSingleResult();
        Assertions.assertEquals(priceQry.getId(), price.getId());

    }

}
