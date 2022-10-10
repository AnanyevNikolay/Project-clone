package ru.mis2022.controllers.economist;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.dto.service.MedicalServiceDto;
import ru.mis2022.models.dto.service.PriceOfMedicalServiceDto;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.MedicalService;
import ru.mis2022.models.entity.PriceOfMedicalService;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.EconomistService;
import ru.mis2022.service.entity.MedicalServiceService;
import ru.mis2022.service.entity.PriceOfMedicalServiceService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.util.ContextIT;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;

public class EconomistMedicalServiceRestControllerIT extends ContextIT {

    @Autowired
    DepartmentService departmentService;

    @Autowired
    RoleService roleService;

    @Autowired
    EconomistService economistService;

    @Autowired
    MedicalServiceService medicalServiceService;

    @Autowired
    PriceOfMedicalServiceService priceOfMedicalServiceService;

    Role initRole(String name) {
        return roleService.save(Role.builder()
                .name(name)
                .build());
    }

    Economist initEconomist(Role role) {
        return economistService.persist(new Economist(
                "economist@email.com",
                String.valueOf("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    @AfterEach
    void clear() {
        priceOfMedicalServiceService.deleteAll();
        medicalServiceService.deleteAll();
        economistService.deleteAll();
        roleService.deleteAll();
        departmentService.deleteAll();
    }

    MedicalService initMedicalService(String identifier, String name) {
        return medicalServiceService.save(MedicalService.builder()
                .identifier(identifier)
                .name(name)
                .build());
    }

    @Test
    public void persistMedicalServiceTest() throws Exception {
        Role role = initRole("ECONOMIST");
        Economist economist = initEconomist(role);

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        //валидный тест
        MedicalServiceDto dto1 = MedicalServiceDto.builder()
                .identifier("K12")
                .name("Обследование")
                .build();
        mockMvc.perform(post("/api/economist/medicalService/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dto1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data.identifier", Is.is(dto1.identifier())))
                .andExpect(jsonPath("$.data.name", Is.is(dto1.name())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //в базе уже есть мед.услуга с таким идентификатором
        MedicalServiceDto dto2 = MedicalServiceDto.builder()
                .identifier("K12")
                .name("Лечение")
                .build();
        mockMvc.perform(post("/api/economist/medicalService/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dto2))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(410)))
                .andExpect(jsonPath("$.text", Is.is("Медицинская услуга с данным  идентификатором уже существует")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //в базе уже есть мед.услуга с таким именем
        MedicalServiceDto dto3 = MedicalServiceDto.builder()
                .identifier("X12")
                .name("Обследование")
                .build();
        mockMvc.perform(post("/api/economist/medicalService/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dto3))
                        .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(412)))
                .andExpect(jsonPath("$.text", Is.is("Медицинская услуга с таким именем уже существует")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        MedicalService qryMedicalService = entityManager.createQuery("""
                        SELECT ms
                        FROM MedicalService ms
                            WHERE ms.identifier = :identifier
                        """, MedicalService.class)
                .setParameter("identifier", dto1.identifier())
                .getSingleResult();

        Assertions.assertEquals(qryMedicalService.getIdentifier(), dto1.identifier());

        Economist qryEconomist = entityManager.createQuery("""
                        SELECT econ
                        FROM Economist econ
                        WHERE econ.id = :econId
                        """, Economist.class)
                .setParameter("econId", economist.getId())
                .getSingleResult();

        Assertions.assertEquals(qryEconomist.getId(), economist.getId());
    }

    @Test
    public void setMedicalServicePriceTest() throws Exception {
        Role role = initRole("ECONOMIST");
        MedicalService medicalService = initMedicalService("identifier", "name");
        Economist economist = initEconomist(role);

        LocalDate dayFrom = LocalDate.now().minusMonths(2);
        LocalDate dayTo = LocalDate.now().minusMonths(1);

        PriceOfMedicalServiceDto price = new PriceOfMedicalServiceDto(BigDecimal.valueOf(1.12312), dayFrom, dayTo);

        accessToken = tokenUtil.obtainNewAccessToken(economist.getUsername(), "1", mockMvc);

        // Устанавливаем цену на мед услугу, нет никаких пересечений, ждем код 200
        mockMvc.perform(post("/api/economist/medicalService/setPrice/{id}", medicalService.getId())
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(price))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.price", Is.is(1.12)))
                .andExpect(jsonPath("$.data.dayFrom", Is.is(price.dayFrom().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data.dayTo", Is.is(price.dayTo().format(DATE_FORMATTER))))
//                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
        ;

        // Прошлая цена все еще лежит в базе и пытаемся отправить ее снова
        // (тем самым создаем пересечение по дате) и ждем 409
        mockMvc.perform(post("/api/economist/medicalService/setPrice/{id}", medicalService.getId())
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(price))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", Is.is(409)))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.data", IsNull.nullValue()))
                .andExpect(jsonPath("$.text", Is.is("В этот диапазон уже есть действующая цена")))
//                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
        ;

        // Ручное удаление
        PriceOfMedicalService priceQry = entityManager.createQuery("""
                        SELECT pms
                        FROM PriceOfMedicalService pms
                            LEFT JOIN MedicalService ms on ms.id = pms.medicalService.id
                        WHERE pms.medicalService.id = :id
                        """, PriceOfMedicalService.class)
                .setParameter("id", medicalService.getId())
                .getSingleResult();

        Assertions.assertEquals(priceQry.getMedicalService().getId(), medicalService.getId());

        Economist economistQry = entityManager.createQuery("""
                SELECT e
                FROM Economist e
                    LEFT JOIN Role r ON r.id = e.role.id
                WHERE e.id = :id
                """, Economist.class)
                .setParameter("id", economist.getId())
                .getSingleResult();

        Assertions.assertEquals(economistQry.getId(), economist.getId());
    }

}
