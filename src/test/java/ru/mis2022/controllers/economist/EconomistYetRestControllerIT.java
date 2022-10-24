package ru.mis2022.controllers.economist;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.dto.yet.YetDto;
import ru.mis2022.models.dto.yet.converter.YetDtoConverter;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.entity.Role;
import ru.mis2022.repositories.EconomistRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.repositories.YetRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static org.aspectj.runtime.internal.Conversions.doubleValue;
import static org.aspectj.runtime.internal.Conversions.intValue;
import static org.aspectj.runtime.internal.Conversions.longValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class EconomistYetRestControllerIT extends ContextIT {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    EconomistRepository economistRepository;
    @Autowired
    YetRepository yetRepository;
    @Autowired
    YetDtoConverter yetDtoConverter;

    Role initRole(String name) {
        return roleRepository.save(Role.builder()
                .name(name)
                .build());
    }

    Economist initEconomist(Role role) {
        return economistRepository.save(new Economist(
                "economist1@email.com",
                passwordEncoder.encode(String.valueOf("1")),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    YearMonth parseDay(String day) {
        return YearMonth.parse(day, DateTimeFormatter.ofPattern( "MM.yyyy" ));
    }

    YetDto initYetDto(Long id, double price, String stRdayFrom, String stRdayTo) {
        YearMonth dayFrom = parseDay(stRdayFrom);
        YearMonth dayTo = parseDay(stRdayTo);
        return new YetDto(id, price, dayFrom, dayTo);
    }

    @AfterEach
    public void clear() {
        economistRepository.deleteAll();
        yetRepository.deleteAll();
        roleRepository.deleteAll();
    }

    //Создание ует
    @Test
    public void createTest() throws Exception {

        Role role = initRole("ECONOMIST");
        Economist economist = initEconomist(role);

        //Валидный ует, в таблице ует пусто. Создаем первый ует.
        YetDto validDtoCreate = initYetDto(null, 500, "02.2000", "06.2000");

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        mockMvc.perform(post("/api/economist/yet/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(validDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.id", Is.is(Matchers.notNullValue())))
                .andExpect(jsonPath("$.data.price", Is.is(doubleValue(500))))
                .andExpect(jsonPath("$.data.dayFrom", Is.is("02.2000")))
                .andExpect(jsonPath("$.data.dayTo", Is.is("06.2000")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Не валидный ует, dayFrom позже dayTo
        YetDto NoValidDateDtoCreate = initYetDto(null, 500, "12.2001", "01.2001");

        mockMvc.perform(post("/api/economist/yet/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(NoValidDateDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(417)))
                .andExpect(jsonPath("$.text", Is.is("Даты указаны неверно!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Валидный ует, новый интервал ует перекрывает существующий полностью
        YetDto noValidDayFromDayToDtoCreate = initYetDto(null, 500, "01.2000", "08.2000");

        mockMvc.perform(post("/api/economist/yet/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidDayFromDayToDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(415)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Данные yet на этот период времени уже установлены!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Валидный ует, новый интервал ует внутри существующего
        YetDto noValidDayFromDayToDtoCreate1 = initYetDto(null, 500, "04.2000", "05.2000");

        mockMvc.perform(post("/api/economist/yet/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidDayFromDayToDtoCreate1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(415)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Данные yet на этот период времени уже установлены!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Создаем в базе второй ует
        YetDto newYet = initYetDto(null, 1000, "07.2000", "09.2000");
        yetRepository.save(yetDtoConverter.toEntity(newYet));

        //Валидный ует, новый интервал ует перекрывает 2 существующих полностью
        YetDto noValidDayFromDayToDtoCreate2 = initYetDto(null, 500, "01.2000", "12.2000");

        mockMvc.perform(post("/api/economist/yet/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidDayFromDayToDtoCreate2))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(415)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Данные yet на этот период времени уже установлены!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Валидный ует, новый интервал ует перекрывает 2 существующих частично каждый
        YetDto noValidDayFromDayToDtoCreate3 = initYetDto(null, 500, "04.2000", "08.2000");

        mockMvc.perform(post("/api/economist/yet/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidDayFromDayToDtoCreate3))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(415)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Данные yet на этот период времени уже установлены!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Не валидный ует, id не null
        YetDto noValidDtoCreate1 = initYetDto(longValue(1), 500, "01.2002", "12.2002");

        mockMvc.perform(post("/api/economist/yet/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidDtoCreate1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.text", Is.is("id должен быть равен null")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

    }

    //Обновление ует
    @Test
    public void updateTest() throws Exception {

        Role role = initRole("ECONOMIST");
        Economist economist = initEconomist(role);

        //Создаем в базе ует, находим его id.
        YetDto newYet = initYetDto(null, 1000, "07.2000", "09.2000");
        yetRepository.save(yetDtoConverter.toEntity(newYet));
        Long idDto = yetRepository.findAll().listIterator().next().getId();

        //Валидный ует, обновляем ует.
        YetDto validDtoUpdate = initYetDto(idDto, 500, "02.2000", "06.2000");

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        mockMvc.perform(put("/api/economist/yet/update")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(validDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.id", Is.is(intValue(idDto))))
                .andExpect(jsonPath("$.data.price", Is.is(doubleValue(500))))
                .andExpect(jsonPath("$.data.dayFrom", Is.is("02.2000")))
                .andExpect(jsonPath("$.data.dayTo", Is.is("06.2000")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Создаем в базе еще один ует.
        YetDto newYet1 = initYetDto(null, 1000, "02.2001", "09.2001");
        yetRepository.save(yetDtoConverter.toEntity(newYet1));

        //Валидный ует, обновляем ует. Обновленный ует перекрывает полностью существующий интервал
        YetDto validDtoUpdate1 = initYetDto(idDto, 900, "01.2001", "10.2001");

        mockMvc.perform(put("/api/economist/yet/update")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(validDtoUpdate1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(415)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Данные yet на этот период времени уже установлены!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Валидный ует, обновляем ует. Интервал обновленного ует попадает внутрь существующего
        YetDto validDtoUpdate2 = initYetDto(idDto, 900, "04.2001", "06.2001");

        mockMvc.perform(put("/api/economist/yet/update")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(validDtoUpdate2))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(415)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Данные yet на этот период времени уже установлены!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Создаем в базе еще один ует.
        YetDto newYet2 = initYetDto(null, 1000, "10.2001", "12.2001");
        yetRepository.save(yetDtoConverter.toEntity(newYet2));

        //Валидный ует, обновляем ует. Интервал обновленного ует перекрывает частично 2 существующих
        YetDto validDtoUpdate3 = initYetDto(idDto, 900, "08.2001", "11.2001");

        mockMvc.perform(put("/api/economist/yet/update")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(validDtoUpdate3))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(415)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Данные yet на этот период времени уже установлены!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Не валидный ует, dayFrom позже dayTo
        YetDto noVlidDateDtoUpdate = initYetDto(idDto, 500, "12.2002", "01.2002");

        mockMvc.perform(put("/api/economist/yet/update")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noVlidDateDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(417)))
                .andExpect(jsonPath("$.text", Is.is("Даты указаны неверно!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Не валидный ует, id = 1000
        YetDto noVlidDateDtoUpdate1 = initYetDto(longValue(1000), 500, "01.2002", "12.2002");

        mockMvc.perform(put("/api/economist/yet/update")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noVlidDateDtoUpdate1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(416)))
                .andExpect(jsonPath("$.text", Is.is("По переданному id запись в базе отсутствует!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

    }

    //Удаление ует
    @Test
    public void deleteTest() throws Exception {

        Role role = initRole("ECONOMIST");
        Economist economist = initEconomist(role);

        //Создаем в базе ует, находим его id.
        YetDto newYet = initYetDto(null, 1000, "07.2000", "09.2000");
        yetRepository.save(yetDtoConverter.toEntity(newYet));
        Long idDto = yetRepository.findAll().listIterator().next().getId();

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        //Валидный id, удаление yet
        mockMvc.perform(delete("/api/economist/yet/deleteByID/{id}", idDto)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Невалидный id, удаление yet
        mockMvc.perform(delete("/api/economist/yet/deleteByID/{id}", 2000)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(416)))
                .andExpect(jsonPath("$.text", Is.is("По переданному id запись в базе отсутствует!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        }

    @Test
    public void economistGetAllYetTest() throws Exception {
        Role role = initRole("ECONOMIST");
        Economist economist = initEconomist(role);

        accessToken = tokenUtil.obtainNewAccessToken(economist.getEmail(), "1", mockMvc);

        YetDto newYetDto = initYetDto(null, 100, "01.2001", "06.2001");
        yetRepository.save(yetDtoConverter.toEntity(newYetDto));
        YetDto newYetDto1 = initYetDto(null, 200, "01.2002", "06.2002");
        yetRepository.save(yetDtoConverter.toEntity(newYetDto1));
        YetDto newYetDto2 = initYetDto(null, 300, "01.2003", "02.2003");
        yetRepository.save(yetDtoConverter.toEntity(newYetDto2));
        YetDto newYetDto3 = initYetDto(null, 400, "01.2004", "06.2004");
        yetRepository.save(yetDtoConverter.toEntity(newYetDto3));
        YetDto newYetDto4 = initYetDto(null, 500, "01.2005", "02.2005");
        yetRepository.save(yetDtoConverter.toEntity(newYetDto4));
        Long idDto = yetRepository.findAll().listIterator().next().getId();

        //Вывод списка записей Yet
        mockMvc.perform(get("/api/economist/yet/getAllYet")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(5)))

                .andExpect(jsonPath("$.data[0].id", Is.is(idDto.intValue())))
                .andExpect(jsonPath("$.data[0].price", Is.is(doubleValue(100))))
                .andExpect(jsonPath("$.data[0].dayFrom", Is.is("01.2001")))
                .andExpect(jsonPath("$.data[0].dayTo", Is.is("06.2001")))

                .andExpect(jsonPath("$.data[1].id", Is.is(idDto.intValue()+1)))
                .andExpect(jsonPath("$.data[1].price", Is.is(doubleValue(200))))
                .andExpect(jsonPath("$.data[1].dayFrom", Is.is("01.2002")))
                .andExpect(jsonPath("$.data[1].dayTo", Is.is("06.2002")))

                .andExpect(jsonPath("$.data[2].id", Is.is(idDto.intValue()+2)))
                .andExpect(jsonPath("$.data[2].price", Is.is(doubleValue(300))))
                .andExpect(jsonPath("$.data[2].dayFrom", Is.is("01.2003")))
                .andExpect(jsonPath("$.data[2].dayTo", Is.is("02.2003")))

                .andExpect(jsonPath("$.data[3].id", Is.is(idDto.intValue()+3)))
                .andExpect(jsonPath("$.data[3].price", Is.is(doubleValue(400))))
                .andExpect(jsonPath("$.data[3].dayFrom", Is.is("01.2004")))
                .andExpect(jsonPath("$.data[3].dayTo", Is.is("06.2004")))

                .andExpect(jsonPath("$.data[4].id", Is.is(idDto.intValue()+4)))
                .andExpect(jsonPath("$.data[4].price", Is.is(doubleValue(500))))
                .andExpect(jsonPath("$.data[4].dayFrom", Is.is("01.2005")))
                .andExpect(jsonPath("$.data[4].dayTo", Is.is("02.2005")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }
}
