package ru.mis2022.controllers.doctor;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.Account;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Disease;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.MedicalService;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.Visit;
import ru.mis2022.repositories.AccountRepository;
import ru.mis2022.repositories.AppealRepository;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.repositories.DiseaseRepository;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.repositories.MedicalServiceRepository;
import ru.mis2022.repositories.PatientRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.repositories.VisitRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.models.entity.Role.RolesEnum.DOCTOR;
import static ru.mis2022.models.entity.Role.RolesEnum.PATIENT;
import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;

public class DoctorAppealRestControllerIT extends ContextIT {
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    DiseaseRepository diseaseRepository;
    @Autowired
    AppealRepository appealRepository;
    @Autowired
    VisitRepository visitRepository;
    @Autowired
    MedicalServiceRepository medicalServiceRepository;
    @Autowired
    AccountRepository accountRepository;

    Role initRole(String name) {
        return roleRepository.save(Role.builder()
                .name(name)
                .build());
    }

    Department initDepartment(String name) {
        return departmentRepository.save(Department.builder()
                .name(name)
                .build());
    }

    Doctor initDoctor(Role role, Department department, PersonalHistory personalHistory, String email) {
        return doctorRepository.save(new Doctor(
                email,
                passwordEncoder.encode(String.valueOf("1")),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                department
        ));
    }

