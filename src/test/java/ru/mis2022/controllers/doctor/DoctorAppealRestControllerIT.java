package ru.mis2022.controllers.doctor;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Disease;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Role;
import ru.mis2022.service.entity.AppealService;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.DiseaseService;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.models.entity.Role.RolesEnum.DOCTOR;
import static ru.mis2022.models.entity.Role.RolesEnum.PATIENT;
import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;

// todo list 4 написать метод clear() дабы избавиться от аннотации Transactional
//  в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с DoctorPatientRestControllerIT#registerPatientInTalon
@Transactional
public class DoctorAppealRestControllerIT extends ContextIT {
    @Autowired
    DoctorService doctorService;
    @Autowired
    RoleService roleService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    PatientService patientService;
    @Autowired
    DiseaseService diseaseService;
    @Autowired
    AppealService appealService;

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

    Disease initDisease(String identifier, String name, Department department) {
        return diseaseService.save(Disease.builder()
                .identifier(identifier)
                .name(name)
                .department(department)
                .build());
    }


    @Test
    void addAppealTest() throws Exception {
        Role roleDoc = initRole(DOCTOR.name());
        Role rolePatient = initRole(PATIENT.name());
        Department department1 = initDepartment("Therapy");
        Department department2 = initDepartment("AnotherDep");
        Disease disease1 = initDisease("T11", "disease_name1", department1);
        Disease disease2 = initDisease("T12", "disease_name2", department2);

        Doctor doctor = initDoctor(roleDoc, department1, null, "doc@email.com");
        Patient patient = initPatient("email1@mail.ru", "Alexandr", "Alexandrov", "Alexandrovich",
                rolePatient, "1234 112233", "123456", "434-111-222 66");


        accessToken = tokenUtil.obtainNewAccessToken(doctor.getEmail(), "1", mockMvc);

        // Пациент не существует
        mockMvc.perform(post("/api/doctor/appeal/create")
                        .param("diseaseId", disease1.getId().toString())
                        .param("patientId", "8888")
                        .param("departmentId", department1.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(410)))
                .andExpect(jsonPath("$.text", Is.is("Пациент не существует")));

        // Заболевания не существует
        mockMvc.perform(post("/api/doctor/appeal/create")
                        .param("diseaseId", "888888")
                        .param("patientId", patient.getId().toString())
                        .param("departmentId", department1.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(411)))
                .andExpect(jsonPath("$.text", Is.is("Заболевание не существует")));

        //Заболевание не лечится в текущем отделении
        mockMvc.perform(post("/api/doctor/appeal/create")
                        .param("diseaseId", disease2.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .param("departmentId", department2.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(412)))
                .andExpect(jsonPath("$.text", Is.is("Заболевание не лечится в текущем отделении")));


        //Нормальный сценарий
        mockMvc.perform(post("/api/doctor/appeal/create")
                        .param("diseaseId", disease1.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .param("departmentId", department1.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.patientId").value(patient.getId()))
                .andExpect(jsonPath("$.data.diseaseId").value(disease1.getId()))
                .andExpect(jsonPath("$.data.visits").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data.account").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data.localDate").value(LocalDate.now().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data.isClosed").value(false));

        //повторить нормальный сценарий с тем же заболеванием и тому же пациенту и убедиться что работает
        mockMvc.perform(post("/api/doctor/appeal/create")
                        .param("diseaseId", disease1.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .param("departmentId", department1.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.patientId").value(patient.getId()))
                .andExpect(jsonPath("$.data.diseaseId").value(disease1.getId()))
                .andExpect(jsonPath("$.data.visits").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data.account").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data.localDate").value(LocalDate.now().format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.data.isClosed").value(false));

        Assertions.assertNotNull(entityManager.createQuery("""
                        SELECT a
                        FROM Appeal a
                        LEFT JOIN Disease d
                        ON d.id = a.disease.id
                        LEFT JOIN Patient p
                        ON p.id = a.patient.id
                        WHERE d.id = :disId
                        AND p.id = :patientId
                        """, Appeal.class)
                .setParameter("disId", disease1.getId())
                .setParameter("patientId", patient.getId())
                .getResultList());
    }
}
