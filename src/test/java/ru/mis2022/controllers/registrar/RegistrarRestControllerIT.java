package ru.mis2022.controllers.registrar;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Registrar;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.service.entity.RegistrarService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.service.entity.TalonService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.models.entity.Role.RolesEnum.CHIEF_DOCTOR;
import static ru.mis2022.models.entity.Role.RolesEnum.DOCTOR;
import static ru.mis2022.models.entity.Role.RolesEnum.PATIENT;

// todo list 18 написать метод clear() дабы избавиться от аннотации Transactional
//  в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с DoctorPatientRestControllerIT#registerPatientInTalon
@Transactional
public class RegistrarRestControllerIT extends ContextIT {
    RoleService roleService;
    RegistrarService registrarService;
    DoctorService doctorService;

    DepartmentService departmentService;


    TalonService talonService;

    PatientService patientService;
    @Autowired
    public RegistrarRestControllerIT(RoleService roleService, RegistrarService registrarService,
             DoctorService doctorService, DepartmentService departmentService, TalonService talonService,
                                                                                    PatientService patientService) {
        this.roleService = roleService;
        this.registrarService = registrarService;
        this.doctorService = doctorService;
        this.departmentService = departmentService;
        this.talonService = talonService;
        this.patientService = patientService;
    }

    Role initRole(String name) {
        return roleService.save(Role.builder()
                .name(name)
                .build());
    }

    Registrar initRegistrar(Role role) {
        return registrarService.persist(new Registrar(
                "registrar1@email.com",
                String.valueOf("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    Doctor initDoctor(Role role, Department department, String email, String firstName, String lastName,
                      PersonalHistory personalHistory) {
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
                "супер пациент",
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
    public void getCurrentUserTest() throws Exception {
        Role role = initRole("REGISTRAR");
        Registrar registrar = initRegistrar(role);

        Role roleCheaf = initRole(CHIEF_DOCTOR.name());
        Role rolePatient = initRole(PATIENT.name());
        Role roleDoc = initRole(DOCTOR.name());

        Department department = initDepartement("Therapy");
        Department deptDantist = initDepartement("Dantist");

        Doctor ChiefDoctor = initDoctor(roleCheaf, department, "mainDoctor1@email.com", "Заведущий",
                "1. with talons" ,  null);
        Doctor docWithOutTalons = initDoctor(roleDoc, department, "docWithOutTalons@email.com",
                "док без талонов", "2. without talons" ,  null);
        Doctor docWithAllFreeTalons = initDoctor(roleDoc, department, "docWithAllFreeTalons@email.com",
                "доктор со свободными талонами", "3. all free talons" ,  null);

        Patient patient = initPatient(rolePatient);
        initTalon(LocalDateTime.now().with(LocalTime.MAX).minusHours(2), ChiefDoctor, patient);
        initTalon(LocalDateTime.now().with(LocalTime.MIN).plusHours(1), ChiefDoctor, null);

        initTalon(LocalDateTime.now().with(LocalTime.MAX).minusHours(1), docWithAllFreeTalons, null);
        initTalon(LocalDateTime.now().with(LocalTime.MIN).plusHours(1), docWithAllFreeTalons, null);


        accessToken = tokenUtil.obtainNewAccessToken(registrar.getEmail(), "1", mockMvc);

        mockMvc.perform(get("/api/registrar/mainPage/current")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
            // Check firstName, lastName of current user
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", Is.is(true)))
            .andExpect(jsonPath("$.data.roleName", Is.is("REGISTRAR")))
            .andExpect(jsonPath("$.data.lastName", Is.is("l_name")))
            .andExpect(jsonPath("$.data.firstName", Is.is("f_name")))
            // Dept has no docs
            .andExpect(jsonPath("$.data.departments[0].name", Is.is("Dantist")))
            .andExpect(jsonPath("$.data.departments[0].doctors", Matchers.nullValue()))
            // Quantity of deps:
            .andExpect(jsonPath("$.data.departments.size()").value(2))
            // Quantity of docs:
            .andExpect(jsonPath("$.data.departments[1].doctors.size()").value(3))
            // Doc without any talons
            .andExpect(jsonPath("$.data.departments[1].doctors[1].talons", Matchers.nullValue()))
            // Doc with patient on talons
            .andExpect(jsonPath("$.data.departments[1].doctors[0].talons[1].patientId", Matchers.notNullValue()))
            // Doc with free talons
            .andExpect(jsonPath("$.data.departments[1].doctors[2].talons[0].patientId", Matchers.nullValue()));
    }

}
