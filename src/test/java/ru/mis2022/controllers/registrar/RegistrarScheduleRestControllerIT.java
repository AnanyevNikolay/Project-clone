package ru.mis2022.controllers.registrar;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.MedicalOrganization;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Registrar;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.repositories.MedicalOrganizationRepository;
import ru.mis2022.repositories.PatientRepository;
import ru.mis2022.repositories.RegistrarRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.repositories.TalonRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.models.entity.Role.RolesEnum.DOCTOR;
import static ru.mis2022.models.entity.Role.RolesEnum.PATIENT;
import static ru.mis2022.models.entity.Role.RolesEnum.REGISTRAR;
import static ru.mis2022.models.entity.Role.builder;
import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;
import static ru.mis2022.utils.DateFormatter.DATE_TIME_FORMATTER;

public class RegistrarScheduleRestControllerIT extends ContextIT {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RegistrarRepository registrarRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    MedicalOrganizationRepository medicalOrganizationRepository;

    @Autowired
    TalonRepository talonRepository;

    @Autowired
    PatientRepository patientRepository;

    Role initRole(String name) {
        return roleRepository.save(Role.builder()
                .name(name)
                .build());
    }

    Registrar initRegistrar(Role role) {
        return registrarRepository.save(new Registrar(
                "registrar1@email.com",
                passwordEncoder.encode("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    Department initDepartment(String name, MedicalOrganization medicalOrganization) {
        return departmentRepository.save(Department.builder()
                .name(name)
                .medicalOrganization(medicalOrganization)
                .build());
    }

    Doctor initDoctor(Role role, Department department, PersonalHistory personalHistory) {
        return doctorRepository.save(new Doctor(
                "doctor1@email.com",
                passwordEncoder.encode("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                department
        ));
    }

    MedicalOrganization initMedicalOrganizations(String name, String address) {
        return medicalOrganizationRepository.save(MedicalOrganization.builder()
                .name(name)
                .address(address)
                .build());
    }

    Patient initPatient(Role role) {
        return patientRepository.save(new Patient(
                "patient1@email.com",
                passwordEncoder.encode("1"),
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

    @AfterEach
    public void clear() {
        patientRepository.deleteAll();
        registrarRepository.deleteAll();
        talonRepository.deleteAll();
        doctorRepository.deleteAll();
        departmentRepository.deleteAll();
        roleRepository.deleteAll();
        medicalOrganizationRepository.deleteAll();
    }

    Talon initTalon(LocalDateTime time, Doctor doctor, Patient patient) {
        return new Talon(time, doctor, patient);
    }

    //todo list1 починить тест. Так же в тесте тестируются сразу несколько эндпоинтов, что неправильно - разложить на несколько тестов
    @Test
    @Disabled
    public void getAllMedicalOrganizationsTest() throws Exception {

        Role roleRegistrar = initRole("REGISTRAR");
        Registrar registrar = initRegistrar(roleRegistrar);

        accessToken = tokenUtil.obtainNewAccessToken(registrar.getEmail(), "1", mockMvc);


        MedicalOrganization medicalOrganization = initMedicalOrganizations(
                "City Hospital", "Moscow, Pravda street, 30");

        //Вывод списка медицинских организаций
        mockMvc.perform(get("/api/registrar/schedule/medicalOrganizations")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data[0].name", Is.is("City Hospital")))
                .andExpect(jsonPath("$.data[0].address", Is.is("Moscow, Pravda street, 30")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Список департаментов медицинской организации с несуществующим id
        mockMvc.perform(post("/api/registrar/schedule/departments/{id}", 100)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(414)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Медицинской организации с таким id нет!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));


        Department department = initDepartment("Therapy", medicalOrganization);

        //Вывод списка департаментов
        mockMvc.perform(post("/api/registrar/schedule/departments/{id}", medicalOrganization.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data[0].name", Is.is("Therapy")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Департамента с таким id нет
        mockMvc.perform(post("/api/registrar/schedule/doctors/{id}", 100)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(414)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Департамента с таким id нет!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));


        Role roleDoctor = initRole("DOCTOR");
        Doctor doctor = initDoctor(roleDoctor, department, null);

        //Вывод списка докторов
        mockMvc.perform(post("/api/registrar/schedule/doctors/{id}", department.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data[0].role", Is.is("DOCTOR")))
                .andExpect(jsonPath("$.data[0].lastName", Is.is("l_name")))
                .andExpect(jsonPath("$.data[0].firstName", Is.is("f_name")))
                .andExpect(jsonPath("$.data[0].department", Is.is("Therapy")))
                .andExpect(jsonPath("$.data[0].birthday", Matchers.notNullValue()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Доктора с таким id нет
        mockMvc.perform(post("/api/registrar/schedule/talons/{id}", 1000)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(414)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Доктора с таким id нет!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));


        Role rolePatient = initRole("PATIENT");
        Patient patient = initPatient(rolePatient);

        //после перехода на использрвание репозиториев в тестах я закомментировал эту строку
//        talonService.persistTalonsForDoctor(doctor, 14, 4, null, null);

        List<Talon> talons = new ArrayList<>();
        LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0));
        for (int day = 0; day < 4; day++) {
            for (int hour = 0; hour < 14; hour++) {
                talons.add(talonRepository.save(new Talon(time.plusDays(day).plusHours(hour), doctor)));
            }
        }
//        talonService.persistTalonsForDoctor(doctor,14, 4, null, null);

        //Вывод талонов доктора
        mockMvc.perform(post("/api/registrar/schedule/talons/{id}", doctor.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data[0].doctorId", Is.is(doctor.getId().intValue())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    public void addPatientToTalonTest() throws Exception {
        Role rolePatient = initRole(PATIENT.name());
        Role roleDoctor = initRole(DOCTOR.name());
        Role roleRegistrar = initRole(REGISTRAR.name());
        Registrar registrar = initRegistrar(roleRegistrar);
        Department department = initDepartment("department", null);
        Patient patient = initPatient(rolePatient);
        Doctor doctor = initDoctor(roleDoctor, department, null);
        Talon talon = initTalon(LocalDateTime.now().plusHours(3), doctor, null);
        Talon busyTalon = initTalon(LocalDateTime.now().plusHours(2), doctor, patient);

        talonRepository.save(talon);
        talonRepository.save(busyTalon);

        accessToken = tokenUtil.obtainNewAccessToken(registrar.getEmail(), "1", mockMvc);

        // успешная запись на талон
        mockMvc.perform(post("/api/registrar/schedule/patientToTalon")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("talonId", talon.getId().toString())
                        .param("patientId", patient.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))

                .andExpect(jsonPath("$.data.id", Is.is(talon.getId().intValue())))
                .andExpect(jsonPath("$.data.time", Is.is(DATE_TIME_FORMATTER.format(talon.getTime()))))
                .andExpect(jsonPath("$.data.doctorId", Is.is(doctor.getId().intValue())))
                .andExpect(jsonPath("$.data.patientDto.id", Is.is(patient.getId().intValue())))
                .andExpect(jsonPath("$.data.patientDto.firstName", Is.is(patient.getFirstName())))
                .andExpect(jsonPath("$.data.patientDto.lastName", Is.is(patient.getLastName())))
                .andExpect(jsonPath("$.data.patientDto.surName", Is.is(patient.getSurname())))
                .andExpect(jsonPath("$.data.patientDto.birthday", Is.is(patient.getBirthday().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data.patientDto.passport", Is.is(patient.getPassport())))
                .andExpect(jsonPath("$.data.patientDto.polis", Is.is(patient.getPolis())))
                .andExpect(jsonPath("$.data.patientDto.snils", Is.is(patient.getSnils())))
                .andExpect(jsonPath("$.data.patientDto.email", Is.is(patient.getEmail())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
//
//         талон уже занят
        mockMvc.perform(post("/api/registrar/schedule/patientToTalon")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("talonId", busyTalon.getId().toString())
                        .param("patientId", patient.getId().toString()))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(403)))
                .andExpect(jsonPath("$.text", Is.is("Талон уже занят")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

//         талона с таким id не существует
        mockMvc.perform(post("/api/registrar/schedule/patientToTalon")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("talonId", "8888888888")
                        .param("patientId", patient.getId().toString()))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(402)))
                .andExpect(jsonPath("$.text", Is.is("Талона с данным id не существует")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

//         пациента с таким id не существует
        mockMvc.perform(post("/api/registrar/schedule/patientToTalon")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("patientId", "88888888")
                        .param("talonId", talon.getId().toString()))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(404)))
                .andExpect(jsonPath("$.text", Is.is("Пациента с данным id не существует")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }
}
