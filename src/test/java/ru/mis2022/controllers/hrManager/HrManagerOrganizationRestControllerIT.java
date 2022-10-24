package ru.mis2022.controllers.hrManager;


import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.dto.organization.MedicalOrganizationDto;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.HrManager;
import ru.mis2022.models.entity.MedicalOrganization;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.User;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.repositories.HrManagerRepository;
import ru.mis2022.repositories.MedicalOrganizationRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.repositories.UserRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class HrManagerOrganizationRestControllerIT extends ContextIT {
    @Autowired
    HrManagerRepository hrManagerRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    MedicalOrganizationRepository medicalOrganizationRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    UserRepository userRepository;


    Role initRole(String name) {
        return roleRepository.save(Role.builder()
                .name(name)
                .build());
    }

    HrManager initHrManager(Role role) {
        return hrManagerRepository.save(new HrManager(
                "hrManager1@email.com",
                passwordEncoder.encode(String.valueOf("1")),
                "f_name",
                "l_name",
                "surName",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    MedicalOrganization initMedicalOrganizations(String name, String address) {
        return medicalOrganizationRepository.save(MedicalOrganization.builder()
                .name(name)
                .address(address)
                .build());
    }

    Department initDepartment(String name, MedicalOrganization medicalOrganization) {
        return departmentRepository.save(Department.builder()
                .name(name)
                .medicalOrganization(medicalOrganization)
                .build());
    }

    Doctor initDoctor(String email, String password, String firstName, String lastName, String surName, LocalDate age, Role role, Department department) {
        return doctorRepository.save(new Doctor(
                email,
                passwordEncoder.encode(password),
                firstName,
                lastName,
                surName,
                age,
                role,
                department
        ));
    }

    User initUser(String firstName, String lastName, String surname, LocalDate birthday, String email, Role role) {
        return userRepository.save(new User(
                email, null, firstName, lastName, surname, birthday, role));
    }

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
        hrManagerRepository.deleteAll();
        departmentRepository.deleteAll();
        doctorRepository.deleteAll();
        medicalOrganizationRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void HrManagerOrganizationTest() throws Exception {
        Role role = initRole("HR_MANAGER");
        HrManager hrManager = initHrManager(role);

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        //Список медицинские организации пуст
        mockMvc.perform(get("/api/hr_manager/medicalOrganizations")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(0)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))


        //Вывод всех медицинских организаций
        MedicalOrganization medicalOrganization = initMedicalOrganizations(
                "Больница №1", "Москва, Ленина, 1");
        MedicalOrganization medicalOrganization1 = initMedicalOrganizations(
                "Больница №21", "Москва, Ленина, 13");

        mockMvc.perform(get("/api/hr_manager/medicalOrganizations")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data[0].name", Is.is("Больница №1")))
                .andExpect(jsonPath("$.data[0].address", Is.is("Москва, Ленина, 1")))

                .andExpect(jsonPath("$.data[1].name", Is.is("Больница №21")))
                .andExpect(jsonPath("$.data[1].address", Is.is("Москва, Ленина, 13")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    public void HrManagerCreateOrganizationTest() throws Exception {
        Role role = initRole("HR_MANAGER");
        HrManager hrManager = initHrManager(role);

        //MedicalOrganization dto=initMedicalOrganizations("Больница 1", "Москва, Ленина, 1");

        MedicalOrganizationDto dto = MedicalOrganizationDto.builder()
                .name("Больница 1")
                .address("Москва, Ленина, 1")
                .build();

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        // Валидная мед.организация, создание мед.организацииD
        mockMvc.perform(post("/api/hr_manager/createMedicalOrganizations")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data.name", Is.is(dto.getName())))
                .andExpect(jsonPath("$.data.address", Is.is(dto.getAddress())));
        // .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Существующее имя мед.организации, создание мед.организации
       // MedicalOrganization dto1 = initMedicalOrganizations("Больница 1", "Москва, Ленина, 10");

        MedicalOrganizationDto dto1 = MedicalOrganizationDto.builder()
                .name("Больница 1")
                .address("Москва, Ленина, 10")
                .build();

        mockMvc.perform(post("/api/hr_manager/createMedicalOrganizations")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dto1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(412)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Такое имя медицинской организации уже используется!")));
        // .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    public void HrManagerUpdateOrganizationTest() throws Exception {
        Role role = initRole("HR_MANAGER");
        HrManager hrManager = initHrManager(role);

        MedicalOrganization validMedOrganizationUpdate = initMedicalOrganizations("Больница №2", "Москва, Разина, 3");
        MedicalOrganization noValid = new MedicalOrganization(987654567L, "Больница 1", "Москва, Разина, 3",
                null);
        validMedOrganizationUpdate.setAddress("Москва, Разина, 55");

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        //валидный, обнавление мед.организации
        mockMvc.perform(put("/api/hr_manager/updateMedicalOrganizations")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(validMedOrganizationUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.id", Is.is(validMedOrganizationUpdate.getId().intValue())))
                .andExpect(jsonPath("$.data.name", Is.is("Больница №2")))
                .andExpect(jsonPath("$.data.address", Is.is("Москва, Разина, 55")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Не валидное ид, обновление мед.организации
        mockMvc.perform(put("/api/hr_manager/updateMedicalOrganizations")
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValid))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(410)))
                .andExpect(jsonPath("$.text", Is.is(
                        "По переданному id медицинская организация не найдена.")));
        //        .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Проверка изменений в БД
        Assertions.assertEquals(
                entityManager.createQuery("""
                    select med from MedicalOrganization med
                    where med.id = :id
                """, MedicalOrganization.class)
                        .setParameter("id", validMedOrganizationUpdate.getId())
                        .getSingleResult().getAddress(),
                "Москва, Разина, 55"
        );
    }

    @Test
    public void HrManagerDeleteOrganizationTest() throws Exception {
        Role role = initRole("HR_MANAGER");
        HrManager hrManager = initHrManager(role);

        MedicalOrganization m1 = initMedicalOrganizations("Больница №45", "Москва, Ленина, 1");
        Long m2 = 8888888L;


        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);


        //валидный, удаление мед.организации
        mockMvc.perform(delete("/api/hr_manager/deleteMedicalOrganization/{id}", m1.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)));
//          .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //проверка, что мед.организация удалена по ид
        Assertions.assertTrue(1L >= entityManager.createQuery("""
                        SELECT COUNT(m) FROM MedicalOrganization m
                        WHERE m.id = :id
                        """, Long.class)
                .setParameter("id", m1.getId())
                .getSingleResult());

        // Не валидный ид, удаление мед.организации
        mockMvc.perform(delete("/api/hr_manager/deleteMedicalOrganization/{id}", m2)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(414)))
                .andExpect(jsonPath("$.text", Is.is("Медицинской организации с таким id нет!")));
        //.andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    public void getAllDepartmentsInMedicalOrganizationTest() throws Exception {
        Role roleHrManager = initRole("HR_MANAGER");
        HrManager hrManager = initHrManager(roleHrManager);
        MedicalOrganization medicalOrganization = initMedicalOrganizations("Городской госпиталь", "Москва, ул. Ленина д. 7");

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        //Организация с неверным Id
        mockMvc.perform(get("/api/hr_manager/medicalOrganization/{medOrgId}/getAllDepartments", 100)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(414)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Медицинской организации с таким id нет")));

        initDepartment("Therapy", medicalOrganization);
        initDepartment("Surgery", medicalOrganization);
        initDepartment("Pediatrics", medicalOrganization);

        //Вывод списка отделений медицинской организации
        mockMvc.perform(get("/api/hr_manager/medicalOrganization/{medOrgId}/getAllDepartments", medicalOrganization.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data[0].name", Is.is("Therapy")))
                .andExpect(jsonPath("$.data[1].name", Is.is("Surgery")))
                .andExpect(jsonPath("$.data[2].name", Is.is("Pediatrics")))
                .andExpect(jsonPath("$.data[3].name", Is.is("Other staff")));
    }

    @Test
    public void getAllEmployeesByDepartmentId() throws Exception {
        Role roleHrManager = initRole("HR_MANAGER");
        HrManager hrManager = initHrManager(roleHrManager);
        MedicalOrganization medicalOrganization = initMedicalOrganizations("Городской госпиталь", "Москва, ул. Ленина д. 7");

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        Department therapyDepartment = initDepartment("Therapy", medicalOrganization);

        //Отделение с неверным Id
        mockMvc.perform(get("/api/hr_manager/departments/{depId}/getEmployees", 88888)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(414)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Отделения с таким id нет")));

        //Нет сотрудников (врачей) в отделении
        mockMvc.perform(get("/api/hr_manager/departments/{depId}/getEmployees", therapyDepartment.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(0)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        initDoctor("therapist1@email.com", String.valueOf(1), "f_name", "l_name", "surname", LocalDate.now().minusYears(20),  initRole("DOCTOR"), therapyDepartment);
        initDoctor("therapist2@email.com", String.valueOf(1), "f_name", "l_name", "surname", LocalDate.now().minusYears(15),  initRole("CHIEF_DOCTOR"), therapyDepartment);
        initDoctor("therapist3@email.com", String.valueOf(1), "f_name", "l_name", "surname", LocalDate.now().minusYears(10),  initRole("MAIN_DOCTOR"), therapyDepartment);
        initUser("f_name", "l_name", "surname", LocalDate.now().minusYears(20), "admin@email.com", initRole("ADMIN"));
        initUser( "f_name", "l_name", "surname", LocalDate.now().minusYears(15), "registrar@email.com", initRole("REGISTRAR"));
        initUser( "f_name", "l_name", "surname", LocalDate.now().minusYears(10), "economist@email.com", initRole("ECONOMIST"));

        //Вывод списка врачей в отделении
        mockMvc.perform(get("/api/hr_manager/departments/{depId}/getEmployees", therapyDepartment.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.[0].email", Is.is("therapist1@email.com")))
                .andExpect(jsonPath("$.data.[1].email", Is.is("therapist2@email.com")))
                .andExpect(jsonPath("$.data.[2].email", Is.is("therapist3@email.com")))
                .andExpect(jsonPath("$.data.[0].roleName", Is.is("DOCTOR")))
                .andExpect(jsonPath("$.data.[1].roleName", Is.is("CHIEF_DOCTOR")))
                .andExpect(jsonPath("$.data.[2].roleName", Is.is("MAIN_DOCTOR")));

        //Вывод административного персонала
        mockMvc.perform(get("/api/hr_manager/departments/{depId}/getEmployees", 0)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data[0].roleName", Is.is("HR_MANAGER")))
                .andExpect(jsonPath("$.data[1].roleName", Is.is("ADMIN")))
                .andExpect(jsonPath("$.data[2].roleName", Is.is("REGISTRAR")))
                .andExpect(jsonPath("$.data[3].roleName", Is.is("ECONOMIST")));

    }
}