    Patient initPatient(String email, String firstName, String lastName, String surname,
                        Role role, String passport, String polis, String snils) {
        return patientRepository.save(new Patient(
                email,
                passwordEncoder.encode(String.valueOf("1")),
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
        return diseaseRepository.save(Disease.builder()
                .identifier(identifier)
                .name(name)
                .department(department)
                .build());
    }

    Appeal initAppeal(Patient patient, Disease disease, LocalDate data) {
        return appealRepository.save(new Appeal(
                patient,
                disease,
                data
        ));
    }

    void updateAppeal(Appeal appeal) {
        appealRepository.save(appeal);
    }
    void updateMedicalService(MedicalService medicalService) {
        medicalServiceRepository.save(medicalService);
    }


    Visit initVisit(LocalDate day, Doctor doctor, Appeal appeal, Set<MedicalService> services) {
        return visitRepository.save(new Visit(day, doctor, appeal, services));
    }

    MedicalService initMedicalService(String identifier) {
        return medicalServiceRepository.save(new MedicalService(
                identifier,
                "MedicalServ"
        ));
    }

    Account initAccount() {
        return accountRepository.save(new Account());
    }

    @AfterEach
    void clear() {
        medicalServiceRepository.deleteAll();
        visitRepository.deleteAll();
        accountRepository.deleteAll();
        doctorRepository.deleteAll();
        appealRepository.deleteAll();
        patientRepository.deleteAll();
        diseaseRepository.deleteAll();
        roleRepository.deleteAll();
        departmentRepository.deleteAll();
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
        Patient patient = initPatient("email1@mail.ru", "Alexandr",
                "Alexandrov", "Alexandrovich",
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

        Doctor qryDoctor = entityManager.createQuery("""
                        SELECT doc
                        FROM Doctor doc
                        LEFT JOIN Department dep
                            ON dep.id = doc.department.id
                        LEFT JOIN Role role
                            ON role.id = doc.role.id
                        WHERE dep.id = :departmentId
                            AND role.id = :roleId
                        """, Doctor.class)
                .setParameter("departmentId", department1.getId())
                .setParameter("roleId", roleDoc.getId())
                .getSingleResult();

        Assertions.assertEquals(qryDoctor.getId(), doctor.getId());
        Assertions.assertEquals(qryDoctor.getDepartment().getId(), department1.getId());
        Assertions.assertEquals(qryDoctor.getRole().getId(), roleDoc.getId());

        Patient qryPatient = entityManager.createQuery("""
                        SELECT pat
                        FROM Patient pat
                        LEFT JOIN Role role
                            ON role.id = pat.role.id
                        WHERE role.id = :roleId
                        """, Patient.class)
                .setParameter("roleId", rolePatient.getId())
                .getSingleResult();

        Assertions.assertEquals(qryPatient.getId(), patient.getId());
        Assertions.assertEquals(qryPatient.getRole().getId(), rolePatient.getId());

        Disease qryDisease = entityManager.createQuery("""
                        SELECT dis
                        FROM Disease dis
                            WHERE dis.id = :disId
                        """, Disease.class)
                .setParameter("disId", disease1.getId())
                .getSingleResult();

        Assertions.assertEquals(qryDisease.getId(), disease1.getId());
    }

    @Test
    void getAppealsTest() throws Exception {

        // Инициализация цепочек сущностей
        Role roleDoc = initRole(DOCTOR.name());
        Role rolePatient = initRole(PATIENT.name());
        Department department = initDepartment("RandomDepartment");
        Doctor doctor = initDoctor(roleDoc, department, null, "doc@email.com");
        Patient patient = initPatient("patient@email.com", "Ivan", "Ivanov",
                "Ivanovich", rolePatient, "1234 112233", "123456", "434-111-222 66");
        Account account = initAccount();


        accessToken = tokenUtil.obtainNewAccessToken(doctor.getEmail(), "1", mockMvc);

        // Сущности первого обращения
        Disease disease1 = initDisease("111", "diseaseName", department);
        Appeal appeal1 = initAppeal(patient, disease1, LocalDate.now().minusDays(26));
        appeal1.setClosed(true);
        appeal1.setAccount(account);
        updateAppeal(appeal1);

        // Пациент не существует
        mockMvc.perform(get("/api/doctor/appeal/getPatientAppeals")
                        .param("patientId", "1111")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(411)))
                .andExpect(jsonPath("$.text", Is.is("Пациента с таким id не существует")));


        // Сущности второго обращения
        Disease disease2 = initDisease("112", "diseaseName2", department);
        Appeal appeal2 = initAppeal(patient, disease2, LocalDate.now().minusDays(26));

        MedicalService ms1v1a1 = initMedicalService("ms1");
        MedicalService ms2v1a1 = initMedicalService("ms2");
        MedicalService ms3v2a1 = initMedicalService("ms3");
        MedicalService ms4v2a1 = initMedicalService("ms4");
        MedicalService ms5v2a1 = initMedicalService("ms5");
        MedicalService ms6v3a1 = initMedicalService("ms6");

        Set<MedicalService> msV1a2 = new HashSet<>(Arrays.asList(ms1v1a1, ms2v1a1));
        Set<MedicalService> msV2a2 = new HashSet<>(Arrays.asList(ms3v2a1, ms4v2a1, ms5v2a1));
        Set<MedicalService> msV3a2 = new HashSet<>(Collections.singletonList(ms6v3a1));

        Set<Visit> visits = new HashSet<>();
        Visit visit1 = initVisit(LocalDate.now().minusDays(25), doctor, appeal2, msV1a2);
        Visit visit2 = initVisit(LocalDate.now().minusDays(23), doctor, appeal2, msV2a2);
        Visit visit3 = initVisit(LocalDate.now().minusDays(21), doctor, appeal2, msV3a2);
        visits.add(visit1);
        visits.add(visit2);
        visits.add(visit3);

        ms1v1a1.setVisit(visit1);
        ms2v1a1.setVisit(visit1);
        ms3v2a1.setVisit(visit2);
        ms4v2a1.setVisit(visit2);
        ms5v2a1.setVisit(visit2);
        ms6v3a1.setVisit(visit3);

        updateMedicalService(ms1v1a1);
        updateMedicalService(ms2v1a1);
        updateMedicalService(ms3v2a1);
        updateMedicalService(ms4v2a1);
        updateMedicalService(ms5v2a1);
        updateMedicalService(ms6v3a1);

        appeal2.setVisits(visits);
        appeal2.setClosed(false);
        appeal2.setAccount(account);
        updateAppeal(appeal2);

        // Сущности третьего обращения
        Disease disease3 = initDisease("113", "diseaseName3", department);
        Appeal appeal3 = initAppeal(patient, disease3, LocalDate.now().minusDays(26));

        MedicalService ms1v1a3 = initMedicalService("ms1");
        MedicalService ms2v2a3 = initMedicalService("ms2");
        MedicalService ms3v2a3 = initMedicalService("ms3");

        Set<MedicalService> msV1a3 = new HashSet<>(Collections.singleton(ms1v1a3));
        Set<MedicalService> msV2a3 = new HashSet<>(Arrays.asList(ms2v2a3, ms3v2a3));

        Set<Visit> visits2 = new HashSet<>();
        Visit a3visit1 = initVisit(LocalDate.now().minusDays(25), doctor, appeal3, msV1a3);
        Visit a3visit2 = initVisit(LocalDate.now().minusDays(23), doctor, appeal3, msV2a3);
        visits2.add(a3visit1);
        visits2.add(a3visit2);

        ms1v1a3.setVisit(a3visit1);
        ms2v2a3.setVisit(a3visit2);
        ms3v2a3.setVisit(a3visit2);

        updateMedicalService(ms1v1a3);
        updateMedicalService(ms2v2a3);
        updateMedicalService(ms3v2a3);

        appeal3.setVisits(visits2);
        appeal3.setClosed(false);
        appeal3.setAccount(account);
        updateAppeal(appeal3);


        // Проверка корректности полученных данных обращения
        mockMvc.perform(get("/api/doctor/appeal/getPatientAppeals")
                .param("patientId", String.valueOf(patient.getId()))
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(2)))
                .andExpect(jsonPath("$.data[0].appealId", Is.is(appeal2.getId().intValue())))
                .andExpect(jsonPath("$.data[0].status", Is.is(appeal2.isClosed())))
                .andExpect(jsonPath("$.data[0].diseaseName", Is.is(disease2.getName())))
                .andExpect(jsonPath("$.data[0].visitDtoList.length()", Is.is(visits.size())))
                .andExpect(jsonPath("$.data[0].visitDtoList[0].visitId", Is.is(visit1.getId().intValue())))
                .andExpect(jsonPath("$.data[0].visitDtoList[1].visitId", Is.is(visit2.getId().intValue())))
                .andExpect(jsonPath("$.data[0].visitDtoList[2].visitId", Is.is(visit3.getId().intValue())))
                .andExpect(jsonPath("$.data[0].visitDtoList[0].medicalServiceDtoList.length()", Is.is(msV1a2.size())))
                .andExpect(jsonPath("$.data[0].visitDtoList[1].medicalServiceDtoList.length()", Is.is(msV2a2.size())))
                .andExpect(jsonPath("$.data[0].visitDtoList[2].medicalServiceDtoList.length()", Is.is(msV3a2.size())))
                .andExpect(jsonPath("$.data[1].appealId", Is.is(appeal3.getId().intValue())))
                .andExpect(jsonPath("$.data[1].status", Is.is(appeal3.isClosed())))
                .andExpect(jsonPath("$.data[1].diseaseName", Is.is(disease3.getName())))
                .andExpect(jsonPath("$.data[1].visitDtoList.length()", Is.is(visits2.size())))
                .andExpect(jsonPath("$.data[1].visitDtoList[0].visitId", Is.is(a3visit1.getId().intValue())))
                .andExpect(jsonPath("$.data[1].visitDtoList[1].visitId", Is.is(a3visit2.getId().intValue())))
                .andExpect(jsonPath("$.data[1].visitDtoList[0].medicalServiceDtoList.length()", Is.is(msV1a3.size())))
                .andExpect(jsonPath("$.data[1].visitDtoList[1].medicalServiceDtoList.length()", Is.is(msV2a3.size())));

    }
}
