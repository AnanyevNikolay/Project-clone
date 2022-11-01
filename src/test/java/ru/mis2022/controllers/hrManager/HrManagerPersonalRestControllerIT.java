package ru.mis2022.controllers.hrManager;


import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.HrManager;
import ru.mis2022.models.entity.MedicalOrganization;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.User;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.repositories.HrManagerRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.repositories.UserRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.aspectj.runtime.internal.Conversions.intValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class HrManagerPersonalRestControllerIT extends ContextIT {

    @Autowired
    HrManagerRepository hrManagerRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    DoctorRepository doctorRepository;

    Role initRole(String name) {
        return roleRepository.save(Role.builder()
                .name(name)
                .build());
    }

    HrManager initHrManager(Role role) {
        return hrManagerRepository.save(new HrManager(
                "hrManager@email.com",
                passwordEncoder.encode(String.valueOf("1")),
                "Иванов",
                "Иван",
                "Иванович",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    User initUser(String firstName, String lastName, String email, Role role, LocalDate birthday) {
        return userRepository.save(new User(
                email, null, firstName, lastName, null, birthday, role));
    }

    Department initDepartment(String name, MedicalOrganization medicalOrganization) {
        return departmentRepository.save(Department.builder()
                .name(name)
                .medicalOrganization(medicalOrganization)
                .build());
    }

    Department initDepartmentForChief(String name, MedicalOrganization medicalOrganization, List<Doctor> doctors) {
        return departmentRepository.save(Department.builder()
                .name(name)
                .medicalOrganization(medicalOrganization)
                .build());
    }

    Doctor initDoctor(String email, String password, String firstName, String lastName, String surName, LocalDate age, Role role, Department department) {
        return doctorRepository.save(new Doctor(
                email,
                password,
                firstName,
                lastName,
                surName,
                age,
                role,
                department
        ));
    }

    String getFullName(User user) {
        return user.getLastName() + " " + user.getFirstName();
    }

    @AfterEach
    public void clear() {
        hrManagerRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void findUsersByFullName() throws Exception {
        Role roleHrManager = initRole("HR_MANAGER");
        Role rolePatient = initRole("PATIENT");
        HrManager hrManager = initHrManager(roleHrManager);
        User user1 = initUser("Александр", "Александров","email99", roleHrManager, null);
        User user2 = initUser("Николай", "Комаров", "email100", roleHrManager, null);
        User user3 = initUser("Николай", "Васильев","email101", roleHrManager, null);
        User user4 = initUser("Даниил", "Данилов","email102", rolePatient, null);
        User user5 = initUser("Ирина", "Данилова","email103", rolePatient, null);
        User user6 = initUser("Василий", "Прохоров","email104", roleHrManager, null);
        User user7 = initUser("Ирина", "Коробова","email105", roleHrManager, null);
        User user8 = initUser("Василий", "Александров","email106", roleHrManager, null);
        User user9 = initUser("Сергей", "Сергеев","email107", roleHrManager, null);
        User user10 = initUser("Александр", "Коротков","email108", roleHrManager, null);

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);
        //Сортировка осуществляется в последовательности: фамилия - имя - id

        //Вывод списка сотрудников в имени или фамилии которых есть "алек"
        String fullName = "алек";
        mockMvc.perform(get("/api/hr_manager/allUsers")
                        .header("Authorization", accessToken)
                        .param("fullName", fullName)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(3)))
                //Александров Александр
                .andExpect(jsonPath("$.data[0].id", Is.is(intValue(user1.getId()))))
                //Александров Василий
                .andExpect(jsonPath("$.data[1].id", Is.is(intValue(user8.getId()))))
                //Коротков Александр
                .andExpect(jsonPath("$.data[2].id", Is.is(intValue(user10.getId()))));
//               .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Вывод списка сотрудников в фамилии которых есть "ров", в имени "сил"
        String fullName1 = "ров сил";
        mockMvc.perform(get("/api/hr_manager/allUsers")
                        .header("Authorization", accessToken)
                        .param("fullName", fullName1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(2)))
                //Александров Василий
                .andExpect(jsonPath("$.data[0].id", Is.is(intValue(user8.getId()))))
                //Прохоров Василий
                .andExpect(jsonPath("$.data[1].id", Is.is(intValue(user6.getId()))));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Вывод списка сотрудников в фамилии которых есть "ков", в имени "сил"
        String fullName2 = "ков сил";
        mockMvc.perform(get("/api/hr_manager/allUsers")
                        .header("Authorization", accessToken)
                        .param("fullName", fullName2)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(0)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Вывод списка сотрудников(весь список) если передан null,
        //проверка на отсутствие пациентов в списке персонала
        String fullName3 = null;
        mockMvc.perform(get("/api/hr_manager/allUsers")
                        .header("Authorization", accessToken)
                        .param("fullName", fullName3)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(9)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Вывод списка сотрудников(пустой список) если передано более одного пробела
        String fullName4 = "  ";
        mockMvc.perform(get("/api/hr_manager/allUsers")
                        .header("Authorization", accessToken)
                        .param("fullName", fullName4)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(9)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

    }

    @Test
    public void findAllBirthdayInRange() throws Exception {
        Role roleHrManager = initRole("HR_MANAGER");
        Role rolePatient = initRole("PATIENT");
        HrManager hrManager = initHrManager(roleHrManager);
        User user1 = initUser("Александр", "Александров","email99", roleHrManager,
                LocalDate.now().plusDays(28));
        User user2 = initUser("Николай", "Комаров", "email100", roleHrManager,
                LocalDate.now().plusDays(40));
        User user3 = initUser("Николай", "Васильев","email101", roleHrManager,
                LocalDate.now().plusDays(7));
        User user4 = initUser("Даниил", "Данилов","email102", rolePatient,
                LocalDate.now().plusDays(11));
        User user5 = initUser("Ирина", "Данилова","email103", rolePatient,
                LocalDate.now().plusDays(25));
        User user6 = initUser("Василий", "Прохоров","email104", roleHrManager,
                LocalDate.now().plusDays(77));
        User user7 = initUser("Ирина", "Коробова","email105", roleHrManager,
                LocalDate.now().plusDays(3));
        User user8 = initUser("Василий", "Александров","email106", roleHrManager,
                LocalDate.now().plusDays(100));
        User user9 = initUser("Сергей", "Сергеев","email107", roleHrManager,
                LocalDate.now().plusDays(10));
        User user10 = initUser("Александр", "Коротков","email108", roleHrManager,
                LocalDate.now().plusDays(44));

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        //ПОЛУЧАЕМ СПИСОК ИМЕНИННИКОВ НА 20 ДНЕЙ ВПЕРЕД (4)
        mockMvc.perform(get("/api/hr_manager/findBirthdayInRange")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("daysCount", objectMapper.writeValueAsString(20))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(4)))

                //ИВАН ИВАНОВ
                .andExpect(jsonPath("$.data[0].id", Is.is(hrManager.getId().intValue())))
                //ИРИНА КОРОБОВА
                .andExpect(jsonPath("$.data[1].id", Is.is(user7.getId().intValue())))
                //НИКОЛАЙ ВАСИЛЬЕВ
                .andExpect(jsonPath("$.data[2].id", Is.is(user3.getId().intValue())))
                //СЕРГЕЙ СЕРГЕЕВ
                .andExpect(jsonPath("$.data[3].id", Is.is(user9.getId().intValue())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //ПОЛУЧАЕМ СПИСОК ИМЕНИННИКОВ НА ДЕФОЛТНОЕ ЗНАЧЕНИЕ = 30 (5)
        mockMvc.perform(get("/api/hr_manager/findBirthdayInRange")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(5)))

                //ИВАН ИВАНОВ
                .andExpect(jsonPath("$.data[0].id", Is.is(hrManager.getId().intValue())))
                //ИРИНА КОРОБОВА
                .andExpect(jsonPath("$.data[1].id", Is.is(user7.getId().intValue())))
                //НИКОЛАЙ ВАСИЛЬЕВ
                .andExpect(jsonPath("$.data[2].id", Is.is(user3.getId().intValue())))
                //СЕРГЕЙ СЕРГЕЕВ
                .andExpect(jsonPath("$.data[3].id", Is.is(user9.getId().intValue())))
                //АЛЕКСАНДР АЛЕКСАНДРОВ
                .andExpect(jsonPath("$.data[4].id", Is.is(user1.getId().intValue())));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    public void addChiefDoctorTest() throws Exception {
        Role roleHrManager = initRole("HR_MANAGER");
        Role roleDoctor = initRole("DOCTOR");
        Role roleChiefDoctor = initRole("CHIEF_DOCTOR");

        HrManager hrManager = initHrManager(roleHrManager);
        Department department = initDepartment("Surgery", null);
        Department department1 = initDepartment("Pediarty", null);

        Doctor doctor = initDoctor("mukagali@mail.com", "1", "Mukagali",
                "Orazbakov", "Nurgaliuly", LocalDate.now().minusYears(25), roleDoctor, department);

        Doctor doctor1 = initDoctor("muka@mail.com", "1", "Mukagali",
                "Orazbakov", "Nurgaliuly", LocalDate.now().minusYears(25), roleChiefDoctor, null);

        // Данные для теста "В отделении уже 2 заведующих"
        Doctor doctor2 = initDoctor("mukkagali@mail.com", "1", "Mukagali",
                "Orazbakov", "Nurgaliuly", LocalDate.now().minusYears(25), roleChiefDoctor, department1);

        Doctor doctor3 = initDoctor("mukar@mail.com", "1", "Mukagali",
                "Orazbakov", "Nurgaliuly", LocalDate.now().minusYears(25), roleChiefDoctor, department1);

        Doctor doctor4 = initDoctor("m@mail.com", "1", "Mukagali",
                "Orazbakov", "Nurgaliuly", LocalDate.now().minusYears(25), roleDoctor, department1);

        List<Doctor> doctors = new ArrayList<>();

        doctors.add(doctor4);
        doctors.add(doctor3);
        doctors.add(doctor2);

        Department department2 = initDepartmentForChief("Surgery", null, doctors);

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        //Валидный тест(Назначаем к department зав.врача и ждем 200 code)
        mockMvc.perform(post("/api/hr_manager/addChiefDoctorForDepartment")
                .param("departmentId", department.getId().toString())
                .param("doctorId", doctor.getId().toString())
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().is(200))
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)));

//        //Доктор с таким id(777777) не существует
        mockMvc.perform(post("/api/hr_manager/addChiefDoctorForDepartment")
                .param("departmentId", department.getId().toString())
                .param("doctorId", "777777")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(411)))
                .andExpect(jsonPath("$.text", Is.is("Доктора с таким id не существует")));

        //В отделении уже 2 заведующих
        mockMvc.perform(post("/api/hr_manager/addChiefDoctorForDepartment")
                        .param("departmentId", department1.getId().toString())
                        .param("doctorId", doctor4.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(412)))
                .andExpect(jsonPath("$.text", Is.is("В отделении уже две заведующих отделением")));

    }

    @Test
    public void addMainDoctorTest() throws Exception {

        Role roleChiefDoctor = initRole("CHIEF_DOCTOR");
        Role roleMainDoctor = initRole("MAIN_DOCTOR");
        Role roleHrManager = initRole("HR_MANAGER");

        Department department = initDepartment("Surgery", null);

        HrManager hrManager = initHrManager(roleHrManager);

        Doctor doctor = initDoctor("muka@mail.com", "1", "Mukagali",
                "Orazbakov", "Nurgaliuly", LocalDate.now().minusYears(25), roleChiefDoctor, department);

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        //Валидный тест, назначаем врачу роль MAIN_DOCTOR и ждем 200
        mockMvc.perform(post("/api/hr_manager/addMainDoctorForOrganization")
                        .param("doctorId", doctor.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().is(200))
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)));

//        //Доктора с таким id(99999) не существует
        mockMvc.perform(post("/api/hr_manager/addMainDoctorForOrganization")
                        .param("doctorId", "99999")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(411)))
                .andExpect(jsonPath("$.text", Is.is("Доктора с таким id не существует")));

    }

    @Test
    public void addMainDoctorForMainTest() throws Exception {

        Role roleChiefDoctor = initRole("CHIEF_DOCTOR");
        Role roleMainDoctor = initRole("MAIN_DOCTOR");
        Role roleHrManager = initRole("HR_MANAGER");

        Department department = initDepartment("Surgery", null);

        HrManager hrManager = initHrManager(roleHrManager);

        Doctor doctor = initDoctor("muka@mail.com", "1", "Mukagali",
                "Orazbakov", "Nurgaliuly", LocalDate.now().minusYears(25), roleMainDoctor, department);

        Doctor doctor1 = initDoctor("muk@mail.com", "1", "Mukagali",
                "Orazbakov", "Nurgaliuly", LocalDate.now().minusYears(25), roleMainDoctor, department);

        Doctor doctor2 = initDoctor("muka@mail.co", "1", "Mukagali",
                "Orazbakov", "Nurgaliuly", LocalDate.now().minusYears(25), roleChiefDoctor, department);

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        //В организации уже две главных врачей
        mockMvc.perform(post("/api/hr_manager/addMainDoctorForOrganization")
                        .param("doctorId", doctor2.getId().toString())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(412)))
                .andExpect(jsonPath("$.text", Is.is("В организации уже две главных врачей")));

    }

}
