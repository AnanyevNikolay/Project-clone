package ru.mis2022.controllers.registrar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.Registrar;
import ru.mis2022.models.entity.Role;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.service.entity.RegistrarService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.util.ContextIT;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegistrarPatientRestControllerIT extends ContextIT {

    private final PatientService patientService;
    private final RegistrarService registrarService;
    private final RoleService roleService;

    @Autowired
    public RegistrarPatientRestControllerIT(PatientService patientService, RegistrarService registrarService, RoleService roleService, DepartmentService departmentService) {
        this.patientService = patientService;
        this.registrarService = registrarService;
        this.roleService = roleService;
    }

    Role initRole(String name) {
        return roleService.save(Role.builder().name(name).build());
    }

    Patient initPatient(Role role, String firstName, String lastName, String polis, String snils) {
        return patientService.persist(new Patient(
                "patient" + polis + "@email.com",
                String.valueOf("1"),
                firstName,
                lastName,
                "surname",
                LocalDate.now().minusYears(20),
                role,
                "passport",
                polis,
                snils,
                "address"));
    }

    Registrar initRegistrar(Role role) {
        return registrarService.persist(new Registrar(
                "registrar1@email.com",
                String.valueOf("1"),
                "f_name",
                "l_name",
                "surname",
                LocalDate.now().minusYears(20),
                role
        ));
    }

    @Test
    public void searchPatientByFirstNameOrLastNameOrPolisOrSnilsTest() throws Exception {
        Role patientRole = initRole("PATIENT");
        Role registrarRole = initRole("REGISTRAR");
        Registrar registrar = initRegistrar(registrarRole);
        Patient patient0 = initPatient(patientRole, "Adelaide", "Harris", "995226719", "28038");
        Patient patient1 = initPatient(patientRole, "Myla", "Jones", "356095322", "32048");
        Patient patient2 = initPatient(patientRole, "Zuri", "Miller", "395877782", "17724");
        Patient patient3 = initPatient(patientRole, "Sloane", "Mitchell", "522883467", "55998");
        Patient patient4 = initPatient(patientRole, "Avianna", "Taylor", "215443012", "46681");
        Patient patient5 = initPatient(patientRole, "Kade", "Moore", "616220551", "71851");
        Patient patient6 = initPatient(patientRole, "Kane", "King", "253989679", "52039");
        Patient patient7 = initPatient(patientRole, "Aspen", "Campbell", "958533983", "84106");
        Patient patient8 = initPatient(patientRole, "Diana", "Allen", "489648930", "84317");
        Patient patient9 = initPatient(patientRole, "Sloane", "Norman", "926817729", "54611");
        Patient patient10 = initPatient(patientRole, "Jaden", "Watts", "393686851", "25940");
        Patient patient11 = initPatient(patientRole, "Veronica", "Brown", "257137787", "70885");
        Patient patient12 = initPatient(patientRole, "Blake", "Wood", "845415373", "18290");
        Patient patient13 = initPatient(patientRole, "Austin", "Cox", "671731571", "14519");
        Patient patient14 = initPatient(patientRole, "Theo", "Watkins", "764964992", "62370");
        Patient patient15 = initPatient(patientRole, "Tristan", "Peterson", "396812230", "31968");
        Patient patient16 = initPatient(patientRole, "Izabella", "Fuller", "225665690", "52782");
        Patient patient17 = initPatient(patientRole, "Walter", "Meyer", "143687022", "99926");
        Patient patient18 = initPatient(patientRole, "Jaxson", "Smith", "649130652", "40360");
        Patient patient19 = initPatient(patientRole, "Emmanuel", "Hanson", "326683018", "33002");
        Patient patient20 = initPatient(patientRole, "Jared", "Edwards", "518066112", "48416");
        Patient patient21 = initPatient(patientRole, "Josue", "Barrett", "444548849", "71818");
        Patient patient22 = initPatient(patientRole, "Felix", "Patterson", "984657834", "35328");
        Patient patient23 = initPatient(patientRole, "Asher", "Morris", "271322997", "87041");
        Patient patient24 = initPatient(patientRole, "Jonathan", "Kelly", "476445296", "44718");
        Patient patient25 = initPatient(patientRole, "Julia", "Ward", "872859189", "30790");
        Patient patient26 = initPatient(patientRole, "Zuri", "Watkins", "684866890", "72281");
        Patient patient27 = initPatient(patientRole, "Penelope", "Brooks", "486727058", "77218");
        Patient patient28 = initPatient(patientRole, "Sophie", "Carter", "306261089", "86137");
        Patient patient29 = initPatient(patientRole, "Jonah", "Larson", "868736396", "29617");

        accessToken = tokenUtil.obtainNewAccessToken(registrar.getEmail(), "1", mockMvc);

        mockMvc.perform(get("/api/registrar/patient?firstName={firstName}&lastName={lastName}&polis={polis}&snils={snils}&offset={offset}",
                        null, null, null, null, "1")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

        int a = 1;

    }
}
