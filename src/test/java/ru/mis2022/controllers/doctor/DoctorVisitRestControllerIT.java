package ru.mis2022.controllers.doctor;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Disease;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.models.entity.Visit;
import ru.mis2022.repositories.AppealRepository;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.repositories.DiseaseRepository;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.repositories.TalonRepository;
import ru.mis2022.repositories.VisitRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.models.entity.Role.RolesEnum.DOCTOR;
import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;

public class DoctorVisitRestControllerIT extends ContextIT {

    @Autowired
    VisitRepository visitRepository;
    @Autowired
    AppealRepository appealRepository;
    @Autowired
    TalonRepository talonRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    DiseaseRepository diseaseRepository;

    Role initRole(String name) {
        return roleRepository.save(Role.builder().name(name).build());
    }

    Department initDepartment(String name) {
        return departmentRepository.save(Department.builder().name(name).build());
    }

    Doctor initDoctor(Department department, Role role, String email) {
        return doctorRepository.save(Doctor
                .builder()
                .email(email)
                .department(department)
                .firstName("doc_firstname")
                .lastName("doc_lastname")
                .surname("doc_surname")
                .role(role)
                .password(passwordEncoder.encode("1"))
                .birthday(LocalDate.now().minusYears(40))
                .enabled(true)
                .build());
    }

    Talon initTalon(Doctor doctor) {
        return talonRepository.save(Talon
                .builder()
                .time(LocalDateTime.now())
                .doctor(doctor)
                .build());
    }

    Disease initDisease(String identifier, String name, Department department) {
        return diseaseRepository.save(Disease
                .builder()
                .name(name)
                .identifier(identifier)
                .department(department)
                .build());
    }

    Appeal initAppeal(Disease disease, boolean isClosed) {
        return appealRepository.save(Appeal
                .builder()
                .disease(disease)
                .localDate(LocalDate.now().minusMonths(5))
                .isClosed(isClosed)
                .build());
    }

    @AfterEach
    void clear() {
        visitRepository.deleteAll();
        appealRepository.deleteAll();
        diseaseRepository.deleteAll();
        doctorRepository.deleteAll();
        departmentRepository.deleteAll();
        roleRepository.deleteAll();
        talonRepository.deleteAll();
    }

    @Test
    public void createVisit() throws Exception {
        Role roleDoctor = initRole(DOCTOR.name());
        Department department1 = initDepartment("department1");
        Doctor doctor1 = initDoctor(department1, roleDoctor, "doc1@mail.com");

        Talon talon1 = initTalon(doctor1);
        Disease disease1 = initDisease("123gs5", "dis1", department1);
        Appeal appeal1 = initAppeal(disease1, false);

        accessToken = tokenUtil.obtainNewAccessToken(doctor1.getEmail(), "1", mockMvc);

        // Нормальный сценарий.
        mockMvc.perform(post("/api/doctor/visit/create")
                        .param("talonId", talon1.getId().toString())
                        .param("appealId", appeal1.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.dateOfVisit", Is.is(talon1.getTime().toLocalDate().format(DATE_FORMATTER))))
                .andExpect(jsonPath("$.data.doctorId", Is.is(doctor1.getId().intValue())))
                .andExpect(jsonPath("$.data.doctorFullName", Is.is(doctor1.getFirstName() + " " + doctor1.getLastName() + " " + doctor1.getSurname())))
                .andExpect(jsonPath("$.data.medicalServiceDtoList", Is.is(Collections.emptyList())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        Talon talon2 = initTalon(doctor1);

        // 410 Талона не существует.
        mockMvc.perform(post("/api/doctor/visit/create")
                        .param("talonId", String.valueOf(8888))
                        .param("appealId", appeal1.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(410)))
                .andExpect(jsonPath("$.text", Is.is("Талона не существует.")));

        // 411 Обращения не существует.
        mockMvc.perform(post("/api/doctor/visit/create")
                        .param("talonId", talon2.getId().toString())
                        .param("appealId", String.valueOf(8888))
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(411)))
                .andExpect(jsonPath("$.text", Is.is("Обращения не существует.")));

        Appeal appeal2 = initAppeal(disease1, true);

        // 412 Обращения закрыто.
        mockMvc.perform(post("/api/doctor/visit/create")
                        .param("talonId", talon2.getId().toString())
                        .param("appealId", appeal2.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(412)))
                .andExpect(jsonPath("$.text", Is.is("Обращение закрыто.")));

        Department department2 = initDepartment("department2");
        Doctor doctor2 = initDoctor(department2, roleDoctor, "doc2@mail.com");
        Talon talon3 = initTalon(doctor2);
        Appeal appeal3 = initAppeal(disease1, false);

        // 413 Обращения закрыто.
        mockMvc.perform(post("/api/doctor/visit/create")
                        .param("talonId", talon3.getId().toString())
                        .param("appealId", appeal3.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(413)))
                .andExpect(jsonPath("$.text", Is.is("Данный доктор не может создать посещение.")));

        Visit visitQry = entityManager
                .createQuery("""
                        SELECT v
                        FROM Visit v
                            WHERE v.appeal.id = :appealId AND v.doctor.id = :docId
                        """, Visit.class)
                .setParameter("appealId", appeal1.getId())
                .setParameter("docId", doctor1.getId())
                .getSingleResult();

        Assertions.assertNotNull(visitQry);

        List<Talon> talonQry = entityManager
                .createQuery("""
                        SELECT t
                        FROM Talon t
                            WHERE t.id = :talonId
                        """, Talon.class)
                .setParameter("talonId", talon1.getId())
                .getResultList();

        Assertions.assertEquals(talonQry, Collections.emptyList());
        Assertions.assertFalse(appeal1.isClosed());

    }
}
