package ru.mis2022.controllers.patient;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.Account;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Disease;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.MedicalOrganization;
import ru.mis2022.models.entity.MedicalService;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.Visit;
import ru.mis2022.service.entity.AccountService;
import ru.mis2022.service.entity.AppealService;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.DiseaseService;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.MedicalOrganizationService;
import ru.mis2022.service.entity.MedicalServiceService;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.service.entity.VisitService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;

public class PatientAppealRestControllerIT extends ContextIT {

    RoleService roleService;
    PatientService patientService;
    DepartmentService departmentService;
    DoctorService doctorService;
    MedicalOrganizationService medicalOrganizationService;
    AppealService appealService;
    DiseaseService diseaseService;
    AccountService accountService;
    MedicalServiceService medicalServiceService;
    VisitService visitService;

    @Autowired
    public PatientAppealRestControllerIT(RoleService roleService,
                                         PatientService patientService,
                                         DepartmentService departmentService,
                                         DoctorService doctorService,
                                         MedicalOrganizationService medicalOrganizationService,
                                         AppealService appealService,
                                         DiseaseService diseaseService,
                                         AccountService accountService,
                                         MedicalServiceService medicalServiceService,
                                         VisitService visitService) {
        this.roleService = roleService;
        this.patientService = patientService;
        this.departmentService = departmentService;
        this.doctorService = doctorService;
        this.medicalOrganizationService = medicalOrganizationService;
        this.appealService = appealService;
        this.diseaseService = diseaseService;
        this.accountService = accountService;
        this.medicalServiceService = medicalServiceService;
        this.visitService = visitService;
    }

    Role initRole(String roleName) {
        return roleService.save(new Role(roleName));
    }

    Patient initPatient(String email, Role role) {
        return patientService.persist(new Patient(
                email,
                "patientPassword",
                "patientFirstName",
                "patientLastName",
                "patientSurname",
                LocalDate.now(),
                role,
                "patientPassport",
                "patientPolis",
                "patientSnils"
        ));
    }

    Doctor initDoctor(String email, Role role, Department department) {
        return doctorService.persist(new Doctor(
                email,
                "doctorPwd",
                "doctorFn",
                "doctorLn",
                "DoctorS",
                LocalDate.now().minusYears(20),
                role,
                department
        ));
    }

    Department initDepartment(MedicalOrganization medicalOrganization) {
        return departmentService.save(new Department(
                "Department1",
                medicalOrganization
        ));
    }

    MedicalOrganization initMedicalOrganization() {
        return medicalOrganizationService.save(new MedicalOrganization(
                "MedicalOrg",
                "MedicalOrgAddress"
        ));
    }

    Appeal initAppeal(Patient patient, Disease disease, Set<Visit> visits, Account account, boolean isClosed, LocalDate date) {
        return appealService.save(new Appeal(
                patient,
                disease,
                visits,
                account,
                isClosed,
                date
        ));
    }

    Disease initDisease(String identifier) {
        return diseaseService.save(new Disease(
                identifier,
                "Disease"
        ));
    }

    Account initAccount() {
        return accountService.save(new Account());
    }

    MedicalService initMedicalService(String identifier) {
        return medicalServiceService.save(new MedicalService(
                identifier,
                "MedicalServ"
        ));
    }

    Visit initVisit(LocalDate date, Doctor doctor, Set<MedicalService> medicalServices) {
        return visitService.save(new Visit(
                date,
                doctor,
                null,
                medicalServices
        ));
    }

