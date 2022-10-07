package ru.mis2022.controllers.doctor;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.Talon;
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

import static org.aspectj.runtime.internal.Conversions.intValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.models.entity.Role.RolesEnum.DOCTOR;
import static ru.mis2022.utils.DateFormatter.DATE_TIME_FORMATTER;

// todo list 7 в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с registerPatientInTalon в этом классе
public class DoctorPatientRestControllerIT extends ContextIT {
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private TalonService talonService;

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
                String.valueOf("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                department
        ));
    }

    Patient initPatient(String email, String firstName, String lastName, String surname, Role role, String passport, String polis, String snils) {
        return patientService.persist(new Patient(
                email,
                String.valueOf("1"),
                firstName,
                lastName,
                surname,
                LocalDate.now().minusYears(20),
                role,
                passport,
                polis,
                snils,
                null
        ));
    }

    Talon initTalon(LocalDateTime time, Doctor doctor, Patient patient) {
        return talonService.save(new Talon(time, doctor, patient));
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
    public void getPatientByFullNameTest() throws Exception {
        Role role = initRole("DOCTOR");
        Role role1 = initRole("PATIENT");
        Department department = initDepartment("Therapy");
        Doctor doctor1 = initDoctor(role, department, null, "doctor1@email.com");
        Patient patient1 = initPatient("email1@rt.ru", "Alexandr", "Safronov", "Sergeevich", role1, "2222 878190", "2349581209685472", "567-476-439 85");
        Patient patient2 = initPatient("email2@rt.ru", "Igor", "Livanov", null, role1, "3407 878190", "2349581209685472", "567-476-43 98");
        Patient patient3 = initPatient("email3@rt.ru", "Oleg", "Karimov", "Sergeevich", role1, "5671 878190", "2349581209685472", "567-476-434 56");
        Patient patient4 = initPatient("email4@rt.ru", "Sergey", "Petrov", "Sergeevich", role1, "1298 878190", "2349581209685472", "567-476-401 93");
        Patient patient5 = initPatient("email5@rt.ru", "Petr", "Krasov", "Sergeevich", role1, "1298 878190", "2349581209685472", "567-476-401 93");
        Patient patient6 = initPatient("email6@rt.ru", "Vladimir", "Ivanov", null, role1, "7304 878190", "2349581209685472", "567-476-439 80");

        accessToken = tokenUtil.obtainNewAccessToken(doctor1.getEmail(), "1", mockMvc);

        String fullName = "pet";
        mockMvc.perform(get("/api/doctor/patient/stringPattern")
                        .header("Authorization", accessToken)
                        .param("stringPattern", fullName)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(2)))
                //PETR KRASOV
                .andExpect(jsonPath("$.data[0].id", Is.is(intValue(patient5.getId()))))
                //SERGEY PETROV
                .andExpect(jsonPath("$.data[1].id", Is.is(intValue(patient4.getId()))))
                .andExpect(jsonPath("$.data[0].firstName", Is.is(patient5.getFirstName())))
                .andExpect(jsonPath("$.data[0].lastName", Is.is(patient5.getLastName())))
                .andExpect(jsonPath("$.data[0].surName", Is.is(patient5.getSurname())))
                .andExpect(jsonPath("$.data[0].birthday", Is.is(patient5.getBirthday().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))))//format(dateFormat = "dd.MM.yyyy"))))
                .andExpect(jsonPath("$.data[0].passport", Is.is(patient5.getPassport().replaceAll("\\s+", "").substring(6))))
                .andExpect(jsonPath("$.data[0].polis", Is.is(patient5.getPolis().substring(12))))
                .andExpect(jsonPath("$.data[0].snils", Is.is(patient5.getSnils().replaceAll("[^A-Za-z�-��-�0-9]", "").substring(7))));
//              .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        String fullName1 = "saf and";
        mockMvc.perform(get("/api/doctor/patient/stringPattern")
                        .header("Authorization", accessToken)
                        .param("stringPattern", fullName1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                //ALEXANDR SAFRONOV
                .andExpect(jsonPath("$.data[0].id", Is.is(intValue(patient1.getId()))))
                .andExpect(jsonPath("$.data[0].firstName", Is.is(patient1.getFirstName())))
                .andExpect(jsonPath("$.data[0].lastName", Is.is(patient1.getLastName())))
                .andExpect(jsonPath("$.data[0].surName", Is.is(patient1.getSurname())))
                .andExpect(jsonPath("$.data[0].birthday", Is.is(patient1.getBirthday().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))))
                .andExpect(jsonPath("$.data[0].passport", Is.is(patient1.getPassport().replaceAll("\\s+", "").substring(6))))
                .andExpect(jsonPath("$.data[0].polis", Is.is(patient1.getPolis().substring(12))))
                .andExpect(jsonPath("$.data[0].snils", Is.is(patient1.getSnils().replaceAll("[^A-Za-z�-��-�0-9]", "").substring(7))));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        String fullName2 = "nov gey";
        mockMvc.perform(get("/api/doctor/patient/stringPattern")
                        .header("Authorization", accessToken)
                        .param("stringPattern", fullName2)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(0)));
