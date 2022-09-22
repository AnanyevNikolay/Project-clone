package ru.mis2022.controllers.doctor;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import ru.mis2022.models.dto.patient.PatientDto;
import ru.mis2022.models.dto.patient.converter.PatientDtoConverter;
import ru.mis2022.models.dto.talon.DoctorTalonsDto;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.service.dto.TalonDtoService;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.service.entity.TalonService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;
import static ru.mis2022.utils.DateFormatter.DATE_TIME_FORMATTER;


public class DoctorTalonsRestControllerIT extends ContextIT {

    @Autowired
    TalonService talonService;
    @Autowired
    TalonDtoService talonDtoService;
    @Autowired
    DoctorService doctorService;
    @Autowired
    RoleService roleService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    PatientService patientService;

    @Value("${mis.property.doctorSchedule}")
    private Integer numberOfDays;

    @Value("${mis.property.talon}")
    private Integer numbersOfTalons;

    Role initRole(String name) {
        return roleService.save(Role.builder()
                .name(name)
                .build());
    }

    Department initDepartment(String name) {
        return departmentService.save(Department.builder()
                .name(name)
                .build());
    }

    Doctor initDoctor(Role role, Department department, PersonalHistory personalHistory, String email) {
        return doctorService.persist(new Doctor(
                email,
                "1",
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                department
        ));
    }

    Patient initPatient(Role role) {
        return patientService.persist(new Patient(
                "patient1@email.com",
                "1",
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                "passport",
                "polis",
                "snils",
                "address"));
    }

    Talon initTalon(Talon talon) {
        return talonService.save(talon);
    }

