package ru.mis2022.controllers.registrar;

import feign.FeignException;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.feign.AuthRequestDtoTS;
import ru.mis2022.feign.AuthTokenTS;
import ru.mis2022.feign.PatientRequestDtoTS;
import ru.mis2022.feign.PatientResponseDtoTS;
import ru.mis2022.feign.TestSystemFeignClient;
import ru.mis2022.models.dto.patient.PatientDto;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.Registrar;
import ru.mis2022.models.entity.Role;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.service.entity.RegistrarService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.models.entity.Role.RolesEnum.PATIENT;
import static ru.mis2022.models.entity.Role.RolesEnum.REGISTRAR;
import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;

// todo list 17 дополнить метод clear() дабы избавиться от аннотации Transactional
//  в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с DoctorPatientRestControllerIT#registerPatientInTalon
@Transactional
@MockBean(TestSystemFeignClient.class)
public class RegistrarPatientRestControllerIT extends ContextIT {

    private final PatientService patientService;
    private final RegistrarService registrarService;
    private final RoleService roleService;

    @Autowired
    private TestSystemFeignClient testSystemFeignClient;

    private AuthRequestDtoTS authRequest;

    @Autowired
    public RegistrarPatientRestControllerIT(PatientService patientService, RegistrarService registrarService, RoleService roleService, AuthRequestDtoTS authRequest) {
        this.patientService = patientService;
        this.registrarService = registrarService;
        this.roleService = roleService;
        this.authRequest = authRequest;
    }

    Role initRole(String name) {
        return roleService.save(Role.builder().name(name).build());
    }

    Patient initPatient(Role role, String firstName, String lastName, String polis, String snils) {
        return patientService.persist(new Patient(
                "patient" + polis + "@email.com",
                "1",
                firstName,
                lastName,
                "surname",
                LocalDate.now().minusYears(20),
                role,
                "passport",
                polis,
                snils,
                "address"));
    }

