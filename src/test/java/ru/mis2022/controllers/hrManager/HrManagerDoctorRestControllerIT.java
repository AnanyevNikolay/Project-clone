package ru.mis2022.controllers.hrManager;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mis2022.models.dto.doctor.DoctorDto;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.HrManager;
import ru.mis2022.models.entity.Role;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.repositories.HrManagerRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.service.entity.MailService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HrManagerDoctorRestControllerIT extends ContextIT {

    @MockBean
    MailService mailService;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    HrManagerRepository hrManagerRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void clear() {
        doctorRepository.deleteAll();
        hrManagerRepository.deleteAll();
        roleRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    Department initDepartment() {
        Department department = new Department("DepartmentTest");
        return departmentRepository.save(department);
    }

    Role initRole(String name) {
        return roleRepository.save(Role.builder()
                .name(name)
                .build());
    }

    HrManager initHrManager(Role role) {
        return hrManagerRepository.save(new HrManager(
                "hrManager@email.com",
                passwordEncoder.encode("1"),
                "f_name",
                "l_name",
                "surName",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    Doctor initDoctor(Role role, Department department) {
        return doctorRepository.save(new Doctor(
                "doctor@email.com",
                passwordEncoder.encode("1"),
                "f_name",
                "l_name",
                "surName",
                LocalDate.now().minusYears(20),
                role,
                department
        ));
    }

    DoctorDto getDoctorDto(Long id, String email) {
        return new DoctorDto(
                id,
                email,
                "2",
                "f_name",
                "l_name",
                "surName",
                "01.01.1980",
                "DOCTOR",
                "DepartmentTest",
                0
        );
    }

    @Test
    public void hrManagerCreateDoctor() throws Exception {
        Role roleHrManager = initRole("HR_MANAGER");
        HrManager hrManager = initHrManager(roleHrManager);
        Department dep = initDepartment();
        Role roleDoctor = initRole("DOCTOR");
        DoctorDto validDtoCreate = getDoctorDto(null, "doctor2@email.com");
        DoctorDto noValidIdDtoCreate = getDoctorDto(2L, "doctor2@email.com");
        DoctorDto noValidEmailDtoCreate = getDoctorDto(null, "123456");
        DoctorDto noValidExistEmailDtoCreate = getDoctorDto(null, "doctor2@email.com");
        DoctorDto noValidNoExistDepartmentDtoCreate = getDoctorDto(null, "doctor2@email.com");
        Mockito.doNothing().when(mailService).sendRegistrationInviteByEmail(any(), any());

        accessToken = tokenUtil.obtainNewAccessToken(hrManager.getEmail(), "1", mockMvc);
        // Валидный ДТО доктора, создание доктора
        mockMvc.perform(post("/api/hr_manager/doctor/{departmentId}/createDoctor", dep.getId())
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(validDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.data.id", Is.is(Matchers.notNullValue())))
                .andExpect(jsonPath("$.data.email", Is.is("doctor2@email.com")))
                .andExpect(jsonPath("$.data.lastName", Is.is("l_name")))
                .andExpect(jsonPath("$.data.firstName", Is.is("f_name")))
                .andExpect(jsonPath("$.data.surname", Is.is("surName")))
                .andExpect(jsonPath("$.data.role", Is.is("DOCTOR")))
                .andExpect(jsonPath("$.data.birthday", Is.is("01.01.1980")))
                .andExpect(jsonPath("$.data.department", Is.is("DepartmentTest")))
                .andExpect(jsonPath("$.data.daysForVacations", Is.is(0)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Не валидный id в ДТО администратора, создание доктора
        mockMvc.perform(post("/api/hr_manager/doctor/{departmentId}/createDoctor", dep.getId())
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidIdDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(400)))
                .andExpect(jsonPath("$.text", Is.is("id должен быть равен null")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Не валидный email в ДТО администратора, создание доктора
        mockMvc.perform(post("/api/hr_manager/doctor/{departmentId}/createDoctor", dep.getId())
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidEmailDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(400)))
                .andExpect(jsonPath("$.text", Is.is(
                        "email должен быть корректным адресом электронной почты")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Существующий email в ДТО администратора, создание доктора
        mockMvc.perform(post("/api/hr_manager/doctor/{departmentId}/createDoctor", dep.getId())
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidExistEmailDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(412)))
                .andExpect(jsonPath("$.text", Is.is(
                        "Такой адрес электронной почты уже используется!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Не существующее отделение
        mockMvc.perform(post("/api/hr_manager/doctor/{departmentId}/createDoctor", 1234L)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(noValidNoExistDepartmentDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(411)))
                .andExpect(jsonPath("$.text", Is.is("Такого отделения не существует!")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        Assertions.assertEquals(validDtoCreate.getEmail(), "doctor2@email.com");
        Assertions.assertEquals(validDtoCreate.getDepartment(), dep.getName());
    }
}