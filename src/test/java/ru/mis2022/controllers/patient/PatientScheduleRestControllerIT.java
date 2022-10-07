package ru.mis2022.controllers.patient;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.dto.talon.DoctorTalonsDto;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.MedicalOrganization;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.service.dto.TalonDtoService;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.MedicalOrganizationService;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.service.entity.TalonService;
import ru.mis2022.util.ContextIT;
import ru.mis2022.utils.DateFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo list 15 написать метод clear() дабы избавиться от аннотации Transactional
//  в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с DoctorPatientRestControllerIT#registerPatientInTalon
@Transactional
public class PatientScheduleRestControllerIT extends ContextIT {

    @Value("${mis.property.doctorSchedule}")
    private Integer numberOfDays;
    @Value("${mis.property.talon}")
    private Integer numbersOfTalons;

    @Autowired
    RoleService roleService;
    @Autowired
    PatientService patientService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    MedicalOrganizationService medicalOrganizationService;
    @Autowired
    DoctorService doctorService;
    @Autowired
    TalonService talonService;
    @Autowired
    TalonDtoService talonDtoService;

    MedicalOrganization initMedicalOrganization(String name, String address) {
        return medicalOrganizationService.save(MedicalOrganization.builder()
                .name(name)
                .address(address)
                .build());
    }

    Role initRole(String name) {
        return roleService.save(Role.builder()
                .name(name)
                .build());
    }

    Department initDepartment(String name, MedicalOrganization medicalOrganization) {
        return departmentService.save(Department.builder()
                .name(name)
                .medicalOrganization(medicalOrganization)
                .build());
    }

    Patient initPatient(Role role) {
        return patientService.persist(new Patient(
                "patient1test@email.com",
                "1",
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                "Passport: 1234 567890",
                "Polis 770000 123456789",
                "Snils 123 456789",
                "Moscow, Mira st. 12-4-35"
        ));
    }

