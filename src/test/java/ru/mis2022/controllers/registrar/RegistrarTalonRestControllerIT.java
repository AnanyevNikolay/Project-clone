package ru.mis2022.controllers.registrar;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import ru.mis2022.repositories.PatientRepository;
import ru.mis2022.repositories.RegistrarRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.repositories.TalonRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.models.entity.Role.builder;
import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;
import static ru.mis2022.utils.DateFormatter.DATE_TIME_FORMATTER;


public class RegistrarTalonRestControllerIT extends ContextIT {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TalonRepository talonRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    RegistrarRepository registrarRepository;

    Role initRole(String name) {
        return roleRepository.save(builder()
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

    Department initDepartment(String name) {
        return departmentRepository.save(Department.builder()
                .name(name)
                .build());
    }

    Doctor initDoctor(String email, Role role, Department department) {
        return doctorRepository.save(new Doctor(
                email,
                passwordEncoder.encode("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                department
        ));
    }

    Patient initPatient(String email, String firstName, String lastName, String surname, Role role, String passport, String polis, String snils) {
        return patientRepository.save(new Patient(
                email,
                passwordEncoder.encode("1"),
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
        return talonRepository.save(new Talon(time, doctor, patient));
    }

    @AfterEach
    public void clear() {
        registrarRepository.deleteAll();
        talonRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
        departmentRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @BeforeEach
    public void clearBefore() {
        registrarRepository.deleteAll();
        talonRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
        departmentRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void transferAppointmentToAnotherDoctorTest() throws Exception {
        Role roleRegistrar = initRole("REGISTRAR");
        Registrar registrar = initRegistrar(roleRegistrar);
        Role roleDoc = initRole("DOCTOR");
        Role rolePatient = initRole("PATIENT");
        Department department = initDepartment("Therapy");
        Department otherDepartment = initDepartment("Surgery");
        Doctor doctor = initDoctor("mainDoctor1@email.com", roleDoc, department);
        Doctor otherDoctor = initDoctor("mainDoctor2@email.com", roleDoc, otherDepartment);
        Patient patient = initPatient("email1@rt.ru", "Alexandr", "Safronov",
                "Sergeevich", rolePatient, "2222 878190", "2349581209685472", "567-476-439 85");
        Patient otherPatient = initPatient("email2@rt.ru", "Alex", "Safron",
                "Sergeevich", rolePatient, "3222 878190", "2349381209685472", "537-476-439 85");
        LocalDateTime talonTime = LocalDateTime.now().with(LocalTime.MIN).plusHours(10);
        LocalDateTime talonTime2 = LocalDateTime.now().with(LocalTime.MIN).plusHours(20);
        Talon talon = initTalon(talonTime, doctor, patient);
        Talon talon2 = initTalon(talonTime2, otherDoctor, patient);
        Talon talon3 = initTalon(talonTime, doctor, patient);

        accessToken = tokenUtil.obtainNewAccessToken(registrar.getEmail(), "1", mockMvc);

        // Талон1 c таким id не существует
        mockMvc.perform(post("/api/registrar/talon/transferAppointment")
                        .param("oldId", "8888")
                        .param("newId", talon2.getId().toString())
                        .param("isDelete", "false")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(402)))
                .andExpect(jsonPath("$.text", Is.is("Талона со старым id нет")));

        //Талон2 c таким id не существует
        mockMvc.perform(post("/api/registrar/talon/transferAppointment")
                        .param("oldId", talon2.getId().toString())
                        .param("newId", "999999999999")
                        .param("isDelete", "false")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(403)))
                .andExpect(jsonPath("$.text", Is.is("Талона с новым id нет")));

        // У талонов разные отделения
        mockMvc.perform(post("/api/registrar/talon/transferAppointment")
                        .param("oldId", talon.getId().toString())
                        .param("newId", talon2.getId().toString())
                        .param("isDelete", "true")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(404)))
                .andExpect(jsonPath("$.text", Is.is("Талоны находятся в разных отделениях")));

        // Талон c таким id существует
        mockMvc.perform(post("/api/registrar/talon/transferAppointment")
                        .param("oldId", talon3.getId().toString())
                        .param("newId", talon.getId().toString())
                        .param("isDelete", "true")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
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

        // Талон со старым удалился
        Assertions.assertNull(talonRepository.findTalonById(talon3.getId()));
    }

}
