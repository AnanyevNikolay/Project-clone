package ru.mis2022.controllers.patient;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.repositories.PatientRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.repositories.TalonRepository;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mis2022.utils.DateFormatter.DATE_TIME_FORMATTER;


public class PatientTalonsRestControllerIT extends ContextIT {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    TalonRepository talonRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    DepartmentRepository departmentRepository;

    @AfterEach
    public void clear() {
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
        talonRepository.deleteAll();
        departmentRepository.deleteAll();
        roleRepository.deleteAll();
    }


    Role initRole(String name) {
        return roleRepository.save(Role.builder()
                .name(name)
                .build());
    }

    Doctor initDoctor(Role role, Department department, PersonalHistory personalHistory, String email) {
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

    Patient initPatient(Role role, String email) {
        return patientRepository.save(new Patient(
                email,
                passwordEncoder.encode("1"),
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

    Department initDepartment(String name) {
        return departmentRepository.save(Department.builder()
                .name(name)
                .build());
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

        doc1talon1 = talonRepository.save(doc1talon1);
        doc1talon2 = talonRepository.save(doc1talon2);
        doc1talon3 = talonRepository.save(doc1talon3);
        doc1talon4 = talonRepository.save(doc1talon4);
        doc2talon1 = talonRepository.save(doc2talon1);
        doc2talon2 = talonRepository.save(doc2talon2);
        doc2talon3 = talonRepository.save(doc2talon3);
        doc2talon4 = talonRepository.save(doc2talon4);

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

        doc1talon3 = talonRepository.save(doc1talon3);
        doc1talon4 = talonRepository.save(doc1talon4);
        doc2talon3 = talonRepository.save(doc2talon3);
        doc2talon4 = talonRepository.save(doc2talon4);

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
                .andExpect(jsonPath("$.data[0].patientDto.id", Is.is(patient1.getId().intValue())))

                .andExpect(jsonPath("$.data[1].id", Is.is(doc1talon4.getId().intValue())))
                .andExpect(jsonPath("$.data[1].doctorId", Is.is(doctor1.getId().intValue())))
                .andExpect(jsonPath("$.data[1].patientDto.id", Is.is(patient1.getId().intValue())))

                .andExpect(jsonPath("$.data[2].id", Is.is(doc2talon3.getId().intValue())))
                .andExpect(jsonPath("$.data[2].doctorId", Is.is(doctor2.getId().intValue())))
                .andExpect(jsonPath("$.data[2].patientDto.id", Is.is(patient1.getId().intValue())))

                .andExpect(jsonPath("$.data[3].id", Is.is(doc2talon4.getId().intValue())))
                .andExpect(jsonPath("$.data[3].doctorId", Is.is(doctor2.getId().intValue())))
                .andExpect(jsonPath("$.data[3].patientDto.id", Is.is(patient1.getId().intValue())));
    }

    @Test
    public void cancelRecordTalonsTest() throws Exception {

        Role role = initRole("PATIENT");
        Role role1 = initRole("DOCTOR");
        Patient patient = initPatient(role, "patient1test@email.com");
        Patient patient2 = initPatient(role, "patient2test@email.com");
        Doctor doctor = initDoctor(role1, null, null, "doctor1test@email.com");
        // строка снизу возможно не нужна, так как тесты работают и без нее
//        talonService.persistTalonsForDoctor(doctor, 14, 4, null, null);
        Talon talon = initTalon(null, doctor, patient);
        Talon talon2 = initTalon(LocalDateTime.now(), doctor, patient2);
        talon = talonRepository.save(talon);
        talon2 = talonRepository.save(talon2);

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
                .andExpect(jsonPath("$.text", Is.is("Пациент не записан по этому талону")))
               .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        //эндпоинт отработал успешно
        mockMvc.perform(patch("/api/patient/talons/{talonId}", talon.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))
                .andExpect(jsonPath("$.data.patientDto").value(Matchers.nullValue()));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));


    }

    @Test
    public void recordOnTalon() throws Exception {
        Role patientRole = initRole("PATIENT");
        Role doctorRole = initRole("DOCTOR");
        Department department1 = initDepartment("Department1");
        Department department2 = initDepartment("Department2");
        Patient patient = initPatient(patientRole, "Patient@gmail.com");
        Patient patient2 = initPatient(patientRole, "Patient2@gmail.com");
        Doctor doctor = initDoctor(doctorRole, department1, null, "Doctor@gmail.com");
        Doctor doctor2 = initDoctor(doctorRole, department2, null, "Doctor2@gmail.com");
        Talon talon1 = initTalon(LocalDateTime.now(), doctor, null);
        Talon sameDepTalon = initTalon(LocalDateTime.now().plusHours(1), doctor, null);
        Talon busyTalon = initTalon(LocalDateTime.now().plusHours(2), doctor, patient2);
        Talon doc2Talon = initTalon(LocalDateTime.now().plusHours(3), doctor2, null);

        talonRepository.save(talon1);
        talonRepository.save(sameDepTalon);
        talonRepository.save(busyTalon);
        talonRepository.save(doc2Talon);
        departmentRepository.save(department1);
//        doctorRepository.save(doctor);

        accessToken = tokenUtil.obtainNewAccessToken(patient.getEmail(), "1", mockMvc);

        //УСПЕШНАЯ ЗАПИСЬ НА ТАЛОН
        mockMvc.perform(patch("/api/patient/talons/recordOnTalon")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("talonId", talon1.getId().toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Is.is(true)))
                .andExpect(jsonPath("$.code", Is.is(200)))

                .andExpect(jsonPath("$.data.patientDto.id", Is.is(patient.getId().intValue())))
                .andExpect(jsonPath("$.data.doctorId", Is.is(doctor.getId().intValue())))
                .andExpect(jsonPath("$.data.id", Is.is(talon1.getId().intValue())))
                .andExpect(jsonPath("$.data.time", Is.is(DATE_TIME_FORMATTER.format(talon1.getTime()))));
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        // ToDo Починить тест
        //ПОВТОРНАЯ ЗАПИСЬ В ТОЖЕ ОТДЕЛЕНИЕ
//        patient.setTalons(new ArrayList<>(){{ add(talon1); }});
//        talon1.setPatient(patient);
//        talonRepository.save(talon1);

//        mockMvc.perform(patch("/api/patient/talons/recordOnTalon")
//                        .header("Authorization", accessToken)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("talonId", sameDepTalon.getId().toString())
//                )
//                    .andExpect(status().is(400))
//                .andExpect(jsonPath("$.success", Is.is(false)))
//                .andExpect(jsonPath("$.code", Is.is(406)))
//                .andExpect(jsonPath("$.text", Is.is("Вы уже записаны к врачу из данного отделения")));

        //ТАЛОН УЖЕ ЗАНЯТ
        mockMvc.perform(patch("/api/patient/talons/recordOnTalon")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("talonId", busyTalon.getId().toString())
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(403)))
                .andExpect(jsonPath("$.text", Is.is("Талон уже занят")));

        //ТАЛОНА С ТАКИМ ID НЕ СУЩЕСВТУЕТ
        mockMvc.perform(patch("/api/patient/talons/recordOnTalon")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("talonId", "88888888")
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(402)))
                .andExpect(jsonPath("$.text", Is.is("Талона с данным id не существует")));

        //ВРАЧА НА ДАННЫЙ ТАЛОН НЕ НАЗНАЧЕН
        talon1.setDoctor(null);
        talonRepository.save(talon1);


        mockMvc.perform(patch("/api/patient/talons/recordOnTalon")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("talonId", talon1.getId().toString())
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.success", Is.is(false)))
                .andExpect(jsonPath("$.code", Is.is(405)))
                .andExpect(jsonPath("$.text", Is.is("Врач на данный талон не назначен")));
    }
}
