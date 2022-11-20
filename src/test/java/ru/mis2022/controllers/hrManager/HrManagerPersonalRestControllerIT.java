package ru.mis2022.controllers.hrManager;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.HrManager;
import ru.mis2022.models.entity.MedicalOrganization;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.User;
import ru.mis2022.models.entity.Vacation;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.repositories.HrManagerRepository;
import ru.mis2022.repositories.PersonalHistoryRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.repositories.UserRepository;
import ru.mis2022.repositories.VacationRepository;
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
    @Autowired
    PersonalHistoryRepository personalHistoryRepository;
    @Autowired
    VacationRepository vacationRepository;

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

    User initUserForTestByFindEmployeesGoVacation(String firstName, String email, Role role, PersonalHistory personalHistory) {
        return userRepository.save(new User(
                email, firstName, role, personalHistory));
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
    Doctor initDoctor(String firstName,
                      String email,
                      Role role,
                      PersonalHistory personalHistory,
                      Department department) {
        return doctorRepository.save(new Doctor(
            firstName,
            email,
            role,
            personalHistory,
            department
        ));
    }

    PersonalHistory initPersonalHistory(LocalDate dateOfEmployment,
                                        LocalDate dateOfDismissal) {
        return personalHistoryRepository.save(new PersonalHistory(
           dateOfEmployment,
           dateOfDismissal
        ));
    }
    Vacation initVacations(LocalDate dateFrom, LocalDate dateTo, PersonalHistory personalHistory) {
        return vacationRepository.save(new Vacation(
           dateFrom,
           dateTo,
           personalHistory
        ));
    }


    PersonalHistory initPersonalHistory() {
        return personalHistoryRepository.save(PersonalHistory.builder()
                .dateOfEmployment(null)
                .dateOfDismissal(null)
                .build());
    }

    Vacation initVacation(LocalDate start, LocalDate end, PersonalHistory personalHistory) {
        return vacationRepository.save(Vacation.builder()
                .dateFrom(start)
                .dateTo(end)
                .personalHistory(personalHistory)
                .build());
    }

    @AfterEach
    public void clear() {
        hrManagerRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        vacationRepository.deleteAll();
        personalHistoryRepository.deleteAll();
    }

    @Test
    public void findAllEmployeesWhoGoVacationInRange() throws Exception {

        LocalDate start1 = LocalDate.now().plusDays(7);
        LocalDate start2 = LocalDate.now().plusWeeks(2);
        LocalDate start3 = LocalDate.now().plusWeeks(5);

        LocalDate end1 = start1.plusDays(30);
        LocalDate end2 = start2.plusDays(30);
        LocalDate end3 = start3.plusDays(30);

        Role roleHrManager = initRole("HR_MANAGER");
        Role roleDoctor = initRole("DOCTOR");

        PersonalHistory personalHistory1 = initPersonalHistory();
        PersonalHistory personalHistory2 = initPersonalHistory();

        // Инитим даты отпуска с персональной историей
        Vacation vacation1 = initVacation(start1, end1, personalHistory1);
        Vacation vacation2 = initVacation(start2, end2, personalHistory2);
        Vacation vacation3 = initVacation(start3, end3, personalHistory2);

        HrManager hrManager = initHrManager(roleHrManager);
        User user1 = initUserForTestByFindEmployeesGoVacation("firstUser1", "user1@email.com", roleDoctor, personalHistory1);
        User user2 = initUserForTestByFindEmployeesGoVacation("firstUser2", "user2@email.com", roleDoctor, personalHistory2);

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        // Ближайшие 20 дней в отпуск должны пойти 2 сотрудника
        mockMvc.perform(get("/api/hr_manager/findAllEmployeesWhoGoVacationInRange")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("daysCount", objectMapper.writeValueAsString(20))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(2)))

                .andExpect(jsonPath("$.data[0].id", Is.is(intValue(user1.getId()))))
                .andExpect(jsonPath("$.data[0].firstName", Is.is(user1.getFirstName())))
                .andExpect(jsonPath("$.data[0].email", Is.is(user1.getEmail())))

                .andExpect(jsonPath("$.data[1].id", Is.is(intValue(user2.getId()))))
                .andExpect(jsonPath("$.data[1].firstName", Is.is(user2.getFirstName())))
                .andExpect(jsonPath("$.data[1].email", Is.is(user2.getEmail())))
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
        ;

        // Ближайшие 5 дней никто в отпуск не идет
        mockMvc.perform(get("/api/hr_manager/findAllEmployeesWhoGoVacationInRange")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .param("daysCount", objectMapper.writeValueAsString(5))
        )
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(0)))
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                ;

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

    @Test
    public void daysForVacationsTest() throws Exception {

        Role roleHrManager = initRole("HR_MANAGER");
        Role roleDoctor = initRole("DOCTOR");
        Department department = initDepartment("Surgery", null);
        HrManager hrManager = initHrManager(roleHrManager);


        // Доктор устроился в Январе, он отдыхал 1 день и ему до конца года положено 32 для отпуска
        PersonalHistory personalHistory1 =
                initPersonalHistory(LocalDate.of(2022, 1, 1),null);

        Vacation vacation = initVacations(LocalDate.of(2022, 12, 1),
                LocalDate.of(2022, 12, 2), personalHistory1);

        Doctor doctor = initDoctor("Ivan", "ivan@mail.ru", roleDoctor, personalHistory1, department);

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        mockMvc.perform(get("/api/hr_manager/daysForVacations")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data[0].daysForVacations", Is.is(32)));


        // Доктор устроился в Декабре, он отдыхал 10 дней и он "перегулял" -9 дней
        PersonalHistory personalHistory2 =
                initPersonalHistory(LocalDate.of(2022, 12, 1),null);

        Vacation vacation2 = initVacations(LocalDate.of(2022, 12, 1),
                LocalDate.of(2022, 12, 10), personalHistory2);

        Doctor doctor2 = initDoctor("Ivan", "ivan@mail", roleDoctor, personalHistory2, department);

        mockMvc.perform(get("/api/hr_manager/daysForVacations")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data[1].daysForVacations", Is.is(-9)));
    }

    @Test
    public void daysForVacationsForEmptyListTest() throws Exception {

        Role roleHrManager = initRole("HR_MANAGER");
        Role roleDoctor = initRole("DOCTOR");
        Department department = initDepartment("Surgery", null);
        HrManager hrManager = initHrManager(roleHrManager);

        //У Доктора нет заявлении, ждем что никому в отпуск не надо
        PersonalHistory personalHistory1 =
                initPersonalHistory(LocalDate.of(2022, 1, 1),null);

        Doctor doctor = initDoctor("Ivan", "ivan@mail.ru", roleDoctor, personalHistory1, department);

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);

        mockMvc.perform(get("/api/hr_manager/daysForVacations")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)));

    }

}
