package ru.mis2022.controllers.economist;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.dto.disease.DiseaseDto;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Disease;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.entity.Role;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.DiseaseService;
import ru.mis2022.service.entity.EconomistService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo list 6 написать метод clear() дабы избавиться от аннотации Transactional
//  в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с DoctorPatientRestControllerIT#registerPatientInTalon
@Transactional
public class EconomistDiseaseRestControllerIT extends ContextIT {

    @Autowired
    DepartmentService departmentService;

    @Autowired
    RoleService roleService;

    @Autowired
    EconomistService economistService;

    @Autowired
    DiseaseService diseaseService;


    Role initRole(String name) {
        return roleService.save(Role.builder()
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

    Disease initDisease(String identifier, String name){
        return diseaseService.save(Disease.builder()
                .identifier(identifier)
                .name(name)
                .build());
    }

    DiseaseDto initDiseaseDto(String identifier, String name) {
        return DiseaseDto.builder()
                .identifier(identifier)
                .name(name)
                .build();
    }

    Department initDepartment(String name) {
        return departmentService.save(Department.builder()
                .name(name)
                .build());

    }


    @Test
    public void getAllDiseaseTest() throws Exception {
        Role role = initRole("ECONOMIST");
        Economist economist = initEconomist(role);

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        //КЕЙС КОГДА В БАЗЕ НЕТ ЗАБОЛЕВАНИЙ
        mockMvc.perform(get("/api/economist/disease/getAll")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(0)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //ВАЛИДНЫЙ КЕЙС
        Disease disease1 = initDisease("12345", "dis1name");
        Disease disease2 = initDisease("98765", "dis2name");

        mockMvc.perform(get("/api/economist/disease/getAll")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(2)))

                .andExpect(jsonPath("$.data[0].id", Is.is(disease1.getId().intValue())))
                .andExpect(jsonPath("$.data[0].identifier", Is.is(disease1.getIdentifier())))
                .andExpect(jsonPath("$.data[0].name", Is.is(disease1.getName())))

                .andExpect(jsonPath("$.data[1].id", Is.is(disease2.getId().intValue())))
                .andExpect(jsonPath("$.data[1].identifier", Is.is(disease2.getIdentifier())))
                .andExpect(jsonPath("$.data[1].name", Is.is(disease2.getName())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    public void persistDiseaseTest() throws Exception {
        Role role = initRole("ECONOMIST");
        Economist economist = initEconomist(role);

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        //ВАЛИДНЫЙ ТЕСТ
        DiseaseDto dto1 = initDiseaseDto("12345", "Covid-19");

        mockMvc.perform(post("/api/economist/disease/create")
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

        //В БАЗЕ УЖЕ ЕСТЬ ЗАБОЛЕВАНИЕ С ТАКИМ ИДЕНТИФИКАТОРОМ
        DiseaseDto dto2 = initDiseaseDto("12345", "Covid-20");

        mockMvc.perform(post("/api/economist/disease/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dto2))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is (400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(410)))
                .andExpect(jsonPath("$.text", Is.is("Заболевание с данным идентификатором уже существует")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //В БАЗЕ ЕСТЬ ЗАБОЛЕВАНИЕ С ТАКИМ ИМЕНЕМ НО ДРУГИМ ИДЕНТИФИКАТОРОМ
        DiseaseDto dto3 = initDiseaseDto("98765", "Covid-19");

        mockMvc.perform(post("/api/economist/disease/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dto3))
                        .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data.identifier", Is.is(dto3.identifier())))
                .andExpect(jsonPath("$.data.name", Is.is(dto3.name())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    public void deleteDiseaseByIdTest() throws Exception {
        Role role = initRole("ECONOMIST");
        Economist economist = initEconomist(role);
        Disease disease1 = initDisease("12345", "dis1name");
        Disease disease2 = initDisease("98765", "dis2name");

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        //ВАЛИДНЫЙ ТЕСТ
        mockMvc.perform(delete("/api/economist/disease/delete/{diseaseId}", disease1.getId())
                                .header("Authorization", accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data", Matchers.nullValue()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //ПРОВЕРЯЮ ПО ИД, ЧТО ЗАБОЛЕВАНИЯ БОЛЬШЕ НЕТ
        Assertions.assertTrue(1L >= entityManager.createQuery("""
                        SELECT COUNT(d) FROM Disease d
                        WHERE d.id = :id
                        """, Long.class)
                .setParameter("id", disease1.getId())
                .getSingleResult());

        //ПРОВЕРЯЮ, ЧТО ДРУГОЕ ЗАБОЛЕВАНИЯ НЕ УДАЛЕНО
        Assertions.assertNotNull(entityManager.createQuery("""
                        SELECT d FROM Disease d
                        WHERE d.id = :id
                        """, Disease.class)
                .setParameter("id", disease2.getId())
                .getSingleResult());

        //ПОПЫТКА УДАЛИТЬ НЕСУЩЕСТВУЩЕЕ ЗАБОЛЕВАНИЕ
        mockMvc.perform(delete("/api/economist/disease/delete/{diseaseId}", disease1.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(411)))
                .andExpect(jsonPath("$.text", Is.is("Заболевание с переданным id не существует")))
                .andExpect(jsonPath("$.data", Matchers.nullValue()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    public void updateDiseaseTest() throws Exception {
        Role role = initRole("ECONOMIST");
        Economist economist = initEconomist(role);
        Disease disease = initDisease("AA1", "Volchanka");
        DiseaseDto diseaseDto = DiseaseDto.builder()
                .id(disease.getId())
                .identifier("AO1")
                .name("Diarea")
                .build();
        DiseaseDto diseaseDto2 = DiseaseDto.builder()
                .id(278L)
                .identifier("AA")
                .name("Cancer")
                .build();

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        //УСПЕШНАЯ МОДИФИКАЦИЯ БОЛЕЗНИ
        mockMvc.perform(put("/api/economist/disease/update")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diseaseDto))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.name", Is.is(diseaseDto.name())))
                .andExpect(jsonPath("$.data.id", Is.is(diseaseDto.id().intValue())))
                .andExpect(jsonPath("$.data.identifier", Is.is(diseaseDto.identifier())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //ПЕРЕДАН НЕСУЩЕСТВУЮЩИЙ ID
        mockMvc.perform(put("/api/economist/disease/update")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diseaseDto2))
        )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(411)))
                .andExpect(jsonPath("$.text", Is.is("Заболевания с переданным id не существует")));
    }

    @Test
    public void assignDepartment() throws Exception {
        Role role = initRole("ECONOMIST");
        Economist economist = initEconomist(role);
        Department department = initDepartment("department1");
        Disease disease = initDisease("AA1", "Volchanka");

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        //УСПЕШНОЕ НАЗНАЧЕНИЕ ДЕПАРТАМЕНТА
        mockMvc.perform(get("/api/economist/disease/assignDepartment/{diseaseId}", disease.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("departmentId", objectMapper.writeValueAsString(department.getId()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)));

        Assertions.assertEquals(disease.getDepartment().getId(), department.getId());
        Assertions.assertEquals(disease.getDepartment().getName(), department.getName());

        //РАЗРЫВАЕМ СВЯЗЬ ЗАБОЛЕВАНИЯ С ТЕКУЩИМ ДЕПАРТАМЕНТОМ
        mockMvc.perform(get("/api/economist/disease/assignDepartment/{diseaseId}", disease.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)));

        Assertions.assertNull(disease.getDepartment());

        //ПЕРЕДАЕМ НЕСУЩЕСТВУЮЩИЙ ДЕПАРТАМЕНТ
        mockMvc.perform(get("/api/economist/disease/assignDepartment/{diseaseId}", disease.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("departmentId", objectMapper.writeValueAsString(88888))
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(411)))
                .andExpect(jsonPath("$.text", Is.is("Департамента с таким id не существует")));

        //НЕСУЩЕСВТУЮЩАЯ БОЛЕЗНЬ
        mockMvc.perform(get("/api/economist/disease/assignDepartment/{diseaseId}", 88888)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("departmentId", objectMapper.writeValueAsString(department.getId()))
        )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(412)))
                .andExpect(jsonPath("$.text", Is.is("Заболевания с таким id не существует")));
    }
}