    Talon initTalon(LocalDateTime time, Doctor doctor, Patient patient) {


        return talonService.save(new Talon(time, doctor, patient));
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

    @Test
    public void getAllMedicalOrganizationsTest() throws Exception {
        Role rolePatient = initRole("PATIENT");
        Patient patient = initPatient(rolePatient);

        accessToken = tokenUtil.obtainNewAccessToken(patient.getEmail(), "1", mockMvc);

        //Вывод пустого списка мед организаций
        mockMvc.perform(get("/api/patient/medicalOrganizations")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.length()", Is.is(0)))
                .andExpect(jsonPath("$.code", Is.is(200)));


        initMedicalOrganization("City Hospital", "Moscow, Pravda street, 30");

        initMedicalOrganization("Hospital №1", "St. Peterburg, Lenina avenue, 3");

        initMedicalOrganization("City Clinic Hospital", "Saratov, Grin street, 25");

        //Вывод списка мед организаций
        mockMvc.perform(get("/api/patient/medicalOrganizations")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data[0].name", Is.is("City Hospital")))
                .andExpect(jsonPath("$.data[0].address", Is.is("Moscow, Pravda street, 30")))

                .andExpect(jsonPath("$.data.[1].name", Is.is("Hospital №1")))
                .andExpect(jsonPath("$.data.[1].address", Is.is("St. Peterburg, Lenina avenue, 3")))

                .andExpect(jsonPath("$.data[2].name", Is.is("City Clinic Hospital")))
                .andExpect(jsonPath("$.data[2].address", Is.is("Saratov, Grin street, 25")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    void FetchAllDepartmentsOfMedicalOrganization() throws Exception {
        Role rolePatient = initRole("PATIENT");
        Patient patient = initPatient(rolePatient);

        accessToken = tokenUtil.obtainNewAccessToken(patient.getEmail(), "1", mockMvc);

        mockMvc.perform(get("/api/patient/medicalOrganization/{medOrgId}/getAllDepartments", 100)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(414)))
                .andExpect(jsonPath("$.text", Is.is("Медицинской организации с таким id нет")));


        MedicalOrganization medicalOrganization = initMedicalOrganization("City Hospital", "Moscow, Pravda street, 30");


        initDepartment("Therapy", medicalOrganization);

        initDepartment("Surgery", medicalOrganization);

        initDepartment("Pediatrics", medicalOrganization);

        mockMvc.perform(get("/api/patient/medicalOrganization/{medOrgId}/getAllDepartments", medicalOrganization.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.[0].name", Is.is("Therapy")))
                .andExpect(jsonPath("$.data[1].name", Is.is("Surgery")))
                .andExpect(jsonPath("$.data[2].name", Is.is("Pediatrics")));
    }

    @Test
    void getAllDoctorsByDepartmentsIdTest() throws Exception {
        Department department = initDepartment("Pediatrics", null);
        Department department2 = initDepartment("Surgery", null);

        Role rolePatient = initRole("PATIENT");
        Role roleDoctor = initRole("DOCTOR");
        Patient patient = initPatient(rolePatient);

        Doctor doctor = initDoctor(roleDoctor, department, null, "doctor1@email.com");
        Doctor doctor2 = initDoctor(roleDoctor, department2, null, "doctor2@email.com");
        Doctor doctor3 = initDoctor(roleDoctor, department, null, "doctor3@email.com");
        Doctor doctor4 = initDoctor(roleDoctor, department, null, "doctor4@email.com");

        talonService.persistTalonsForDoctor(doctor,  numberOfDays, numbersOfTalons, null, null);
        talonService.persistTalonsForDoctor(doctor2,  numberOfDays, numbersOfTalons, null, null);
        talonService.persistTalonsForDoctor(doctor3,  numberOfDays, numbersOfTalons, null, null);
        talonService.persistTalonsForDoctor(doctor4,  numberOfDays, numbersOfTalons, null, null);

        // Заполняем пациентами талоны доктора 4 (так было в тесте у Анны Муравьевой)
        List<DoctorTalonsDto> doc4Talons = talonDtoService.getTalonsByDoctorIdAndDay(
                doctor4.getId(),
                LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
                LocalDateTime.of(LocalDate.now().plusDays(numberOfDays), LocalTime.MAX));

        doc4Talons.stream()
            .map(doctorTalonsDto-> {
                Talon talon = talonService.findTalonById(doctorTalonsDto.id());
                talon.setPatient(patient);
                return talon;
            })
            .forEach(talonService::save);

        // Заполняем пациентами талоны доктора 2 (так было в тесте у Анны Муравьевой)
        List<DoctorTalonsDto> doc2Talons = talonDtoService.getTalonsByDoctorIdAndDay(
                doctor2.getId(),
                LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
                LocalDateTime.of(LocalDate.now().plusDays(numberOfDays), LocalTime.MAX));

        doc2Talons.stream()
                .map(doctorTalonsDto-> {
                    Talon talon = talonService.findTalonById(doctorTalonsDto.id());
                    talon.setPatient(patient);
                    return talon;
                })
                .forEach(talonService::save);


        accessToken = tokenUtil.obtainNewAccessToken(patient.getEmail(), "1", mockMvc);

        //Такого департамента нет
        mockMvc.perform(get("/api/patient/departments/{departmentId}/getAllDoctors", 88888)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(414)))
                .andExpect(jsonPath("$.text", Is.is("Департамента с таким id нет!")));


        //Вывод списка всех докторов с пустыми талонами
        //Проверка , что не попадает в список доктор с занятыми талонами
        mockMvc.perform(get("/api/patient/departments/{departmentId}/getAllDoctors", department.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(2)))

                .andExpect(jsonPath("$.data[0].role", Is.is("DOCTOR")))
                .andExpect(jsonPath("$.data[0].lastName", Is.is(doctor.getLastName())))
                .andExpect(jsonPath("$.data[0].firstName", Is.is(doctor.getFirstName())))
                .andExpect(jsonPath("$.data[0].department", Is.is("Pediatrics")))
                .andExpect(jsonPath("$.data[0].birthday", Matchers.notNullValue()))

                .andExpect(jsonPath("$.data[0].role", Is.is("DOCTOR")))
                .andExpect(jsonPath("$.data[0].lastName", Is.is(doctor3.getLastName())))
                .andExpect(jsonPath("$.data[0].firstName", Is.is(doctor3.getFirstName())))
                .andExpect(jsonPath("$.data[0].department", Is.is("Pediatrics")))
                .andExpect(jsonPath("$.data[0].birthday", Matchers.notNullValue()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));


        //В этом департаменте нет докторов с пустыми талонами
        mockMvc.perform(get("/api/patient/departments/{departmentId}/getAllDoctors", department2.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(0)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    void findTalonsByDoctorIdAndTimeBetweenTest() throws Exception {

        Role rolePatient = initRole("PATIENT");
        Role roleDoctor = initRole("DOCTOR");
        Patient patient = initPatient(rolePatient);
        Doctor doctor = initDoctor(roleDoctor, null, null, "doctor1test@email.com");

        LocalDateTime fixedTimeNow = LocalDateTime.now();

        //подходящие талоны
        initTalon(fixedTimeNow.plusDays(2), doctor, null);
        initTalon(fixedTimeNow.plusDays(2).plusHours(1), doctor, null);
        initTalon(fixedTimeNow.plusDays(2).plusHours(2), doctor, null);
        initTalon(fixedTimeNow.plusDays(3), doctor, null);
        initTalon(fixedTimeNow.plusDays(3).plusHours(1), doctor, null);
        initTalon(fixedTimeNow.plusDays(3).plusHours(2), doctor, null);
        initTalon(fixedTimeNow.plusDays(7).with(LocalTime.MAX).minusMinutes(30), doctor, null);

        //Неподходящие талоны
        initTalon(fixedTimeNow.minusDays(100), doctor, null);
        initTalon(fixedTimeNow.plusDays(100), doctor, null);

        //Занятые талоны
        initTalon(fixedTimeNow.plusDays(2), doctor, patient);
        initTalon(fixedTimeNow.plusDays(2).minusHours(1), doctor, patient);
        initTalon(fixedTimeNow.plusDays(2).minusHours(2), doctor, patient);
        initTalon(fixedTimeNow.plusDays(3), doctor, patient);
        initTalon(fixedTimeNow.plusDays(3).minusHours(1), doctor, patient);
        initTalon(fixedTimeNow.plusDays(3).minusHours(2), doctor, patient);

        accessToken = tokenUtil.obtainNewAccessToken(patient.getEmail(), "1", mockMvc);

        //Доктора с таким id нет
        mockMvc.perform(get("/api/patient/departments/{doctorId}/getFreeTalons", 6666)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(414)))
                .andExpect(jsonPath("$.text", Is.is("Доктора с таким id нет!")));
        //.andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //проверяем валидные талоны
        mockMvc.perform(get("/api/patient/departments/{doctorId}/getFreeTalons", doctor.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))

                .andExpect(jsonPath("$.data[0].time", Is.is(fixedTimeNow.plusDays(2).format(DateFormatter.DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data[0].doctorId", Is.is(doctor.getId().intValue())))
                .andExpect(jsonPath("$.data[0].patientDto", Matchers.nullValue()))

                .andExpect(jsonPath("$.data[1].time", Is.is(fixedTimeNow.plusDays(2).plusHours(1).format(DateFormatter.DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data[1].doctorId", Is.is(doctor.getId().intValue())))
                .andExpect(jsonPath("$.data[1].patientDto", Matchers.nullValue()))

                .andExpect(jsonPath("$.data[2].time", Is.is(fixedTimeNow.plusDays(2).plusHours(2).format(DateFormatter.DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data[2].doctorId", Is.is(doctor.getId().intValue())))
                .andExpect(jsonPath("$.data[2].patientDto", Matchers.nullValue()))

                .andExpect(jsonPath("$.data[3].time", Is.is(fixedTimeNow.plusDays(3).format(DateFormatter.DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data[3].doctorId", Is.is(doctor.getId().intValue())))
                .andExpect(jsonPath("$.data[3].patientDto", Matchers.nullValue()))

                .andExpect(jsonPath("$.data[4].time", Is.is(fixedTimeNow.plusDays(3).plusHours(1).format(DateFormatter.DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data[4].doctorId", Is.is(doctor.getId().intValue())))
                .andExpect(jsonPath("$.data[4].patientDto", Matchers.nullValue()))

                .andExpect(jsonPath("$.data[5].time", Is.is(fixedTimeNow.plusDays(3).plusHours(2).format(DateFormatter.DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data[5].doctorId", Is.is(doctor.getId().intValue())))
                .andExpect(jsonPath("$.data[5].patientDto", Matchers.nullValue()))

                .andExpect(jsonPath("$.data[6].time", Is.is(fixedTimeNow.plusDays(7).with(LocalTime.MAX).minusMinutes(30).format(DateFormatter.DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.data[6].doctorId", Is.is(doctor.getId().intValue())))
                .andExpect(jsonPath("$.data[6].patientDto", Matchers.nullValue()));
              //.andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }
}


