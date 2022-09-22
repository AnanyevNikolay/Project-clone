package ru.mis2022.controllers.patient;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.service.entity.TalonService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PatientTalonsRestControllerIT extends ContextIT {

    @Autowired
    RoleService roleService;
    @Autowired
    PatientService patientService;
    @Autowired
    TalonService talonService;
    @Autowired
    DoctorService doctorService;

    Role initRole(String name) {
        return roleService.save(Role.builder()
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

    Patient initPatient(Role role, String email) {
        return patientService.persist(new Patient(
                email,
                String.valueOf("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role,
                "passport",
                "polis",
                "snils",
                "address"));
    }

    Talon initTalon(LocalDateTime time, Doctor doctor, Patient patient) {
        return new Talon(time, doctor, patient);
    }

    @Test
    public void getAllTalonsPatientTest() throws Exception {

        Role role = initRole("PATIENT");
        Role role1 = initRole("DOCTOR");
        Patient patient1 = initPatient(role, "patient1@email.com");
        Patient patient2 = initPatient(role, "patient2@email.com");
        Doctor doctor1 = initDoctor(role1, null, null, "doctor1@email.com");
        Doctor doctor2 = initDoctor(role1, null, null, "doctor2@email.com");
        Talon doc1talon1 = initTalon(null, doctor1, null);
        Talon doc1talon2 = initTalon(null, doctor1, null);
        Talon doc1talon3 = initTalon(null, doctor1, null);
        Talon doc1talon4 = initTalon(null, doctor1, null);
        Talon doc2talon1 = initTalon(null, doctor2, null);
        Talon doc2talon2 = initTalon(null, doctor2, null);
        Talon doc2talon3 = initTalon(null, doctor2, null);
        Talon doc2talon4 = initTalon(null, doctor2, null);

        doc1talon1 = talonService.save(doc1talon1);
        doc1talon2 = talonService.save(doc1talon2);
        doc1talon3 = talonService.save(doc1talon3);
        doc1talon4 = talonService.save(doc1talon4);
        doc2talon1 = talonService.save(doc2talon1);
        doc2talon2 = talonService.save(doc2talon2);
        doc2talon3 = talonService.save(doc2talon3);
        doc2talon4 = talonService.save(doc2talon4);

        // Запись пациента2 на 2 талона к 2 докторам

        doc1talon1.setPatient(patient2);
        doc1talon2.setPatient(patient2);
        doc2talon1.setPatient(patient2);
        doc2talon2.setPatient(patient2);

//        Проверка, что пациент1 пациент не записан

        accessToken = tokenUtil.obtainNewAccessToken(patient1.getEmail(), "1", mockMvc);

        mockMvc.perform(get("/api/patient/talons")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(0)));

        // Запись пациента1 на 2 талона к 2 докторам

        doc1talon3.setPatient(patient1);
        doc1talon4.setPatient(patient1);
        doc2talon3.setPatient(patient1);
        doc2talon4.setPatient(patient1);

        mockMvc.perform(get("/api/patient/talons")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.length()", Is.is(4)))

                .andExpect(jsonPath("$.data[0].id", Is.is(doc1talon3.getId().intValue())))
                .andExpect(jsonPath("$.data[0].doctorId", Is.is(doctor1.getId().intValue())))
                .andExpect(jsonPath("$.data[0].patient.id", Is.is(patient1.getId().intValue())))

                .andExpect(jsonPath("$.data[1].id", Is.is(doc1talon4.getId().intValue())))
                .andExpect(jsonPath("$.data[1].doctorId", Is.is(doctor1.getId().intValue())))
                .andExpect(jsonPath("$.data[1].patient.id", Is.is(patient1.getId().intValue())))

                .andExpect(jsonPath("$.data[2].id", Is.is(doc2talon3.getId().intValue())))
                .andExpect(jsonPath("$.data[2].doctorId", Is.is(doctor2.getId().intValue())))
                .andExpect(jsonPath("$.data[2].patient.id", Is.is(patient1.getId().intValue())))

                .andExpect(jsonPath("$.data[3].id", Is.is(doc2talon4.getId().intValue())))
                .andExpect(jsonPath("$.data[3].doctorId", Is.is(doctor2.getId().intValue())))
                .andExpect(jsonPath("$.data[3].patient.id", Is.is(patient1.getId().intValue())));
    }

    @Test
    public void cancelRecordTalonsTest() throws Exception {

        Role role = initRole("PATIENT");
        Role role1 = initRole("DOCTOR");
        Patient patient = initPatient(role, "patient1test@email.com");
        Patient patient2 = initPatient(role, "patient2test@email.com");
        Doctor doctor = initDoctor(role1, null, null, "doctor1test@email.com");
        talonService.persistTalonsForDoctor(doctor, 14, 4, null, null);
        Talon talon = initTalon(null, doctor, patient);
        Talon talon2 = initTalon(LocalDateTime.now(), doctor, patient2);
        talon = talonService.save(talon);
        talon2 = talonService.save(talon2);

        accessToken = tokenUtil.obtainNewAccessToken(patient.getEmail(), "1", mockMvc);

        if (talon.getPatient() == null || talon2.getPatient() == null || talon.getId() == null || talon2.getId() == null) {
            throw new Exception("Неправильно созданы талоны для теста");
        }

        //Проверка на несуществующий талон
        mockMvc.perform(patch("/api/patient/talons/{talonId}", 150000)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(402)))
                .andExpect(jsonPath("$.text", Is.is("Талона с таким id нет!")));
//              .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //Пациент пытается удалить чужую запись
        mockMvc.perform(patch("/api/patient/talons/{talonId}", talon2.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(403)))
                .andExpect(jsonPath("$.text", Is.is("Пациент не записан по этому талону")));
//               .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //эндпоинт отработал успешно
        mockMvc.perform(patch("/api/patient/talons/{talonId}", talon.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

//        Запись на талон успешно удалена
        if (talon.getPatient() != null || talon2.getPatient() == null) {
            throw new Exception("Запись по талону не удалена");
        }
    }
}
