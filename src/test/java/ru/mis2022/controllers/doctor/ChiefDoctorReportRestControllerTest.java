package ru.mis2022.controllers.doctor;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
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

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.models.entity.Role.RolesEnum.CHIEF_DOCTOR;
import static ru.mis2022.models.entity.Role.RolesEnum.DOCTOR;
import static ru.mis2022.models.entity.Role.RolesEnum.PATIENT;
import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;

// todo list 5 написать метод clear() дабы избавиться от аннотации Transactional
//  в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с DoctorPatientRestControllerIT#registerPatientInTalon
@Transactional
class ChiefDoctorReportRestControllerTest extends ContextIT {
    RoleService roleService;

    DoctorService doctorService;

    DepartmentService departmentService;

    TalonService talonService;

    PatientService patientService;

    @Autowired
    public ChiefDoctorReportRestControllerTest(RoleService roleService, DoctorService doctorService, DepartmentService departmentService, TalonService talonService, PatientService patientService) {
        this.roleService = roleService;
        this.doctorService = doctorService;
        this.departmentService = departmentService;
        this.talonService = talonService;
        this.patientService = patientService;
    }

    Role initRole(String roleName) {
        return roleService.save(Role.builder()
                .name(roleName)
                .build());
    }

    Doctor initDoctor(Role role, Department department, String email, String firstName, String lastName, PersonalHistory personalHistory) {
        return doctorService.persist(new Doctor(
                email,
                String.valueOf("1"),
                firstName,
                lastName,
                "surname",
                LocalDate.now().minusYears(20),
                role,
                department
        ));
    }

    Department initDepartement(String name) {
        return departmentService.save(Department.builder()
                .name(name)
                .build());
    }

    Patient initPatient(Role role) {
        return patientService.persist(new Patient(
                "patient1@email.com",
                String.valueOf("1"),
                "Patient test",
                "супер пац",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                "passport",
                "polis",
                "snils",
                "address"));
    }

    Talon initTalon(LocalDateTime time, Doctor doctor, Patient patient) {
        return talonService.save(new Talon(time, doctor, patient));
    }

    @Test
    void getWorkloadReport() throws Exception {
        Role roleCheaf = initRole(CHIEF_DOCTOR.name());
        Role rolePatient = initRole(PATIENT.name());
        Role roleDoc = initRole(DOCTOR.name());

        Department department = initDepartement("Therapy");
        Department deptDantist = initDepartement("Dantist");

        Doctor ChiefDoctor = initDoctor(roleCheaf, department, "mainDoctor1@email.com", "главный", "Доктор" ,  null);
        Doctor docWithOutTalons = initDoctor(roleDoc, department, "docWithOutTalons@email.com", "доктор вообще", "без талонов" ,  null);
        Doctor docWithAllFreeTalons = initDoctor(roleDoc, department, "docWithAllFreeTalons@email.com", "доктор со всеми", "свободными талонами" ,  null);

        Patient patient = initPatient(rolePatient);
        initTalon(LocalDateTime.now().with(LocalTime.MAX).minusHours(2), ChiefDoctor, patient);
        initTalon(LocalDateTime.now().with(LocalTime.MIN).plusHours(1), ChiefDoctor, null);

        initTalon(LocalDateTime.now().with(LocalTime.MAX).minusHours(1), docWithAllFreeTalons, null);
        initTalon(LocalDateTime.now().with(LocalTime.MIN).plusHours(1), docWithAllFreeTalons, null);
        initTalon(LocalDateTime.now().with(LocalTime.MIN).plusHours(2), docWithAllFreeTalons, null);


        accessToken = tokenUtil.obtainNewAccessToken(ChiefDoctor.getEmail(), "1", mockMvc);

        LocalDate ldNow = LocalDate.now();
        MvcResult result = mockMvc.perform(get("/api/chief/doctor/workload_employees_report")
                        .param("dateStart", ldNow.with(firstDayOfYear()).format(DATE_FORMATTER).toString())
                        .param("dateEnd", ldNow.with(lastDayOfYear()).format(DATE_FORMATTER).toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].doctorId", Is.is(ChiefDoctor.getId().intValue())))
                .andExpect(jsonPath("$.data[0].talons[0].busyTalons", Is.is(1)))
                .andExpect(jsonPath("$.data[0].talons[0].totalTalons", Is.is(2)))

                .andExpect(jsonPath("$.data[1].talons[0].date", Is.is(Matchers.nullValue())))
                .andExpect(jsonPath("$.data[1].talons[0].busyTalons", Is.is(0)))
                .andExpect(jsonPath("$.data[1].talons[0].totalTalons", Is.is(0)))

                .andExpect(jsonPath("$.data[2].talons[0].date", Is.is(Matchers.notNullValue())))
                .andExpect(jsonPath("$.data[2].talons[0].busyTalons", Is.is(0)))
                .andExpect(jsonPath("$.data[2].talons[0].totalTalons", Is.is(3)))
                .andReturn();

    }
}