    @Test
    public void getCurrentPatientAppealsTest() throws Exception {
        // »нитим роли
        Role patientRole = initRole("PATIENT");
        Role doctorRole = initRole("DOCTOR");

        // »нитим двух пациентов
        Patient patient1 = initPatient("patient1@mail.com", patientRole);
        Patient patient2 = initPatient("patient2@mail.com", patientRole);

        // »нитим аккаунты дл€ пациентов
        Account patient1Acc = initAccount();
        Account patient2Acc = initAccount();

        // »нитим мед. организацию, департамент и двух докторов в одном отделе
        MedicalOrganization medicalOrganization = initMedicalOrganization();

        Department department = initDepartment(medicalOrganization);

        Doctor doctor1 = initDoctor("doctor1@mail.com", doctorRole, department);
        Doctor doctor2 = initDoctor("doctor2@mail.com", doctorRole, department);

        // »нитим два заболевани€ дл€ двух пациентов
        Disease patient1Disease1 = initDisease("p1d1");
        Disease patient1Disease2 = initDisease("p1d2");

        // »нитим 6 медицинских услуг по 3 на два посещени€
        MedicalService visit1Ms1 = initMedicalService("ms1");
        MedicalService visit1Ms2 = initMedicalService("ms2");
        MedicalService visit1Ms3 = initMedicalService("ms3");

        MedicalService visit2Ms1 = initMedicalService("ms4");
        MedicalService visit2Ms2 = initMedicalService("ms5");
        MedicalService visit2Ms3 = initMedicalService("ms6");

        // —обираем мед. услуги в сет и инитим два посещени€
        Set<MedicalService> visit1ServicesSet = new HashSet<>();
        visit1ServicesSet.add(visit1Ms1);
        visit1ServicesSet.add(visit1Ms2);
        visit1ServicesSet.add(visit1Ms3);
        Visit appeal1Visit1 = initVisit(LocalDate.now().minusDays(1), doctor1, visit1ServicesSet);

        Set<MedicalService> visit2ServicesSet = new HashSet<>();
        visit2ServicesSet.add(visit2Ms1);
        visit2ServicesSet.add(visit2Ms2);
        visit2ServicesSet.add(visit2Ms3);
        Visit appeal1Visit2 = initVisit(LocalDate.now(), doctor2, visit2ServicesSet);

        // —обираем посещени€ в сет и инитим обращание дл€ первого пациента
        Set<Visit> appeal1VisitSet = new HashSet<>();
        appeal1VisitSet.add(appeal1Visit1);
        appeal1VisitSet.add(appeal1Visit2);
        Appeal patient1Appeal1 = initAppeal(patient1, patient1Disease1, appeal1VisitSet, patient1Acc, true, LocalDate.now());

        // »нитим 3 услуги дл€ третьего посещени€ во втором обращание
        MedicalService visit3Ms1 = initMedicalService("ms7");
        MedicalService visit3Ms2 = initMedicalService("ms8");
        MedicalService visit3Ms3 = initMedicalService("ms9");

        // —обираем услуги в сет и инитим посещение
        Set<MedicalService> visit3ServicesSet = new HashSet<>();
        visit3ServicesSet.add(visit3Ms1);
        visit3ServicesSet.add(visit3Ms2);
        visit3ServicesSet.add(visit3Ms3);
        Visit appeal2Visit1 = initVisit(LocalDate.now(), doctor2, visit3ServicesSet);

        // —обираем посещени€ в сет и инитим второе обращание дл€ первого пациента
        Set<Visit> appeal2VisitSet = new HashSet<>();
        appeal2VisitSet.add(appeal2Visit1);
        Appeal patient1Appeal2 = initAppeal(patient1, patient1Disease2, appeal2VisitSet, patient1Acc, false, LocalDate.now());

        // »нитим как обращение дл€ второго пациента
        // как две капли воды похожее на первое обращаение первого пациента
        // ожидаем что оно не попадет в ответ от контроллера
        Visit appeal3Visit1 = initVisit(LocalDate.now().minusDays(1), doctor1, visit1ServicesSet);
        Visit appeal3Visit2 = initVisit(LocalDate.now(), doctor2, visit2ServicesSet);
        Set<Visit> appeal3VisitSet = new HashSet<>();
        appeal3VisitSet.add(appeal3Visit1);
        appeal3VisitSet.add(appeal3Visit2);
        initAppeal(patient2, patient1Disease1, appeal3VisitSet, patient2Acc, true, LocalDate.now());

        String patient1FullName = patient1.getFirstName() + " " + patient1.getLastName() + " " + patient1.getSurname();
        String doctor1FullName = doctor1.getFirstName() + " " + doctor1.getLastName() + " " + doctor1.getSurname();
        String doctor2FullName = doctor2.getFirstName() + " " + doctor2.getLastName() + " " + doctor2.getSurname();

        accessToken = tokenUtil.obtainNewAccessToken(patient1.getEmail(), "patientPassword", mockMvc);

        mockMvc.perform(get("/api/patient/appeals")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.patientId", Is.is(patient1.getId().intValue())))
                .andExpect(jsonPath("$.data.patientFullName", Is.is(patient1FullName)))
                .andExpect(jsonPath("$.data.appealDtoList.length()", Is.is(2)))

                .andExpect(jsonPath("$.data.appealDtoList[0].appealId", Is.is(patient1Appeal1.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[0].diseaseName", Is.is(patient1Appeal1.getDisease().getName())))
                .andExpect(jsonPath("$.data.appealDtoList[0].status", Is.is(patient1Appeal1.isClosed())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList.length()", Is.is(2)))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].visitId", Is.is(appeal1Visit1.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].dateOfVisit", Is.is(appeal1Visit1.getDayOfVisit().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].doctorId", Is.is(appeal1Visit1.getDoctor().getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].doctorFullName", Is.is(doctor1FullName)))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].medicalServiceDtoList.length()", Is.is(3)))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].medicalServiceDtoList[0].id", Is.is(visit1Ms1.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].medicalServiceDtoList[0].identifier", Is.is(visit1Ms1.getIdentifier())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].medicalServiceDtoList[0].name", Is.is(visit1Ms1.getName())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].medicalServiceDtoList[1].id", Is.is(visit1Ms2.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].medicalServiceDtoList[1].identifier", Is.is(visit1Ms2.getIdentifier())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].medicalServiceDtoList[1].name", Is.is(visit1Ms2.getName())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].medicalServiceDtoList[2].id", Is.is(visit1Ms3.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].medicalServiceDtoList[2].identifier", Is.is(visit1Ms3.getIdentifier())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[0].medicalServiceDtoList[2].name", Is.is(visit1Ms3.getName())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].visitId", Is.is(appeal1Visit2.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].dateOfVisit", Is.is(appeal1Visit2.getDayOfVisit().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].doctorId", Is.is(appeal1Visit2.getDoctor().getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].doctorFullName", Is.is(doctor2FullName)))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].medicalServiceDtoList.length()", Is.is(3)))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].medicalServiceDtoList[0].id", Is.is(visit2Ms1.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].medicalServiceDtoList[0].identifier", Is.is(visit2Ms1.getIdentifier())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].medicalServiceDtoList[0].name", Is.is(visit2Ms1.getName())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].medicalServiceDtoList[1].id", Is.is(visit2Ms2.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].medicalServiceDtoList[1].identifier", Is.is(visit2Ms2.getIdentifier())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].medicalServiceDtoList[1].name", Is.is(visit2Ms2.getName())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].medicalServiceDtoList[2].id", Is.is(visit2Ms3.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].medicalServiceDtoList[2].identifier", Is.is(visit2Ms3.getIdentifier())))
                .andExpect(jsonPath("$.data.appealDtoList[0].visitDtoList[1].medicalServiceDtoList[2].name", Is.is(visit2Ms3.getName())))

                .andExpect(jsonPath("$.data.appealDtoList[1].appealId", Is.is(patient1Appeal2.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[1].diseaseName", Is.is(patient1Appeal2.getDisease().getName())))
                .andExpect(jsonPath("$.data.appealDtoList[1].status", Is.is(patient1Appeal2.isClosed())))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList.length()", Is.is(1)))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].visitId", Is.is(appeal2Visit1.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].dateOfVisit", Is.is(appeal2Visit1.getDayOfVisit().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].doctorId", Is.is(appeal2Visit1.getDoctor().getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].doctorFullName", Is.is(doctor1FullName)))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].medicalServiceDtoList.length()", Is.is(3)))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].medicalServiceDtoList[0].id", Is.is(visit3Ms1.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].medicalServiceDtoList[0].identifier", Is.is(visit3Ms1.getIdentifier())))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].medicalServiceDtoList[0].name", Is.is(visit3Ms1.getName())))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].medicalServiceDtoList[1].id", Is.is(visit3Ms2.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].medicalServiceDtoList[1].identifier", Is.is(visit3Ms2.getIdentifier())))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].medicalServiceDtoList[1].name", Is.is(visit3Ms2.getName())))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].medicalServiceDtoList[2].id", Is.is(visit3Ms3.getId().intValue())))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].medicalServiceDtoList[2].identifier", Is.is(visit3Ms3.getIdentifier())))
                .andExpect(jsonPath("$.data.appealDtoList[1].visitDtoList[0].medicalServiceDtoList[2].name", Is.is(visit3Ms3.getName())))

//                .andDo(mockMvcRes -> System.out.println(mockMvcRes.getResponse().getContentAsString()))
                ;
    }

}
