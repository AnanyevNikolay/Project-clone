package ru.mis2022.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.mis2022.models.entity.Account;
import ru.mis2022.models.entity.Administrator;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.Attestation;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.Diploma;
import ru.mis2022.models.entity.Disease;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.entity.HrManager;
import ru.mis2022.models.entity.MedicalOrganization;
import ru.mis2022.models.entity.MedicalService;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.PersonalHistory;
import ru.mis2022.models.entity.PriceOfMedicalService;
import ru.mis2022.models.entity.Registrar;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.models.entity.Vacation;
import ru.mis2022.models.entity.Visit;
import ru.mis2022.models.entity.Yet;
import ru.mis2022.repositories.AccountRepository;
import ru.mis2022.repositories.AdministratorRepository;
import ru.mis2022.repositories.AppealRepository;
import ru.mis2022.repositories.AttestationRepository;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.repositories.DiplomaRepository;
import ru.mis2022.repositories.DiseaseRepository;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.repositories.EconomistRepository;
import ru.mis2022.repositories.HrManagerRepository;
import ru.mis2022.repositories.MedicalOrganizationRepository;
import ru.mis2022.repositories.MedicalServiceRepository;
import ru.mis2022.repositories.PatientRepository;
import ru.mis2022.repositories.PersonalHistoryRepository;
import ru.mis2022.repositories.PriceOfMedicalServiceRepository;
import ru.mis2022.repositories.RegistrarRepository;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.repositories.TalonRepository;
import ru.mis2022.repositories.VacationRepository;
import ru.mis2022.repositories.VisitRepository;
import ru.mis2022.repositories.YetRepository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.mis2022.models.entity.Role.RolesEnum;


@Component
@ConditionalOnExpression("${mis.property.runInitialize:true}")
public class DataInitializer {

    @Value("${mis.property.doctorSchedule}")
    private Integer numberOfDaysDoctor;
    @Value("${mis.property.talon}")
    private Integer numbersOfTalons;
    @Value("${mis.property.patientSchedule}")
    private Integer numberOfDaysPatient;

    @Autowired
    private AppealRepository appealRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private EconomistRepository economistRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MedicalOrganizationRepository medicalOrganizationRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DiplomaRepository diplomaRepository;
    @Autowired
    private RegistrarRepository registrarRepository;
    @Autowired
    private AdministratorRepository administratorRepository;
    @Autowired
    private HrManagerRepository hrManagerRepository;
    @Autowired
    private AttestationRepository attestationRepository;
    @Autowired
    private PersonalHistoryRepository personalHistoryRepository;
    @Autowired
    private TalonRepository talonRepository;
    @Autowired
    private MedicalServiceRepository medicalServiceRepository;
    @Autowired
    private DiseaseRepository diseaseRepository;
    @Autowired
    private YetRepository yetRepository;
    @Autowired
    private VacationRepository vacationRepository;
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private PriceOfMedicalServiceRepository priceOfMedicalServiceRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int MEDICAL_ORGANIZATION_VALUE = 1;
    private static final int DEPARTMENT_VALUE = intInRange(2, 5);
    private static final int DOCTOR_VALUE = intInRange(5, 10);
    private static final int DIPLOMA_VALUE = intInRange(1, 5);
    private static final int DISEASE_VALUE = intInRange(5, 20);
    private static final int MEDICAL_SERVICE_VALUE = intInRange(50, 100);
    private static final int ADMIN_VALUE = 1;
    private static final int HR_MANAGER_VALUE = 1;
    private static final int ECONOMIST_VALUE = 1;
    private static final int REGISTRAR_VALUE = intInRange(2, 5);
    private static final int PATIENT_VALUE = intInRange(20, 50);
    private static final int TALON_VALUE = intInRange(25, 50);
    private static final int PRICE_OF_MEDICAL_SERVICE_VALUE = intInRange(10, 20);
    private static final int APPEAL_VALUE = intInRange(5, 10);
    private static final int VISIT_VALUE = intInRange(10, 15);

    private static final double BASE_PRICE_YET_VALUE = 50.0;
    private static final int DOCUMENT_NUMBER_COUNT_ = 107700;
    private static final int START_YET_MONTH = -6;
    private static final int END_YET_MONTH = 6;
    private static final int START_SERVICE_PRICE_MONTH = 2;

