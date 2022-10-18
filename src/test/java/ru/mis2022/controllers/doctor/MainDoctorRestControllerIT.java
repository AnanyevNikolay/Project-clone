package ru.mis2022.controllers.doctor;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Role;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo list 6 написать метод clear() дабы избавиться от аннотации Transactional
//  в конце каждого теста дописать запрос проверяющий что все действительно было
//  проинициализированно в бд. по аналогии с DoctorPatientRestControllerIT#registerPatientInTalon

public class MainDoctorRestControllerIT extends ContextIT {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    PasswordEncoder encoder;

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

    Doctor initDoctor(Role role, Department department, PersonalHistory personalHistory) {
        Doctor doctor = new Doctor(
                "doctor1@email.com",
                String.valueOf("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                department
        );
        doctor.setPassword(encoder.encode(doctor.getPassword()));
        return doctorRepository.save(doctor);
    }

    @AfterEach
    public void clear() {
        doctorRepository.deleteAll();
        roleRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    public void getCurrentUserTest() throws Exception {
        Role role = initRole("MAIN_DOCTOR");
        Department department = initDepartment("Therapy");
        Doctor doctor1 = initDoctor(role, department, null);


        accessToken = tokenUtil.obtainNewAccessToken(doctor1.getEmail(), "1", mockMvc);
        mockMvc.perform(get("/api/main-doctor/mainPage/current")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.roleName", Is.is("MAIN_DOCTOR")))
                .andExpect(jsonPath("$.data.lastName", Is.is("l_name")))
                .andExpect(jsonPath("$.data.firstName", Is.is("f_name")))
                .andExpect(jsonPath("$.data.departmentName", Is.is("Therapy")))
                .andExpect(jsonPath("$.data.birthday", Matchers.notNullValue()));
//              .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Проверка идентичности сохраненного пользователя в БД
        Doctor doctorInDepartment = entityManager.createQuery("""
                select d from Doctor d
                    left join fetch Role r on d.role.id = r.id
                    left join Department dep on d.department.id = dep.id
                where d.id = :idDoc and r.id = :idRole and dep.id = :idDep
                """, Doctor.class)
                .setParameter("idDoc", doctor1.getId())
                .setParameter("idRole", role.getId())
                .setParameter("idDep", department.getId())
                .getSingleResult();

        Assertions.assertEquals(doctorInDepartment, doctor1);
        Assertions.assertNotNull(doctorInDepartment.getDepartment());
    }

}