    private String formatDate(LocalDate date, int hour) {
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(8, 0).plusHours(hour));
        return time.format(DATE_TIME_FORMATTER);
    }

    String todayTimeTalon(int hour) {
        return LocalDateTime.now().with(LocalTime.of(hour, 0))
                .format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @AfterEach
    public void clear() {
        talonService.deleteAll();
        doctorService.deleteAll();
        patientService.deleteAll();
        departmentService.deleteAll();
        roleService.deleteAll();
    }

    @Test
    public void addTalonTest() throws Exception {
        LocalDate date = LocalDate.now();
        LocalDate firstTestDate = LocalDate.now().plusDays(2);
        LocalDate secondTestDate = LocalDate.now().plusDays(5);
        LocalDateTime time2 = LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(8, 0).plusHours(3));
        String formattedString2 = time2.format(DATE_TIME_FORMATTER);
        Role role = initRole("DOCTOR");
        Role role2 = initRole("PATIENT");
        Department department = initDepartment("Therapy");
        Patient patient = initPatient(role2);
        Doctor doctor1 = initDoctor(role, department, null, "doctor@email.com");

        accessToken = tokenUtil.obtainNewAccessToken(doctor1.getEmail(), "1", mockMvc);


        //УСПЕШНОЕ СОЗДАНИЕ ТАЛОНОВ
        mockMvc.perform(post("/api/doctor/talon/add")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data[0].id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[0].time", Is.is(formatDate(date, 0))))
                .andExpect(jsonPath("$.data[0].doctorId", Is.is(doctor1.getId().intValue())))

                .andExpect(jsonPath("$.data[1].id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[1].time", Is.is(formatDate(date, 1))))
                .andExpect(jsonPath("$.data[1].doctorId", Is.is(doctor1.getId().intValue())))

                .andExpect(jsonPath("$.data[2].id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[2].time", Is.is(formatDate(date, 2))))
                .andExpect(jsonPath("$.data[2].doctorId", Is.is(doctor1.getId().intValue())))

                .andExpect(jsonPath("$.data[3].id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[3].time", Is.is(formatDate(date, 3))))
                .andExpect(jsonPath("$.data[3].doctorId", Is.is(doctor1.getId().intValue())))

                .andExpect(jsonPath("$.data[11].id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[11].time", Is.is(formattedString2)))
                .andExpect(jsonPath("$.data[11].doctorId", Is.is(doctor1.getId().intValue())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //НЕКОТОРЫЕ ДНИ ЗАНЯТЫ
        mockMvc.perform(post("/api/doctor/talon/add")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(401)))
                .andExpect(jsonPath("$.text", Is.is("У доктора есть талоны на данные дни")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
        talonService.deleteAll();

        //СОЗДАНИЕ ТАЛОННОВ НА ПЕРЕДАННЫЕ ДАТЫ
        mockMvc.perform(post("/api/doctor/talon/add")
                        .param("startDate", firstTestDate.format(DATE_FORMATTER))
                        .param("endDate", secondTestDate.format(DATE_FORMATTER))
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data[0].id", Is.is(Matchers.notNullValue())))
                .andExpect(jsonPath("$.data[0].doctorId", Is.is(doctor1.getId().intValue())))
                .andExpect(jsonPath("$.data[0].time", Is.is(formatDate(firstTestDate, 0))))

                .andExpect(jsonPath("$.data[11].id", Is.is(Matchers.notNullValue())))
                .andExpect(jsonPath("$.data[11].doctorId", Is.is(doctor1.getId().intValue())))
                .andExpect(jsonPath("$.data[11].time", Is.is(formatDate(firstTestDate.plusDays(2), 3))))
                .andExpect(jsonPath("$.data.length()", Is.is(12)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //НЕКОРРЕКТНАЯ ПОСЛЕДОВАТЕЛЬНОСТЬ
        mockMvc.perform(post("/api/doctor/talon/add")
                        .param("startDate", secondTestDate.format(DATE_FORMATTER))
                        .param("endDate", firstTestDate.format(DATE_FORMATTER))
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(403)))
                .andExpect(jsonPath("$.text", Is.is("Некорректный порядок дат")));

        //ОДНА ИЗ ДАТ = NULL
        mockMvc.perform(post("/api/doctor/talon/add")
                        .param("startDate", firstTestDate.format(DATE_FORMATTER))
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(402)))
                .andExpect(jsonPath("$.text", Is.is("Введены некорректные даты")));

        //ДАТА ЗАНЯТА
        Talon talon1 = initTalon(new Talon(LocalDateTime.now().plusDays(30), doctor1, patient));

        mockMvc.perform(post("/api/doctor/talon/add")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate", LocalDate.now().plusDays(29).format(DATE_FORMATTER))
                        .param("endDate", LocalDate.now().plusDays(31).format(DATE_FORMATTER))
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(401)))
                .andExpect(jsonPath("$.text", Is.is("У доктора есть талоны на данные дни")));


    }

    @Test
    public void getAllTalonsByCurrentDoctorTest() throws Exception {
        Role role = initRole("DOCTOR");
        Role role1 = initRole("PATIENT");
        Department department = initDepartment("Therapy");
        Doctor doctor1 = initDoctor(role, department, null, "doctor1@email.com");
        Doctor doctor2 = initDoctor(role, department, null, "doctor2@email.com");
        Patient patient = initPatient(role1);
        Talon talon1 = initTalon(new Talon(LocalDateTime.now(), doctor1, null));
        Talon talon2 = initTalon(new Talon(LocalDateTime.now(), doctor1, patient));
        Talon talon3 = initTalon(new Talon(LocalDateTime.now(), doctor1, null));
        Talon talon4 = initTalon(new Talon(LocalDateTime.now().plusDays(2), doctor1, patient));
        Talon talon5 = initTalon(new Talon(LocalDateTime.now().plusDays(4), doctor1, null));
        LocalDate date = LocalDate.now();
        String formatDate = date.format(DATE_FORMATTER);

        accessToken = tokenUtil.obtainNewAccessToken(doctor1.getEmail(), "1", mockMvc);

        // У ДОКТОРА 5 ТАЛОНОВ ИЗ КОТОРЫХ 2 ЗАНЯТО
        mockMvc.perform(get("/api/doctor/talon/group")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data[0].date", Is.is(formatDate)))
                .andExpect(jsonPath("$.data[0].talonsDto[0].id", Is.is(talon1.getId().intValue())))
                .andExpect(jsonPath("$.data[0].talonsDto[0].time", Is.is(talon1.getTime().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data[0].talonsDto[0].doctorId", Is.is(doctor1.getId().intValue())))
                .andExpect(jsonPath("$.data[0].talonsDto[0].patient", Is.is(Matchers.nullValue())))
                //      .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

                .andExpect(jsonPath("$.data[0].talonsDto[1].id", Is.is(talon2.getId().intValue())))
                .andExpect(jsonPath("$.data[0].talonsDto[1].time", Is.is(talon2.getTime().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data[0].talonsDto[1].doctorId", Is.is(doctor1.getId().intValue())))
                .andExpect(jsonPath("$.data[0].talonsDto[1].patient.id", Is.is(patient.getId().intValue())))

                .andExpect(jsonPath("$.data[0].talonsDto[2].id", Is.is(talon3.getId().intValue())))
                .andExpect(jsonPath("$.data[0].talonsDto[2].time", Is.is(talon3.getTime().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data[0].talonsDto[2].doctorId", Is.is(doctor1.getId().intValue())))
                .andExpect(jsonPath("$.data[0].talonsDto[2].patient", Is.is(Matchers.nullValue())))

                .andExpect(jsonPath("$.data[1].talonsDto[0].id", Is.is(talon4.getId().intValue())))
                .andExpect(jsonPath("$.data[1].talonsDto[0].time", Is.is(talon4.getTime().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data[1].talonsDto[0].doctorId", Is.is(doctor1.getId().intValue())))
                .andExpect(jsonPath("$.data[1].talonsDto[0].patient.id", Is.is(patient.getId().intValue())))

                .andExpect(jsonPath("$.data[2].talonsDto[0].id", Is.is(talon5.getId().intValue())))
                .andExpect(jsonPath("$.data[2].talonsDto[0].time", Is.is(talon5.getTime().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data[2].talonsDto[0].doctorId", Is.is(doctor1.getId().intValue())))
                .andExpect(jsonPath("$.data[2].talonsDto[0].patient", Is.is(Matchers.nullValue())));

        // У ДОКТОРА НЕТ ТАЛОНОВ
        accessToken = tokenUtil.obtainNewAccessToken(doctor2.getEmail(), "1", mockMvc);

        mockMvc.perform(get("/api/doctor/talon/group")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(0)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // ТЕСТ НА СТЫКЕ ДАТ
        accessToken = tokenUtil.obtainNewAccessToken(doctor1.getEmail(), "1", mockMvc);

        date = LocalDate.of(2022, LocalDate.now().getMonth(), 28);
        LocalTime time = LocalTime.now();

        Talon junction_talon1 = initTalon(new Talon(LocalDateTime.of(date, time), doctor1, null));
        Talon junction_talon2 = initTalon(new Talon(LocalDateTime.of(date, time).plusDays(2), doctor1, null));
        Talon junction_talon3 = initTalon(new Talon(LocalDateTime.of(date, time).plusDays(4), doctor1, null));

        formatDate = date.format(DATE_FORMATTER);

        mockMvc.perform(get("/api/doctor/talon/group")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data[3].date", Is.is(formatDate)))
                .andExpect(jsonPath("$.data[3].talonsDto[0].time", Is.is(junction_talon1.getTime().format(DATE_TIME_FORMATTER))))

                .andExpect(jsonPath("$.data[4].talonsDto[0].time", Is.is(junction_talon2.getTime().format(DATE_TIME_FORMATTER))))

                .andExpect(jsonPath("$.data[5].talonsDto[0].time", Is.is(junction_talon3.getTime().format(DATE_TIME_FORMATTER))));

    }

    @Test
    public void onTodayTalonsTest() throws Exception {

        Role role = initRole("DOCTOR");
        Role role1 = initRole("PATIENT");
        Department department = initDepartment("Therapy");
        Doctor doctor = initDoctor(role, department, null, "doctor@email.com");
        Patient patient = initPatient(role1);
        talonService.persistTalonsForDoctor(doctor, numberOfDays, numbersOfTalons, null, null);

        // Берем получившиеся талоны (чтобы дальше заполнить пациентом)
        List<DoctorTalonsDto> doc4Talons = talonDtoService.getTalonsByDoctorIdAndDay(
                doctor.getId(),
                LocalDateTime.of(LocalDate.now(),
                        LocalTime.MIN),
                LocalDateTime.of(LocalDate.now().plusDays(numberOfDays), LocalTime.MAX));

        // Заполняем все свободные талоны пациентом:
        doc4Talons.stream()
                .map(doctorTalonsDto -> {
                    Talon talon = talonService.findTalonById(doctorTalonsDto.id());
                    talon.setPatient(patient);
                    return talon;
                })
                .forEach(talonService::save);

        accessToken = tokenUtil.obtainNewAccessToken(doctor.getEmail(), "1", mockMvc);

        mockMvc.perform(get("/api/doctor/talon/onToday")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data[0].id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[0].time", Is.is(todayTimeTalon(8))))
                .andExpect(jsonPath("$.data[0].patientId", Is.is(patient.getId().intValue())))

                .andExpect(jsonPath("$.data[1].id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[1].time", Is.is(todayTimeTalon(9))))
                .andExpect(jsonPath("$.data[1].patientId", Is.is(patient.getId().intValue())))

                .andExpect(jsonPath("$.data[2].id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[2].time", Is.is(todayTimeTalon(10))))
                .andExpect(jsonPath("$.data[2].patientId", Is.is(patient.getId().intValue())))

                .andExpect(jsonPath("$.data[3].id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data[3].time", Is.is(todayTimeTalon(11))))
                .andExpect(jsonPath("$.data[3].patientId", Is.is(patient.getId().intValue())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    public void getCurrentDoctorTalonByIdTest() throws Exception {
        Role role1 = initRole("DOCTOR");
        Role role2 = initRole("PATIENT");
        Department department = initDepartment("Therapy");
        Doctor doctor1 = initDoctor(role1, department, null, "doctor1@email.com");
        Doctor doctor2 = initDoctor(role1, department, null, "doctor2@email.com");
        Patient patient = initPatient(role2);
        Talon talon1 = initTalon(new Talon(LocalDateTime.now(), doctor1, null));
        Talon talon2 = initTalon(new Talon(LocalDateTime.now(), doctor2, null));
        Talon talon3 = initTalon(new Talon(LocalDateTime.now(), doctor1, patient));

        accessToken = tokenUtil.obtainNewAccessToken(doctor1.getEmail(), "1", mockMvc);

        // Для теста правильности возвращенного дто пацента
        PatientDtoConverter patientDtoConverter = new PatientDtoConverter();
        PatientDto patientDto = patientDtoConverter.patientToPatientDto(patient);

        // Проверяем поиск по талону и ждем ответ 200 с пациентом null
        mockMvc.perform(get("/api/doctor/talon/{id}", talon1.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.id", Is.is(talon1.getId().intValue())))
                .andExpect(jsonPath("$.data.time", Is.is(talon1.getTime().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data.doctorId", Is.is(talon1.getDoctor().getId().intValue())))
                .andExpect(jsonPath("$.data.patient", Is.is(IsNull.nullValue())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Проверяем поиск по талону и ждем ответ 200 с пациентом not null
        mockMvc.perform(get("/api/doctor/talon/{id}", talon3.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.id", Is.is(talon3.getId().intValue())))
                .andExpect(jsonPath("$.data.time", Is.is(talon3.getTime().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data.doctorId", Is.is(talon3.getDoctor().getId().intValue())))
                .andExpect(jsonPath("$.data.patient.id", Is.is(patientDto.id().intValue())))
                .andExpect(jsonPath("$.data.patient.firstName", Is.is(patientDto.firstName())))
                .andExpect(jsonPath("$.data.patient.lastName", Is.is(patientDto.lastName())))
                .andExpect(jsonPath("$.data.patient.surName", Is.is(patientDto.surName())))
                .andExpect(jsonPath("$.data.patient.birthday", Is.is(patientDto.birthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data.patient.passport", Is.is(patientDto.passport())))
                .andExpect(jsonPath("$.data.patient.polis", Is.is(patientDto.polis())))
                .andExpect(jsonPath("$.data.patient.snils", Is.is(patientDto.snils())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Проверяем поиск по талону не принадлежащему доктору и ждем 403
        mockMvc.perform(get("/api/doctor/talon/{id}", talon2.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(403)))
                .andExpect(jsonPath("$.data", Is.is(IsNull.nullValue())))
                .andExpect(jsonPath("$.text", Is.is("Талон принадлежит другому доктору")));

        // Проверяем поиск по несуществуещему талону и ждем 404
        mockMvc.perform(get("/api/doctor/talon/{id}", 888888)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(404)))
                .andExpect(jsonPath("$.data", Is.is(IsNull.nullValue())))
                .andExpect(jsonPath("$.text", Is.is("Талон не найден")));

    }
}