    private static List<MedicalOrganization> MEDICAL_ORGANIZATIONS_LIST = new ArrayList<>();
    private static List<Department> DEPARTMENT_LIST = new ArrayList<>();
    private static List<Disease> DISEASE_LIST = new ArrayList<>();
    private static List<Doctor> DOCTORS_LIST = new ArrayList<>();
    private static List<Diploma> DIPLOMA_LIST = new ArrayList<>();
    private static List<Talon> TALONS_LIST = new ArrayList<>();
    private static List<MedicalService> MEDICAL_SERVICE_LIST = new ArrayList<>();
    private static List<PriceOfMedicalService> PRICE_OF_MEDICAL_SERVICE_LIST = new ArrayList<>();
    private static List<Patient> PATIENT_LIST = new ArrayList<>();
    private static List<Appeal> APPEAL_LIST = new ArrayList<>();
    private static List<Visit> VISIT_LIST = new ArrayList<>();
    private static List<Administrator> ADMIN_LIST = new ArrayList<>();
    private static List<HrManager> HR_LIST = new ArrayList<>();
    private static List<Registrar> REGISTRAR_LIST = new ArrayList<>();
    private static List<Economist> ECONOMIST_LIST = new ArrayList<>();
    private static List<Doctor> MAIN_DOCTOR_LIST = new ArrayList<>();
    private static List<Vacation> VACATION_LIST = new ArrayList<>();
    public static List<Account> ACCOUNT_LIST = new ArrayList<>();
    public static List<Yet> YET_LIST = new ArrayList<>();

    private static HashMap<Integer, List<Talon>> TALONS_TO_ASSIGN_MAP = new HashMap<>();


    private static int intInRange(int startBorder, int endBorder) {
        return new Random().nextInt(++endBorder - startBorder) + startBorder;
    }

    private static double doubleInRange(double startBorder, double endBorder) {
        return new Random().nextDouble(++endBorder - startBorder) + startBorder;
    }

    private static BigDecimal bigDecimalInRange(double startBorder, double endBorder) {
        return BigDecimal.valueOf(doubleInRange(startBorder, endBorder));
    }

    private static LocalDate dateInRange(LocalDate startBorder, LocalDate endBorder) {
        return LocalDate.ofEpochDay(
                new Random()
                        .nextLong(endBorder.toEpochDay() - startBorder.toEpochDay()) + startBorder.toEpochDay());
    }

    private static LocalDateTime dateTimeInRange(LocalDateTime startBorder, LocalDateTime endBorder) {
        return LocalDateTime.ofEpochSecond(
                new Random()
                        .nextLong(endBorder.toEpochSecond(ZoneOffset.UTC) -
                                startBorder.toEpochSecond(ZoneOffset.UTC)) +
                        startBorder.toEpochSecond(ZoneOffset.UTC),
                0,
                ZoneOffset.UTC
        );
    }

    /*
        метод генерирует рандомную дату рождения в диапазоне лет
     */
    private static LocalDate getRandomBirthday() {
        return dateInRange(
                LocalDate.now().minusYears(75),
                LocalDate.now().minusYears(30));
    }

    /*
        метод возвращает рандомную дату трудоустройства
    */
    private LocalDate getRandomDateOfEmployment() {
        return dateInRange(
                LocalDate.now().minusYears(5),
                LocalDate.now().minusMonths(5));
    }

    /*
        метод сохраняет талоны сразу в HashMap и в List
     */
    private void saveTalonToHashMapAndList(Integer index, Talon talon) {
        TALONS_LIST.add(talon);
        if (intInRange(0, 2) == 1) {
            List<Talon> talons = TALONS_TO_ASSIGN_MAP.get(index);
            if (talons == null) {
                TALONS_TO_ASSIGN_MAP.put(index, new ArrayList<>(List.of(talon)));
            } else {
                talons.add(talon);
                TALONS_TO_ASSIGN_MAP.remove(index);
                TALONS_TO_ASSIGN_MAP.put(index, talons);
            }
        }
    }

