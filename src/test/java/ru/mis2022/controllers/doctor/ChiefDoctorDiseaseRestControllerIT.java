package ru.mis2022.controllers.doctor;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Disease;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Role;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.repositories.DiseaseRepository;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.models.entity.Role.RolesEnum.CHIEF_DOCTOR;

public class ChiefDoctorDiseaseRestControllerIT extends ContextIT {

    @Autowired
    DiseaseRepository diseaseRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    DepartmentRepository departmentRepository;

    Role initRole(String name) {
        return roleRepository.save(new Role(name));
    }

    Department initDepartment(String name) {
        return departmentRepository.save(Department
                .builder()
                .name(name)
                .build());
    }

    Doctor initDoctor(String email, Role role, Department department) {
        return doctorRepository.save(new Doctor(
                email,
                passwordEncoder.encode("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                department
        ));
    }

    Disease initDisease(String identifier, Department department, String name, boolean disabled) {
        return diseaseRepository.save(Disease
                .builder()
                .identifier(identifier)
                .department(department)
                .name(name)
                .disabled(disabled)
                .build());
    }

    Disease initDisease(String identifier, Department department, String name) {
        return diseaseRepository.save(Disease
                .builder()
                .identifier(identifier)
                .department(department)
                .name(name)
                .build());
    }

    @AfterEach
    void clear() {
        doctorRepository.deleteAll();
        diseaseRepository.deleteAll();
        roleRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    public void changeDisabledOnTrueTest() throws Exception {
        Role roleChiefDoctor = initRole(CHIEF_DOCTOR.name());
        Department department1 = initDepartment("department1");
        Disease disease1 = initDisease("963bBMAKHUU3Bi4", department1, "disease1");
        Doctor doctor = initDoctor("doctor0@mail.ru", roleChiefDoctor, department1);

        accessToken = tokenUtil.obtainNewAccessToken(doctor.getEmail(), "1", mockMvc);

        // Нормальный сценарий!
        mockMvc.perform(patch("/api/chief/doctor/changeDisabledOnTrue/{id}", disease1.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code").value(200))

                .andExpect(jsonPath("$.data.id").value(disease1.getId()))
                .andExpect(jsonPath("$.data.identifier").value(disease1.getIdentifier()))
                .andExpect(jsonPath("$.data.name").value(disease1.getName()))
                .andExpect(jsonPath("$.data.disabled").value(!disease1.isDisabled()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Заболевания не существует! 410
        mockMvc.perform(patch("/api/chief/doctor/changeDisabledOnTrue/{id}", 8888)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(410)))
                .andExpect(jsonPath("$.text", Is.is("Заболевания не существует.")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        Department department2 = initDepartment("department2");
        Disease disease2 = initDisease("963bBMAKHUU3Bi444", department2, "disease1");

        // Заболеваним не занимается данный доктор! 411
        mockMvc.perform(patch("/api/chief/doctor/changeDisabledOnTrue/{id}", disease2.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(411)))
                .andExpect(jsonPath("$.text", Is.is("Заболеваним не занимается данный доктор.")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    public void changeDisabledOnFalseTest() throws Exception {
        Role roleChiefDoctor = initRole(CHIEF_DOCTOR.name());
        Department department1 = initDepartment("department1");
        Disease disease1 = initDisease("963bBMAKHUU3Bi4", department1, "disease1", true);
        Doctor doctor = initDoctor("doctor0@mail.ru", roleChiefDoctor, department1);

        accessToken = tokenUtil.obtainNewAccessToken(doctor.getEmail(), "1", mockMvc);

        // Нормальный сценарий!
        mockMvc.perform(patch("/api/chief/doctor/changeDisabledOnFalse/{id}", disease1.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code").value(200))

                .andExpect(jsonPath("$.data.id").value(disease1.getId()))
                .andExpect(jsonPath("$.data.identifier").value(disease1.getIdentifier()))
                .andExpect(jsonPath("$.data.name").value(disease1.getName()))
                .andExpect(jsonPath("$.data.disabled").value(!disease1.isDisabled()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // Заболевания не существует! 410
        mockMvc.perform(patch("/api/chief/doctor/changeDisabledOnFalse/{id}", 8888)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(410)))
                .andExpect(jsonPath("$.text", Is.is("Заболевания не существует.")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        Department department2 = initDepartment("department2");
        Disease disease2 = initDisease("963bBMAKHUU3Bi444", department2, "disease1", true);

        // Заболеваним не занимается данный доктор! 411
        mockMvc.perform(patch("/api/chief/doctor/changeDisabledOnFalse/{id}", disease2.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(411)))
                .andExpect(jsonPath("$.text", Is.is("Заболеваним не занимается данный доктор.")));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
    }
}