//              .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        String fullName3 = "biz";
        mockMvc.perform(get("/api/doctor/patient/stringPattern")
                        .header("Authorization", accessToken)
                        .param("stringPattern", fullName3)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(0)));
    }

    @Test
    void registerPatientInTalon() throws Exception {
        Role roleDoc = initRole(DOCTOR.name());
        Role rolePatient = initRole("PATIENT");
        Department department = initDepartment("Therapy");
        Doctor doctor = initDoctor(roleDoc, department, null, "doc@email.com");
        Doctor otherDoctor = initDoctor(roleDoc, department, null, "otherDoc@email.com");
        Patient patient = initPatient("email1@rt.ru", "Alexandr", "Safronov", "Sergeevich",
                rolePatient, "2222 878190", "2349581209685472", "567-476-439 85");
        Patient otherPatient = initPatient("lala", "la", "tra", "lala",
                rolePatient, "la", "1", "1");
        LocalDateTime talonTime = LocalDateTime.now().with(LocalTime.MIN).plusHours(10);
        Talon talon = initTalon(talonTime, doctor, patient);

        accessToken = tokenUtil.obtainNewAccessToken(doctor.getEmail(), "1", mockMvc);

        // Талон не существует
        mockMvc.perform(post("/api/doctor/patient/registerInTalon")
                        .param("talonId", "8888")
                        .param("patientId", patient.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(401)))
                .andExpect(jsonPath("$.text", Is.is("Талона не существует")));

        // Пациент не существует
        mockMvc.perform(post("/api/doctor/patient/registerInTalon")
                        .param("talonId", talon.getId().toString())
                        .param("patientId", "8888")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(410)))
                .andExpect(jsonPath("$.text", Is.is("Пациента не существует")));

        // Талон занят
        talon.setPatient(otherPatient);
        talonService.save(talon);
        mockMvc.perform(post("/api/doctor/patient/registerInTalon")
                        .param("talonId", talon.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(415)))
                .andExpect(jsonPath("$.text", Is.is("Талон занят")));

        // У талона нет доктора
        talon.setPatient(patient);
        talon.setDoctor(null);
        talonService.save(talon);
        mockMvc.perform(post("/api/doctor/patient/registerInTalon")
                        .param("talonId", talon.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(420)))
                .andExpect(jsonPath("$.text", Is.is("У талона нет доктора")));

        // У талона другой доктор, а не под которым зашли
        talon.setPatient(patient);
        talon.setDoctor(otherDoctor);
        talonService.save(talon);
        mockMvc.perform(post("/api/doctor/patient/registerInTalon")
                        .param("talonId", talon.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(425)))
                .andExpect(jsonPath("$.text", Is.is("Талон принадлежит другому доктору")));

        // Все норм (пациент в талоне есть, но тот же самый, которого пытаются записать)
        talon.setPatient(patient);
        talon.setDoctor(doctor);
        talonService.save(talon);
        mockMvc.perform(post("/api/doctor/patient/registerInTalon")
                        .param("talonId", talon.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.doctorId").value(doctor.getId()))
                .andExpect(jsonPath("$.data.patientDto.id").value(patient.getId()))
                .andExpect(jsonPath("$.data.time").value(DATE_TIME_FORMATTER.format(talonTime)));

        // Все норм (пациент в талоне отсутствует)
        talon.setPatient(null);
        talon.setDoctor(doctor);
        talonService.save(talon);
        mockMvc.perform(post("/api/doctor/patient/registerInTalon")
                        .param("talonId", talon.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.doctorId").value(doctor.getId()))
                .andExpect(jsonPath("$.data.patientDto.id").value(patient.getId()))
                .andExpect(jsonPath("$.data.time").value(DATE_TIME_FORMATTER.format(talonTime)));

        Talon qryTalon = entityManager.createQuery("""
            SELECT t
            FROM Talon t
            LEFT JOIN Doctor d
                 ON d.id = t.doctor.id
            LEFT JOIN Patient p
                 ON p.id = t.patient.id
            WHERE d.id = :docId
                 AND p.id = :patientId
            """, Talon.class)
                .setParameter("docId", doctor.getId())
                .setParameter("patientId", patient.getId())
                .getSingleResult();


        Assertions.assertEquals(qryTalon.getDoctor().getId(), doctor.getId());
        Assertions.assertEquals(qryTalon.getPatient().getId(), patient.getId());
    }
}