    private String generateRandomIdentifier() {
        String availableSymbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random(System.nanoTime());
        for (int i = 0; i < 15; i++) {
            sb.append(availableSymbols.charAt(random.nextInt(availableSymbols.length())));
        }
        return sb.toString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void progressBar(long remain, long total) {
        char incomplete = '░';
        char complete = '█';

        int barLength = 25;
        int remainPercent = (int) ((100 * remain) / total);
        int step = 100 / barLength;

        String barStart = "[";
        String barEnd = "]";

        StringBuilder sb = new StringBuilder();
        Stream.generate(() -> incomplete).limit(barLength).forEach(sb::append);
        for (int i = 0; i < remainPercent / step; i++) {
            sb.replace(i, i + 1, String.valueOf(complete));
        }
        sb.append(barEnd).append(" - ").append(remainPercent).append("%");
        System.out.print("\r" + barStart + sb);
        if (total == remain) {
            System.out.println();
        }
    }

    @PostConstruct
    public void addTestData() {
        Role roleRegistrar = roleRepository.save(new Role(RolesEnum.REGISTRAR.name()));
        Role roleDoctor = roleRepository.save(new Role(RolesEnum.DOCTOR.name()));
        Role rolePatient = roleRepository.save(new Role(RolesEnum.PATIENT.name()));
        Role roleMainDoctor = roleRepository.save(new Role(RolesEnum.MAIN_DOCTOR.name()));
        Role roleEconomist = roleRepository.save(new Role(RolesEnum.ECONOMIST.name()));
        Role roleAdmin = roleRepository.save(new Role(RolesEnum.ADMIN.name()));
        Role roleHrManager = roleRepository.save(new Role(RolesEnum.HR_MANAGER.name()));
        Role roleChiefDoctor = roleRepository.save(new Role(RolesEnum.CHIEF_DOCTOR.name()));

        int total = (((DOCTOR_VALUE + 1) + (MEDICAL_SERVICE_VALUE + 1)) * (DEPARTMENT_VALUE + 1) * (MEDICAL_ORGANIZATION_VALUE + 1));
        long remain = 0;

        System.out.println("Running data initializer... (Medical organizations + departments + doctors + etc)");

        // YET
        for (int i = START_YET_MONTH; i < END_YET_MONTH; i++) {
            Yet yet;
            LocalDate todayDate = LocalDate.now();
            if (i <= 0) {
                yet = Yet.builder()
                        .price(doubleInRange(BASE_PRICE_YET_VALUE - BASE_PRICE_YET_VALUE / 2,
                                BASE_PRICE_YET_VALUE + BASE_PRICE_YET_VALUE / 2))
                        .dayFrom(LocalDate.now().minusMonths(Math.abs(i - 1))
                                .minusDays(todayDate.getDayOfMonth()))
                        .dayTo(LocalDate.now().minusMonths(Math.abs(i))
                                .plusDays(todayDate.getDayOfMonth() +
                                        (todayDate.lengthOfMonth() - todayDate.getDayOfMonth())))
                        .build();
            } else {
                yet = Yet.builder()
                        .price(doubleInRange(BASE_PRICE_YET_VALUE - BASE_PRICE_YET_VALUE / 2,
                                BASE_PRICE_YET_VALUE + BASE_PRICE_YET_VALUE / 2))
                        .dayFrom(LocalDate.now().plusMonths(i - 1)
                                .minusDays(todayDate.getDayOfMonth()))
                        .dayTo(LocalDate.now().plusMonths(i)
                                .plusDays(todayDate.getDayOfMonth() +
                                        (todayDate.lengthOfMonth() - todayDate.getDayOfMonth())))
                        .build();
            }
            YET_LIST.add(yetRepository.save(yet));

        }

        //создаю организацию
        for (int org = MEDICAL_ORGANIZATIONS_LIST.size(); org <= MEDICAL_ORGANIZATION_VALUE; ++org) {

            MedicalOrganization organization = MedicalOrganization.builder()
                    .address("address" + org)
                    .name("medicalOrganization" + org)
                    .build();
            organization = medicalOrganizationRepository.save(organization);
            MEDICAL_ORGANIZATIONS_LIST.add(organization);

            //Создаю отделения
            int dep_count = DEPARTMENT_LIST.size();
            for (int dep = dep_count; dep <= dep_count + DEPARTMENT_VALUE; ++dep) {
                Department department = Department.builder()
                        .name("Department_name" + dep)
                        .medicalOrganization(organization)
                        .build();
                department = departmentRepository.save(department);
                DEPARTMENT_LIST.add(department);

                // Создаю медицинские услуги
                int med_serv_count = MEDICAL_SERVICE_LIST.size();
                for (int medServ = med_serv_count; medServ <= med_serv_count + MEDICAL_SERVICE_VALUE; ++medServ) {
                    progressBar(++remain, total);

                    MedicalService medicalService = MedicalService.builder()
                            .identifier(generateRandomIdentifier())
                            .name("Medical_service_name" + medServ)
                            .department(department)
                            .build();
                    medicalService = medicalServiceRepository.save(medicalService);
                    MEDICAL_SERVICE_LIST.add(medicalService);

                    // Создаю цены для медицинской услуги
                    int price_for_med_serv_count = PRICE_OF_MEDICAL_SERVICE_LIST.size();
                    int startDay = 1;
                    int endDay = intInRange(startDay + 1,
                            startDay + 30 / (PRICE_OF_MEDICAL_SERVICE_VALUE /
                                    (Math.abs(START_SERVICE_PRICE_MONTH) + START_SERVICE_PRICE_MONTH))
                    );
                    for (int price = price_for_med_serv_count;
                         price <= price_for_med_serv_count + PRICE_OF_MEDICAL_SERVICE_VALUE; ++price) {

                        LocalDate dayFrom = LocalDate.now()
                                .minusMonths(START_SERVICE_PRICE_MONTH)
                                .plusDays(startDay);

                        LocalDate dayTo = LocalDate.now()
                                .minusMonths(START_SERVICE_PRICE_MONTH)
                                .plusDays(endDay);

                        PriceOfMedicalService priceOfMedicalService = PriceOfMedicalService.builder()
                                .dayFrom(dayFrom)
                                .dayTo(dayTo)
                                .medicalService(medicalService)
                                .yet(bigDecimalInRange(0.5, 2))
                                .build();

                        PRICE_OF_MEDICAL_SERVICE_LIST.add(priceOfMedicalServiceRepository.save(priceOfMedicalService));

                        startDay = endDay;
                        endDay = intInRange(startDay + 1,
                                startDay + 30 / (PRICE_OF_MEDICAL_SERVICE_VALUE /
                                        (Math.abs(START_SERVICE_PRICE_MONTH) + START_SERVICE_PRICE_MONTH))
                        );

                    }

                }

                // подвязываю цены услуг к услугам
                MEDICAL_SERVICE_LIST.forEach(medicalService -> {
                    List<PriceOfMedicalService> prices = PRICE_OF_MEDICAL_SERVICE_LIST.stream()
                            .filter(priceOfMedicalService -> priceOfMedicalService
                                    .getMedicalService().getId().equals(medicalService.getId()))
                            .toList();
                    medicalService.setPrices(prices);
                    medicalServiceRepository.save(medicalService);
                });

                //Создаю заболевания
                int dis_count = DISEASE_LIST.size();
                for (int dis = dis_count; dis <= dis_count + DISEASE_VALUE; ++dis) {
                    Disease disease = Disease.builder()
                            .identifier(generateRandomIdentifier())
                            .name("Disease_name" + dis)
                            .department(department)
                            .build();
                    DISEASE_LIST.add(diseaseRepository.save(disease));

                }

                //Создаю врачей c персональной историей
                int doc_count = DOCTORS_LIST.size();
                int dip_count = DIPLOMA_LIST.size();
                for (int doc = doc_count; doc <= doc_count + DOCTOR_VALUE; ++doc) {
                    progressBar(++remain, total);

                    //персональную историю каждому врачу
                    PersonalHistory personalHistory = PersonalHistory.builder()
                            .dateOfEmployment(getRandomDateOfEmployment())
                            .dateOfDismissal(null)
                            .build();
                    personalHistoryRepository.save(personalHistory);


                    //врача
                    Doctor doctor = Doctor.builder()
                            .email("doctor" + doc + "@email.com")
                            .password(encodePassword("doctorPwd" + doc))
                            .firstName("doctorFirstName" + doc)
                            .lastName("doctorLastName" + doc)
                            .surname("doctorSurname" + doc)
                            .birthday(getRandomBirthday())
                            .department(department)
                            .personalHistory(personalHistory)
                            .role(intInRange(0, 1) == 1 ? roleDoctor : roleChiefDoctor)
                            .build();
                    DOCTORS_LIST.add(doctorRepository.save(doctor));


                    //дипломы
                    for (int dip = dip_count; dip <= dip_count + DIPLOMA_VALUE; ++dip) {
                        Diploma diploma = Diploma.builder()
                                .serialNumber(1000L + dip)
                                .universityName("University_name" + dip)
                                .dateFrom(dateInRange(doctor.getBirthday().plusYears(25),
                                        personalHistory.getDateOfEmployment()))
                                .personalHistory(personalHistory)
                                .build();
                        DIPLOMA_LIST.add(diplomaRepository.save(diploma));

                    }

                    //аттестации
                    LocalDate attestationEnd = dateInRange(LocalDate.now(), LocalDate.now().plusMonths(1));
                    int year = 1;
                    while (personalHistory.getDateOfEmployment().plusYears(year - 1).getYear() <= LocalDate.now().getYear()) {
                        LocalDate attestationStart = attestationEnd.minusYears(year);
                        Attestation attestation = new Attestation(
                                attestationStart,
                                attestationStart.plusYears(1),
                                (DOCUMENT_NUMBER_COUNT_ + new Random().nextInt(10000)) + "",
                                personalHistory
                        );
                        attestationRepository.save(attestation);
                        year++;

                    }

                    //отпуска
                    long vacationsCount = (long) Math.ceil((double) (LocalDate.now().toEpochDay() - personalHistory.getDateOfEmployment().toEpochDay()) / 365);
                    for (long i = 0; i < vacationsCount; ++i) {
                        LocalDate vacationStarts = dateInRange(
                                personalHistory.getDateOfEmployment().plusYears(1 + i),
                                personalHistory.getDateOfEmployment().plusYears(2 + i)
                        );
                        LocalDate vacationEnds = vacationStarts.plusWeeks(4);
                        Vacation vacation = new Vacation(
                                vacationStarts,
                                vacationEnds,
                                personalHistory
                        );
                        VACATION_LIST.add(vacationRepository.save(vacation));

                    }

                    //талоны
                    int talonCount = TALONS_LIST.size();
                    for (int talon = talonCount; talon <= talonCount + TALON_VALUE; ++talon) {
                        Talon talonObject = Talon.builder()
                                .time(dateTimeInRange(LocalDateTime.now(), LocalDateTime.now().plusWeeks(4)))
                                .doctor(doctor)
                                .build();
                        saveTalonToHashMapAndList(intInRange(0, PATIENT_VALUE), talonRepository.save(talonObject));
                    }
                }

                // главного доктора
                int mainDoctorCount = MAIN_DOCTOR_LIST.size();
                for (int mainDoctor = mainDoctorCount; mainDoctor <= mainDoctorCount + HR_MANAGER_VALUE; ++mainDoctor) {

                    //персональную историю каждому врачу
                    PersonalHistory personalHistory = PersonalHistory.builder()
                            .dateOfEmployment(getRandomDateOfEmployment())
                            .dateOfDismissal(null)
                            .build();
                    personalHistoryRepository.save(personalHistory);

                    Doctor mainDoctorObject = Doctor.builder()
                            .email("mainDoctor" + mainDoctor + "@email.com")
                            .password(encodePassword("mainDoctorPwd" + mainDoctor))
                            .firstName("mainDoctorFirstName" + mainDoctor)
                            .lastName("mainDoctorLastName" + mainDoctor)
                            .surname("mainDoctorSurname" + mainDoctor)
                            .birthday(getRandomBirthday())
                            .department(department)
                            .personalHistory(personalHistory)
                            .role(roleMainDoctor)
                            .build();
                    MAIN_DOCTOR_LIST.add(doctorRepository.save(mainDoctorObject));

                    //дипломы Для глав врача
                    for (int dip = dip_count; dip <= dip_count + DIPLOMA_VALUE; ++dip) {
                        Diploma diploma = Diploma.builder()
                                .serialNumber(1000L + dip)
                                .universityName("University_name" + dip)
                                .dateFrom(dateInRange(mainDoctorObject.getBirthday().plusYears(25),
                                        personalHistory.getDateOfEmployment()))
                                .personalHistory(personalHistory)
                                .build();
                        DIPLOMA_LIST.add(diplomaRepository.save(diploma));
                    }

                    //аттестации
                    LocalDate attestationEnd = dateInRange(LocalDate.now(), LocalDate.now().plusMonths(1));
                    int year = 1;
                    while (personalHistory.getDateOfEmployment().plusYears(year - 1).getYear() <=
                            LocalDate.now().getYear()) {
                        LocalDate attestationStart = attestationEnd.minusYears(year);
                        Attestation attestation = new Attestation(
                                attestationStart,
                                attestationStart.plusYears(1),
                                (DOCUMENT_NUMBER_COUNT_ + new Random().nextInt(10000)) + "",
                                personalHistory
                        );
                        attestationRepository.save(attestation);
                        year++;
                    }

                    //отпуска
                    long vacationsCount = (long) Math.ceil((double) (LocalDate.now().toEpochDay() - personalHistory.getDateOfEmployment().toEpochDay()) / 365);
                    for (long i = 0; i < vacationsCount; ++i) {
                        LocalDate vacationStarts = dateInRange(
                                personalHistory.getDateOfEmployment().plusYears(1 + i),
                                personalHistory.getDateOfEmployment().plusYears(2 + i)
                        );
                        LocalDate vacationEnds = vacationStarts.plusWeeks(4);
                        Vacation vacation = new Vacation(
                                vacationStarts,
                                vacationEnds,
                                personalHistory
                        );

                        VACATION_LIST.add(vacationRepository.save(vacation));
                    }

                }
            }
        }

        total = ((VISIT_VALUE + 1) * (APPEAL_VALUE + 1) * (PATIENT_VALUE + 1));
        remain = 0;

        System.out.println("Running second step... (Patient, appeals, visits)");

        // создаю пациента
        int patientCount = PATIENT_LIST.size();
        for (int patient = patientCount; patient <= patientCount + PATIENT_VALUE; ++patient) {

            Patient patientObject = Patient.builder()
                    .email("patient" + patient + "@mail.com")
                    .password(encodePassword("patient" + patient))
                    .firstName("patentFirstName" + patient)
                    .lastName("patientLastName" + patient)
                    .surname("patientSurname" + patient)
                    .address("patientAddress" + patient)
                    .polis("polis" + patient)
                    .snils("snils" + patient)
                    .passport("passport" + patient)
                    .talons(TALONS_TO_ASSIGN_MAP.get(patient))
                    .build();
            patientObject = patientRepository.save(patientObject);

            // ассигнимся на талоны
            for (Talon talon : TALONS_TO_ASSIGN_MAP.get(patient)) {
                talon.setPatient(patientObject);
                talonRepository.save(talon);
            }

            // создаю обращения
            int appealCount = APPEAL_LIST.size();
            List<Visit> visitsTempList = new ArrayList<>();
            for (int appeal = appealCount; appeal <= appealCount + APPEAL_VALUE; ++appeal) {
                Appeal appealObject = Appeal.builder()
                        .disease(DISEASE_LIST.get(intInRange(0, DISEASE_LIST.size() - 1)))
                        .patient(patientObject)
                        .build();

                // создаю посещения
                int visitCount = VISIT_LIST.size();
                Set<Visit> tempVisitList = new HashSet<>();
                for (int visit = visitCount; visit <= visitCount + VISIT_VALUE; ++visit) {
                    progressBar(++remain, total);

                    Visit visitObject = Visit.builder()
                            .doctor(DOCTORS_LIST.get(intInRange(0, DOCTORS_LIST.size() - 1)))
                            .medicalServices(
                                    MEDICAL_SERVICE_LIST.stream()
                                            .filter(x -> x.getId() == intInRange(0, MEDICAL_SERVICE_LIST.size() - 1))
                                            .limit(intInRange(3, 6))
                                            .collect(Collectors.toSet())
                            )
                            .dayOfVisit(dateInRange(
                                    LocalDate.now().minusWeeks(18),
                                    LocalDate.now().plusWeeks(2)))
                            .appeal(appealObject)
                            .build();
                    VISIT_LIST.add(visitObject);
                    tempVisitList.add(visitObject);
                }
                appealObject.setVisits(tempVisitList);
                appealObject.setLocalDate(tempVisitList.stream()
                        .sorted(Comparator.comparing(Visit::getDayOfVisit).reversed())
                        .map(Visit::getDayOfVisit)
                        .findFirst()
                        .orElse(null));
                appealObject.setClosed((intInRange(0, 1) == 1 && appealObject.getLocalDate().isBefore(LocalDate.now()))
                        || (appealObject.getLocalDate().isBefore(LocalDate.now().minusMonths(2))));
                appealObject = appealRepository.save(appealObject);
                visitRepository.saveAll(tempVisitList);
                APPEAL_LIST.add(appealObject);
            }
        }

        // инитим админа
        int adminCount = ADMIN_LIST.size();
        for (int admin = adminCount; admin <= adminCount + ADMIN_VALUE; ++admin) {
            Administrator administrator = Administrator.builder()
                    .email("admin" + admin + "@mail.com")
                    .password(encodePassword("adminPwd" + admin))
                    .firstName("adminFirstName" + admin)
                    .lastName("adminLastName" + admin)
                    .surname("adminSurname" + admin)
                    .birthday(dateInRange(LocalDate.now().minusYears(50), LocalDate.now().minusYears(30)))
                    .role(roleAdmin)
                    .build();
            ADMIN_LIST.add(administratorRepository.save(administrator));
        }

        // иниитим hr'а
        int hrCount = HR_LIST.size();
        for (int hr = hrCount; hr <= hrCount + HR_MANAGER_VALUE; ++hr) {
            HrManager hrManager = HrManager.builder()
                    .email("hr" + hr + "@mail.com")
                    .password(encodePassword("hrPwd" + hr))
                    .firstName("hrFirstName" + hr)
                    .lastName("hrLastName" + hr)
                    .surname("hrSurname" + hr)
                    .birthday(dateInRange(LocalDate.now().minusYears(50), LocalDate.now().minusYears(30)))
                    .role(roleHrManager)
                    .build();
            HR_LIST.add(hrManagerRepository.save(hrManager));
        }

        // инитим регистратора
        int registrarCount = REGISTRAR_LIST.size();
        for (int registrar = registrarCount; registrar <= registrarCount + REGISTRAR_VALUE; ++registrar) {
            Registrar registrarObject = Registrar.builder()
                    .email("registrar" + registrar + "@mail.com")
                    .password(encodePassword("registrarPwd" + registrar))
                    .firstName("registrarFirstName" + registrar)
                    .lastName("registrarLastName" + registrar)
                    .surname("registrarSurname" + registrar)
                    .birthday(dateInRange(LocalDate.now().minusYears(50), LocalDate.now().minusYears(30)))
                    .role(roleRegistrar)
                    .build();
            REGISTRAR_LIST.add(registrarRepository.save(registrarObject));
        }

        // инитим экономиста
        int economistCount = ECONOMIST_LIST.size();
        for (int economist = economistCount; economist <= economistCount + ECONOMIST_VALUE; ++economist) {
            Economist economistObject = Economist.builder()
                    .email("economist" + economist + "@mail.com")
                    .password(encodePassword("economistPwd" + economist))
                    .firstName("economistFirstName" + economist)
                    .lastName("economistLastName" + economist)
                    .surname("economistSurname" + economist)
                    .birthday(dateInRange(LocalDate.now().minusYears(50), LocalDate.now().minusYears(30)))
                    .role(roleEconomist)
                    .build();
            ECONOMIST_LIST.add(economistRepository.save(economistObject));

        }

        System.out.println("Done with main entities... Creating accounts");

        // создаю аккаунт
        MEDICAL_ORGANIZATIONS_LIST
                .forEach(medicalOrganization -> {

                    Set<Appeal> medicalOrganizationAppeals = APPEAL_LIST.stream()
                            .filter(appeal -> appeal.getDisease()
                                    .getDepartment()
                                    .getMedicalOrganization()
                                    .getId().equals(medicalOrganization.getId()))
                            .collect(Collectors.toSet());

                    List<Visit> medicalOrganizationVisits = VISIT_LIST.stream()
                            .filter(visit -> visit.getAppeal()
                                    .getDisease()
                                    .getDepartment()
                                    .getMedicalOrganization()
                                    .getId().equals(medicalOrganization.getId()))
                            .toList();

                    LocalDate date = medicalOrganizationAppeals.stream()
                            .filter(appeal -> {
                                LocalDate todayDate = LocalDate.now();
                                return appeal.getLocalDate().isBefore(todayDate.minusDays(todayDate.getDayOfMonth()));
                            })
                            .filter(Appeal::isClosed)
                            .map(appeal -> {
                                LocalDate localDate = appeal.getLocalDate();
                                return localDate.plusDays(localDate.lengthOfMonth() - localDate.getDayOfMonth());
                            })
                            .findFirst()
                            .orElse(null);

                    final LocalDate[] dateToWrite = {LocalDate.now()
                            .plusDays(LocalDate.now().lengthOfMonth() - LocalDate.now().getDayOfMonth())};

                    final LocalDate[] startDate = {dateToWrite[0]};
                    final LocalDate[] endDate = {startDate[0].minusMonths(1)};
                    final Map<LocalDate, List<Appeal>> localDateListHashMap = new HashMap<>();

                    medicalOrganizationAppeals.stream()
                            .sorted(Comparator.comparing(Appeal::getLocalDate))
                            .forEach(appeal -> {
                                if (appeal.getLocalDate().isBefore(startDate[0]) && appeal.getLocalDate().isAfter(endDate[0])) {
                                    if (localDateListHashMap.get(dateToWrite[0]) == null) {
                                        localDateListHashMap.put(dateToWrite[0], new ArrayList<>(List.of(appeal)));
                                    } else {
                                        List<Appeal> appeals = localDateListHashMap.get(dateToWrite[0]);
                                        appeals.add(appeal);
                                        localDateListHashMap.remove(dateToWrite[0]);
                                        localDateListHashMap.put(dateToWrite[0], appeals);
                                    }
                                } else {
                                    startDate[0] = endDate[0];
                                    endDate[0] = endDate[0].minusMonths(1);

                                    dateToWrite[0] = dateToWrite[0].minusMonths(1);

                                    if (localDateListHashMap.get(dateToWrite[0]) == null) {
                                        localDateListHashMap.put(dateToWrite[0], new ArrayList<>(List.of(appeal)));
                                    } else {
                                        List<Appeal> appeals = localDateListHashMap.get(dateToWrite[0]);
                                        appeals.add(appeal);
                                        localDateListHashMap.remove(dateToWrite[0]);
                                        localDateListHashMap.put(dateToWrite[0], appeals);
                                    }
                                }
                            });

                    localDateListHashMap.forEach((key, value) -> {
                        Account account = Account.builder()
                                .name("medicalOrganizationAccount" + medicalOrganization.getId())
                                .appeals(medicalOrganizationAppeals)
                                .date(key)
                                .build();

                        final Long[] money = {0L};

                        value.stream()
                                .map(Appeal::getVisits)
                                .flatMap(Collection::stream)
                                .map(visit -> {
                                    Set<MedicalService> medicalServices = visit.getMedicalServices();
                                    List<PriceOfMedicalService> priceOfMedicalServices = new ArrayList<>();
                                    LocalDate todayDate = LocalDate.now();
                                    Double yetValue = YET_LIST.stream()
                                            .filter(yet -> todayDate.isAfter(yet.getDayFrom()) && todayDate.isBefore(yet.getDayTo()))
                                            .map(Yet::getPrice)
                                            .findFirst()
                                            .orElse(0D);

                                    final Double[] sum = {0D};

                                    medicalServices.stream()
                                            .map(MedicalService::getPrices)
                                            .forEach(priceOfMedicalServices::addAll);

                                    priceOfMedicalServices.stream()
                                            .map(PriceOfMedicalService::getYet)
                                            .forEach(x -> sum[0] += x.doubleValue() * yetValue);
                                    return sum[0].longValue();
                                })
                                .forEach(x -> money[0] += x);
                        account.setMoney(money[0]);
                        account = accountRepository.save(account);
                        ACCOUNT_LIST.add(account);
                    });
                });
    }
}