    Registrar initRegistrar(Role role) {
        return registrarService.persist(new Registrar(
                "registrar1@email.com",
                "1",
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    PatientDto createPatientDto() {
        return new PatientDto(
                null,
                "Patient test",
                "супер пациент",
                "surname",
                LocalDate.now().minusYears(20),
                "PATIENT",
                "passport",
                "polis",
                "snils",
                "patientdto@email.com",
                "1");
    }

    @AfterEach
    public void clear() {
        patientService.deleteAll();
        registrarService.deleteAll();
        roleService.deleteAll();
    }

    @Test
    public void searchPatientByFirstNameOrLastNameOrPolisOrSnilsTest() throws Exception {
        Role patientRole = initRole("PATIENT");
        Role registrarRole = initRole("REGISTRAR");
        Registrar registrar = initRegistrar(registrarRole);
        Patient patient0 = initPatient(patientRole, "Adelaide", "Harris", "995226719", "28038");
        Patient patient1 = initPatient(patientRole, "Myla", "Jones", "356095322", "32048");
        Patient patient2 = initPatient(patientRole, "Zuri", "Miller", "395877782", "17724");
        Patient patient3 = initPatient(patientRole, "Sloane", "Mitchell", "522883467", "55998");
        Patient patient4 = initPatient(patientRole, "Avianna", "Taylor", "215443012", "46681");
        Patient patient5 = initPatient(patientRole, "Kade", "Moore", "616220551", "71851");
        Patient patient6 = initPatient(patientRole, "Kane", "King", "253989679", "52039");
        Patient patient7 = initPatient(patientRole, "Aspen", "Campbell", "958533983", "84106");
        Patient patient8 = initPatient(patientRole, "Diana", "Allen", "489648930", "84317");
        Patient patient9 = initPatient(patientRole, "Sloane", "Norman", "926817729", "54611");
        Patient patient10 = initPatient(patientRole, "Jaden", "Watts", "393686851", "25940");
        Patient patient11 = initPatient(patientRole, "Veronica", "Brown", "257137787", "70885");
        Patient patient12 = initPatient(patientRole, "Blake", "Wood", "845415373", "18290");
        Patient patient13 = initPatient(patientRole, "Austin", "Cox", "671731571", "14519");
        Patient patient14 = initPatient(patientRole, "Theo", "Watkins", "764964992", "62370");
        Patient patient15 = initPatient(patientRole, "Tristan", "Peterson", "396812230", "31968");
        Patient patient16 = initPatient(patientRole, "Izabella", "Fuller", "225665690", "52782");
        Patient patient17 = initPatient(patientRole, "Walter", "Meyer", "143687022", "99926");
        Patient patient18 = initPatient(patientRole, "Jaxson", "Smith", "649130652", "40360");
        Patient patient19 = initPatient(patientRole, "Emmanuel", "Hanson", "326683018", "33002");
        Patient patient20 = initPatient(patientRole, "Jared", "Edwards", "518066112", "48416");
        Patient patient21 = initPatient(patientRole, "Julia", "Ward", "872859189", "30790");
        Patient patient22 = initPatient(patientRole, "Watson", "Meyers", "370633933", "35437");

        accessToken = tokenUtil.obtainNewAccessToken(registrar.getEmail(), "1", mockMvc);

        // Проверка поиска по имени включающем в себя сочетание `Ja`
        mockMvc.perform(get("/api/registrar/patient")
                        .param("firstName", "Ja")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(3)))

                .andExpect(jsonPath("$.data[0].firstName", Is.is(patient10.getFirstName())))
                .andExpect(jsonPath("$.data[0].lastName", Is.is(patient10.getLastName())))
                .andExpect(jsonPath("$.data[0].surName", Is.is(patient10.getSurname())))
                .andExpect(jsonPath("$.data[0].birthday", Is.is(patient10.getBirthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data[0].passport", Is.is(patient10.getPassport())))
                .andExpect(jsonPath("$.data[0].polis", Is.is(patient10.getPolis())))
                .andExpect(jsonPath("$.data[0].snils", Is.is(patient10.getSnils())))

                .andExpect(jsonPath("$.data[1].firstName", Is.is(patient18.getFirstName())))
                .andExpect(jsonPath("$.data[1].lastName", Is.is(patient18.getLastName())))
                .andExpect(jsonPath("$.data[1].surName", Is.is(patient18.getSurname())))
                .andExpect(jsonPath("$.data[1].birthday", Is.is(patient18.getBirthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data[1].passport", Is.is(patient18.getPassport())))
                .andExpect(jsonPath("$.data[1].polis", Is.is(patient18.getPolis())))
                .andExpect(jsonPath("$.data[1].snils", Is.is(patient18.getSnils())))

                .andExpect(jsonPath("$.data[2].firstName", Is.is(patient20.getFirstName())))
                .andExpect(jsonPath("$.data[2].lastName", Is.is(patient20.getLastName())))
                .andExpect(jsonPath("$.data[2].surName", Is.is(patient20.getSurname())))
                .andExpect(jsonPath("$.data[2].birthday", Is.is(patient20.getBirthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data[2].passport", Is.is(patient20.getPassport())))
                .andExpect(jsonPath("$.data[2].polis", Is.is(patient20.getPolis())))
                .andExpect(jsonPath("$.data[2].snils", Is.is(patient20.getSnils())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Тест поиска по имени включающем Wa и фамилии включающей Me
        mockMvc.perform(get("/api/registrar/patient")
                        .param("firstName", "Wa")
                        .param("lastName", "Me")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(2)))

                .andExpect(jsonPath("$.data[0].firstName", Is.is(patient17.getFirstName())))
                .andExpect(jsonPath("$.data[0].lastName", Is.is(patient17.getLastName())))
                .andExpect(jsonPath("$.data[0].surName", Is.is(patient17.getSurname())))
                .andExpect(jsonPath("$.data[0].birthday", Is.is(patient17.getBirthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data[0].passport", Is.is(patient17.getPassport())))
                .andExpect(jsonPath("$.data[0].polis", Is.is(patient17.getPolis())))
                .andExpect(jsonPath("$.data[0].snils", Is.is(patient17.getSnils())))

                .andExpect(jsonPath("$.data[1].firstName", Is.is(patient22.getFirstName())))
                .andExpect(jsonPath("$.data[1].lastName", Is.is(patient22.getLastName())))
                .andExpect(jsonPath("$.data[1].surName", Is.is(patient22.getSurname())))
                .andExpect(jsonPath("$.data[1].birthday", Is.is(patient22.getBirthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data[1].passport", Is.is(patient22.getPassport())))
                .andExpect(jsonPath("$.data[1].polis", Is.is(patient22.getPolis())))
                .andExpect(jsonPath("$.data[1].snils", Is.is(patient22.getSnils())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Поиск по имени включающем в себя J и номеру полиса включающем 85
        mockMvc.perform(get("/api/registrar/patient")
                        .param("firstName", "J")
                        .param("polis", "85")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(2)))

                .andExpect(jsonPath("$.data[0].firstName", Is.is(patient10.getFirstName())))
                .andExpect(jsonPath("$.data[0].lastName", Is.is(patient10.getLastName())))
                .andExpect(jsonPath("$.data[0].surName", Is.is(patient10.getSurname())))
                .andExpect(jsonPath("$.data[0].birthday", Is.is(patient10.getBirthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data[0].passport", Is.is(patient10.getPassport())))
                .andExpect(jsonPath("$.data[0].polis", Is.is(patient10.getPolis())))
                .andExpect(jsonPath("$.data[0].snils", Is.is(patient10.getSnils())))

                .andExpect(jsonPath("$.data[1].firstName", Is.is(patient21.getFirstName())))
                .andExpect(jsonPath("$.data[1].lastName", Is.is(patient21.getLastName())))
                .andExpect(jsonPath("$.data[1].surName", Is.is(patient21.getSurname())))
                .andExpect(jsonPath("$.data[1].birthday", Is.is(patient21.getBirthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data[1].passport", Is.is(patient21.getPassport())))
                .andExpect(jsonPath("$.data[1].polis", Is.is(patient21.getPolis())))
                .andExpect(jsonPath("$.data[1].snils", Is.is(patient21.getSnils())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Тест пагинации 1 страница
        mockMvc.perform(get("/api/registrar/patient")
                        .param("offset", "0")
                        .param("size", "10")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(10)))
                .andExpect(jsonPath("$.data[0].polis", Is.is(patient0.getPolis())))
                .andExpect(jsonPath("$.data[1].polis", Is.is(patient1.getPolis())))
                .andExpect(jsonPath("$.data[2].polis", Is.is(patient2.getPolis())))
                .andExpect(jsonPath("$.data[3].polis", Is.is(patient3.getPolis())))
                .andExpect(jsonPath("$.data[4].polis", Is.is(patient4.getPolis())))
                .andExpect(jsonPath("$.data[5].polis", Is.is(patient5.getPolis())))
                .andExpect(jsonPath("$.data[6].polis", Is.is(patient6.getPolis())))
                .andExpect(jsonPath("$.data[7].polis", Is.is(patient7.getPolis())))
                .andExpect(jsonPath("$.data[8].polis", Is.is(patient8.getPolis())))
                .andExpect(jsonPath("$.data[9].polis", Is.is(patient9.getPolis())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // 2 страница
        mockMvc.perform(get("/api/registrar/patient")
                        .param("offset", "1")
                        .param("size", "10")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(10)))
                .andExpect(jsonPath("$.data[0].polis", Is.is(patient10.getPolis())))
                .andExpect(jsonPath("$.data[1].polis", Is.is(patient11.getPolis())))
                .andExpect(jsonPath("$.data[2].polis", Is.is(patient12.getPolis())))
                .andExpect(jsonPath("$.data[3].polis", Is.is(patient13.getPolis())))
                .andExpect(jsonPath("$.data[4].polis", Is.is(patient14.getPolis())))
                .andExpect(jsonPath("$.data[5].polis", Is.is(patient15.getPolis())))
                .andExpect(jsonPath("$.data[6].polis", Is.is(patient16.getPolis())))
                .andExpect(jsonPath("$.data[7].polis", Is.is(patient17.getPolis())))
                .andExpect(jsonPath("$.data[8].polis", Is.is(patient18.getPolis())))
                .andExpect(jsonPath("$.data[9].polis", Is.is(patient19.getPolis())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // 2 страница, но с 5 значениями, должна вернуть вторые 5 значений 1 страницы
        mockMvc.perform(get("/api/registrar/patient")
                        .param("offset", "1")
                        .param("size", "5")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(5)))
                .andExpect(jsonPath("$.data[0].polis", Is.is(patient5.getPolis())))
                .andExpect(jsonPath("$.data[1].polis", Is.is(patient6.getPolis())))
                .andExpect(jsonPath("$.data[2].polis", Is.is(patient7.getPolis())))
                .andExpect(jsonPath("$.data[3].polis", Is.is(patient8.getPolis())))
                .andExpect(jsonPath("$.data[4].polis", Is.is(patient9.getPolis())));

        // Тест неверного номера страницы (номер страницы должен быть > 0)
        mockMvc.perform(get("/api/registrar/patient")
                        .param("offset", "-1")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(422)))
                .andExpect(jsonPath("$.data", Is.is(IsNull.nullValue())))
                .andExpect(jsonPath("$.text", Is.is("Неверно указан номер страницы")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Тест паттерна по которому не будет найден ни один пользователь
        mockMvc.perform(get("/api/registrar/patient")
                        .param("firstName", "Christopher")
                        .param("lastName", "Notbek")
                        .param("polis", "347915212")
                        .param("snils", "47132")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data", Is.is(Collections.emptyList())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Тест запроса без параметров
        // автоматическое проставление везде null
        // если offset == null, то автоматом ставится 0,
        // если size == null, то автоматом ставится 0
        mockMvc.perform(get("/api/registrar/patient")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(10)))
                .andExpect(jsonPath("$.data[0].polis", Is.is(patient0.getPolis())))
                .andExpect(jsonPath("$.data[1].polis", Is.is(patient1.getPolis())))
                .andExpect(jsonPath("$.data[2].polis", Is.is(patient2.getPolis())))
                .andExpect(jsonPath("$.data[3].polis", Is.is(patient3.getPolis())))
                .andExpect(jsonPath("$.data[4].polis", Is.is(patient4.getPolis())))
                .andExpect(jsonPath("$.data[5].polis", Is.is(patient5.getPolis())))
                .andExpect(jsonPath("$.data[6].polis", Is.is(patient6.getPolis())))
                .andExpect(jsonPath("$.data[7].polis", Is.is(patient7.getPolis())))
                .andExpect(jsonPath("$.data[8].polis", Is.is(patient8.getPolis())))
                .andExpect(jsonPath("$.data[9].polis", Is.is(patient9.getPolis())));

        // Тест сортировки по firstName
        mockMvc.perform(get("/api/registrar/patient")
                        .param("firstName", "Ja")
                        .param("sortBy", "FIRST_NAME")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(3)))

                .andExpect(jsonPath("$.data[0].firstName", Is.is(patient10.getFirstName())))
                .andExpect(jsonPath("$.data[0].lastName", Is.is(patient10.getLastName())))
                .andExpect(jsonPath("$.data[0].surName", Is.is(patient10.getSurname())))
                .andExpect(jsonPath("$.data[0].birthday", Is.is(patient10.getBirthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data[0].passport", Is.is(patient10.getPassport())))
                .andExpect(jsonPath("$.data[0].polis", Is.is(patient10.getPolis())))
                .andExpect(jsonPath("$.data[0].snils", Is.is(patient10.getSnils())))

                .andExpect(jsonPath("$.data[1].firstName", Is.is(patient20.getFirstName())))
                .andExpect(jsonPath("$.data[1].lastName", Is.is(patient20.getLastName())))
                .andExpect(jsonPath("$.data[1].surName", Is.is(patient20.getSurname())))
                .andExpect(jsonPath("$.data[1].birthday", Is.is(patient20.getBirthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data[1].passport", Is.is(patient20.getPassport())))
                .andExpect(jsonPath("$.data[1].polis", Is.is(patient20.getPolis())))
                .andExpect(jsonPath("$.data[1].snils", Is.is(patient20.getSnils())))

                .andExpect(jsonPath("$.data[2].firstName", Is.is(patient18.getFirstName())))
                .andExpect(jsonPath("$.data[2].lastName", Is.is(patient18.getLastName())))
                .andExpect(jsonPath("$.data[2].surName", Is.is(patient18.getSurname())))
                .andExpect(jsonPath("$.data[2].birthday", Is.is(patient18.getBirthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data[2].passport", Is.is(patient18.getPassport())))
                .andExpect(jsonPath("$.data[2].polis", Is.is(patient18.getPolis())))
                .andExpect(jsonPath("$.data[2].snils", Is.is(patient18.getSnils())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

    }

    @Test
    public void findPeopleTestBedCredentials() throws Exception {
        Role role = initRole("REGISTRAR");
        Registrar registrar = initRegistrar(role);
        PatientRequestDtoTS patientRequestDto = new PatientRequestDtoTS(
                "patientFirstName1",
                "patientLastName1",
                "patientSurname1",
                "passport1",
                "snils1",
                "polis1"
        );


        accessToken = tokenUtil.obtainNewAccessToken(registrar.getEmail(), "1", mockMvc);

        TestSystemFeignClient client = mock(TestSystemFeignClient.class);
        when(client.login(authRequest))
                .thenThrow(FeignException.class);

        mockMvc.perform(post("/api/registrar/patient/findPeople")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequestDto))
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.code", Is.is(402)))
                .andExpect(jsonPath("$.text", Is.is("Test system authorisation error")))

        ;
    }

    @Test
    public void findPeopleTest() throws Exception {
        Role role = initRole("REGISTRAR");
        Registrar registrar = initRegistrar(role);
        PatientRequestDtoTS patientRequestDto = new PatientRequestDtoTS(
                "patientFirstName1",
                "patientLastName1",
                "patientSurname1",
                "passport1",
                "snils1",
                "polis1"
        );
        PatientResponseDtoTS peopleDto = new PatientResponseDtoTS(
                0L,
                patientRequestDto.firstName(),
                patientRequestDto.lastName(),
                patientRequestDto.surname(),
                null,
                patientRequestDto.passport(),
                patientRequestDto.snils(),
                patientRequestDto.polis(),
                null,
                null
        );
        AuthTokenTS authTokenTS = new AuthTokenTS("tokenValue", 86400000L, System.currentTimeMillis());
        accessToken = tokenUtil.obtainNewAccessToken(registrar.getEmail(), "1", mockMvc);

        doReturn(authTokenTS).when(testSystemFeignClient).login(authRequest);
        doReturn(peopleDto).when(testSystemFeignClient).getAllPeoples(authTokenTS.token(), patientRequestDto);

        //отправляем пациента и получаем его назад
        mockMvc.perform(post("/api/registrar/patient/findPeople")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))

                .andExpect(jsonPath("$.data.id", Is.is(0)))
                .andExpect(jsonPath("$.data.firstName", Is.is("patientFirstName1")))
                .andExpect(jsonPath("$.data.lastName", Is.is("patientLastName1")))
                .andExpect(jsonPath("$.data.surname", Is.is("patientSurname1")))
                .andExpect(jsonPath("$.data.address").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.data.passport", Is.is("passport1")))
                .andExpect(jsonPath("$.data.polis", Is.is("polis1")))
                .andExpect(jsonPath("$.data.birthday").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.data.dateOfDeath").value(IsNull.nullValue()))
        //.andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
        ;
    }

    @Test
    public void persistPatientTest() throws Exception {

        Role roleRegistrar = initRole(REGISTRAR.name());
        Role rolePatient = initRole(PATIENT.name());
        Registrar registrar = initRegistrar(roleRegistrar);

        accessToken = tokenUtil.obtainNewAccessToken(registrar.getEmail(), "1", mockMvc);

        //ВАЛИДНЫЙ ТЕСТ
        PatientDto patientDto = createPatientDto();
        mockMvc.perform(post("/api/registrar/patient/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(patientDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data.firstName", Is.is(patientDto.firstName())))
                .andExpect(jsonPath("$.data.lastName", Is.is(patientDto.lastName())))
                .andExpect(jsonPath("$.data.surName", Is.is(patientDto.surName())))
                .andExpect(jsonPath("$.data.birthday", Is.is(patientDto.birthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data.roleName", Is.is(patientDto.roleName())))
                .andExpect(jsonPath("$.data.passport", Is.is(patientDto.passport())))
                .andExpect(jsonPath("$.data.polis", Is.is(patientDto.polis())))
                .andExpect(jsonPath("$.data.snils", Is.is(patientDto.snils())))
                .andExpect(jsonPath("$.data.email", Is.is(patientDto.email())));

        //Проверка, что пациент создан

        mockMvc.perform(get("/api/registrar/patient")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(1)))
                .andExpect(jsonPath("$.data[0].firstName", Is.is(patientDto.firstName())))
                .andExpect(jsonPath("$.data[0].lastName", Is.is(patientDto.lastName())))
                .andExpect(jsonPath("$.data[0].surName", Is.is(patientDto.surName())))
                .andExpect(jsonPath("$.data[0].birthday", Is.is(patientDto.birthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data[0].roleName", Is.is(patientDto.roleName())))
                .andExpect(jsonPath("$.data[0].passport", Is.is(patientDto.passport())))
                .andExpect(jsonPath("$.data[0].polis", Is.is(patientDto.polis())))
                .andExpect(jsonPath("$.data[0].snils", Is.is(patientDto.snils())));

        //Проверка, что логин уже занят
        mockMvc.perform(post("/api/registrar/patient/create")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(patientDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(415)))
                .andExpect(jsonPath("$.text", Is.is("Пациент с данным логином уже существует в базе")));

    }


}
