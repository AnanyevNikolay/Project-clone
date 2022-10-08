package ru.mis2022.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
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
import ru.mis2022.repositories.AdministratorRepository;
import ru.mis2022.service.entity.AdministratorService;
import ru.mis2022.service.entity.AppealService;
import ru.mis2022.service.entity.AttestationService;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.DiplomaService;
import ru.mis2022.service.entity.DiseaseService;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.EconomistService;
import ru.mis2022.service.entity.HrManagerService;
import ru.mis2022.service.entity.MedicalOrganizationService;
import ru.mis2022.service.entity.MedicalServiceService;
import ru.mis2022.service.entity.PatientService;
import ru.mis2022.service.entity.PersonalHistoryService;
import ru.mis2022.service.entity.PriceOfMedicalServiceService;
import ru.mis2022.service.entity.RegistrarService;
import ru.mis2022.service.entity.RoleService;
import ru.mis2022.service.entity.TalonService;
import ru.mis2022.service.entity.VacationService;
import ru.mis2022.service.entity.VisitService;
import ru.mis2022.service.entity.YetService;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

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
    @Value("${mis.property.Invite.expirationPeriod}")
    private String expirationPeriod;

    private final AppealService appealService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final EconomistService economistService;
    private final RoleService roleService;
    private final MedicalOrganizationService medicalOrganizationService;
    private final DepartmentService departmentService;

    private final DiplomaService diplomaService;
    private final RegistrarService registrarService;
    private final AdministratorService administratorService;
    private final HrManagerService hrManagerService;
    private final AttestationService attestationService;
    private final PersonalHistoryService personalHistoryService;
    private final TalonService talonService;
    private final MedicalServiceService medicalServiceService;
    private final DiseaseService diseaseService;

    private final YetService yetService;

    private final VacationService vacationService;

    private final VisitService visitService;
    private final PriceOfMedicalServiceService priceOfMedicalServiceService;
    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;


    public DataInitializer(AppealService appealService,
                           PatientService patientService,
                           DoctorService doctorService,
                           EconomistService economistService,
                           RoleService roleService,
                           MedicalOrganizationService medicalOrganizationService,
                           DepartmentService departmentService,
                           DiplomaService diplomaService,
                           RegistrarService registrarService,
                           AdministratorService administratorService,
                           TalonService talonService,
                           HrManagerService hrManagerService,
                           AttestationService attestationService,
                           PersonalHistoryService personalHistoryService,
                           MedicalServiceService medicalServiceService,
                           DiseaseService diseaseService,
                           YetService yetService,
                           VacationService vacationService,
                           VisitService visitService,
                           PriceOfMedicalServiceService priceOfMedicalServiceService,
                           AdministratorRepository administratorRepository,
                           PasswordEncoder passwordEncoder) {
        this.appealService = appealService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.economistService = economistService;
        this.roleService = roleService;
        this.medicalOrganizationService = medicalOrganizationService;
        this.departmentService = departmentService;
        this.diplomaService = diplomaService;
        this.registrarService = registrarService;
        this.administratorService = administratorService;
        this.hrManagerService = hrManagerService;
        this.talonService = talonService;
        this.attestationService = attestationService;
        this.personalHistoryService = personalHistoryService;
        this.medicalServiceService = medicalServiceService;
        this.diseaseService = diseaseService;
        this.yetService = yetService;
        this.vacationService = vacationService;
        this.visitService = visitService;
        this.priceOfMedicalServiceService = priceOfMedicalServiceService;
        this.administratorRepository = administratorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int FIVE = 5;
    private static final int SOME = 10;
    private static final int ANY = 20;
    private static final int MANY = 50;
    private static final int VERY_MANY = 100;

    private static final int MEDICAL_ORGANIZATION = ONE;
    private static final int DEPARTMENT_COUNT = intInRange(TWO, FIVE);
    private static final int DOCTOR_COUNT = intInRange(FIVE, SOME);
    private static final int DIPLOMA_COUNT = intInRange(ONE, FIVE);
    private static final int MAIN_DOCTORS_COUNT = intInRange(ONE, TWO);
    private static final int DISEASE_COUNT = intInRange(FIVE, ANY);
    private static final int MEDICAL_SERVICE_COUNT = intInRange(MANY, VERY_MANY);
    private static final int ADMIN = ONE;
    private static final int HR_MANAGER = ONE;
    private static final int ECONOMIST = ONE;
    private static final int REGISTRAR_COUNT = intInRange(TWO, FIVE);
    private static final int PATIENT_COUNT = intInRange(ANY, MANY);

    private static final double BASE_PRICE_YET = 100.0;

    private static final int DOCUMENT_NUMBER_COUNT = 107700;

    private static final long SERIAL_NUMBER_COUNT = 77780000;


    private static int intInRange(int startBorder, int endBorder) {
        return new Random().nextInt(++endBorder - startBorder) + startBorder;
    }

    private static LocalDate randomBirthday() {
        LocalDate beginDate = LocalDate.now().minusYears(65);
        LocalDate endDate = LocalDate.now().minusYears(25);
        return dateInRange(beginDate, endDate);
    }

    private static LocalDate dateInRange(LocalDate startBorder, LocalDate endBorder) {
        long randomDay = new Random().nextLong(endBorder.toEpochDay() - startBorder.toEpochDay()) +
                startBorder.toEpochDay();
        return LocalDate.ofEpochDay(randomDay);
    }

    @PostConstruct
    public void addTestData() {
        Role roleRegistrar = roleService.save(new Role(RolesEnum.REGISTRAR.name()));
        Role roleDoctor = roleService.save(new Role(RolesEnum.DOCTOR.name()));
        Role rolePatient = roleService.save(new Role(RolesEnum.PATIENT.name()));
        Role roleMainDoctor = roleService.save(new Role(RolesEnum.MAIN_DOCTOR.name()));
        Role roleEconomist = roleService.save(new Role(RolesEnum.ECONOMIST.name()));
        Role roleAdmin = roleService.save((new Role(RolesEnum.ADMIN.name())));
        Role roleHrManager = roleService.save(new Role(RolesEnum.HR_MANAGER.name()));
        Role roleChiefDoctor = roleService.save(new Role(RolesEnum.CHIEF_DOCTOR.name()));


        //Создать одну медицинскую организацию
        MedicalOrganization medicalOrganization = new MedicalOrganization(
                "medicalOrganization" + ONE,
                "address" + ONE
        );
        medicalOrganizationService.save(medicalOrganization);

        //Создать отделения и докторов для медицинской организации
        List<Department> departmentsList = new ArrayList<>();
        List<Doctor> doctorsList = new ArrayList<>();
        List<Disease> diseaseList = new ArrayList<>();
        for (int dep = 1; dep <= DEPARTMENT_COUNT; dep++) {
            Department department = new Department("DepartmentName" + dep);
            department.setMedicalOrganization(medicalOrganization);
            departmentService.save(department);
            departmentsList.add(department);

            //Создать заболевания для отделений
            for (int dis = 1; dis <= DISEASE_COUNT; dis++) {
                Disease disease = new Disease(
                        "AO" + dep + dis,
                        "diseaseName" + dep + dis
                );
                disease.setDepartment(department);
                diseaseService.save(disease);
                diseaseList.add(disease);
            }

            //Создать врачей в отделениях
            for (int doc = 1; doc <= DOCTOR_COUNT; doc++) {
                Doctor doctor = new Doctor(
                        "doctor" + dep + doc + "@email.com",
                        "doctor" + dep + doc,
                        "doctorFirstName" + dep + doc,
                        "doctorLastName" + dep + doc,
                        "doctorSurname" + dep + doc,
                        randomBirthday(),
                        roleDoctor,
                        department
                );
                doctorService.persist(doctor);
                doctorsList.add(doctor);

                if (doc <= MAIN_DOCTORS_COUNT) {
                    Doctor chiefDoctor = new Doctor(
                            "chiefDoctor" + dep + doc + "@email.com",
                            "chiefDoctor" + dep + doc,
                            "chiefDoctorFirstName" + dep + doc,
                            "chiefDoctorLastName" + dep + doc,
                            "chiefDoctorSurname" + dep + doc,
                            randomBirthday(),
                            roleChiefDoctor,
                            department
                    );
                    doctorService.persist(chiefDoctor);
                    doctorsList.add(chiefDoctor);

                    Doctor mainDoctor = new Doctor(
                            "mainDoctor" + dep + doc + "@email.com",
                            "mainDoctor" + dep + doc,
                            "mainDoctorFirstName" + dep + doc,
                            "mainDoctorLastName" + dep + doc,
                            "mainDoctorSurname" + dep + doc,
                            randomBirthday(),
                            roleMainDoctor,
                            department
                    );
                    doctorService.persist(mainDoctor);
                    doctorsList.add(mainDoctor);
                }
            }
        }

        //Создаем расписание докторам (присваиваем доктору
        // numberOfDaysDoctor * numbersOfTalons талонов)
        for (Doctor doctor : doctorsList) {
            talonService.persistTalonsForDoctor(doctor, numberOfDaysDoctor, numbersOfTalons, null, null);
        }

        //Создаем персональные истории, аттестации, отпуска докторам
        List<PersonalHistory> personalHistories = new ArrayList<>();
        for (Doctor doctor : doctorsList) {

            PersonalHistory personalHistory = new PersonalHistory();

            //Выбираем дату трудоустройства
            LocalDate dateOfEmployment = dateInRange(LocalDate.now()
                    .minusYears(FIVE), LocalDate.now().minusMonths(FIVE));
            personalHistory.setDateOfEmployment(dateOfEmployment);
            personalHistoryService.save(personalHistory);

            // Дипломы, от ONE до FIVE каждому врачу, от д.р. + 25 лет до наст.вр.
            for (int dip = 1; dip <= DIPLOMA_COUNT; dip++) {
                Diploma diploma = new Diploma(
                        "Медицинская академия №" + dip,
                        SERIAL_NUMBER_COUNT + new Random().nextLong(10000),
                        dateInRange(doctor.getBirthday().plusYears(25), LocalDate.now()),
                        personalHistory
                );
                diplomaService.save(diploma);
            }

            //Аттестации - на каждый год работы, с момента трудоустройства
            //Выбираем дату окончания аттестации - в течение месяца от н.вр.
            LocalDate attestationEnd = dateInRange(LocalDate.now(),
                    LocalDate.now().plusMonths(1));
            int year = 1;
            while (dateOfEmployment.plusYears(year - 1).getYear() <= LocalDate.now().getYear()) {
                LocalDate attestationStart = attestationEnd.minusYears(year);
                Attestation attestation = new Attestation(
                        attestationStart,
                        attestationStart.plusYears(1),
                        (DOCUMENT_NUMBER_COUNT + new Random().nextInt(10000)) + "",
                        personalHistory
                );
                attestationService.save(attestation);
                year++;
            }

            //Отпуска - месяц, за каждый год со следующего года после трудоустройства
            year = 1;
            while (dateOfEmployment.plusYears(year).getYear() < LocalDate.now().getYear()) {
                LocalDate vacationStart = dateInRange(
                        LocalDate.of(dateOfEmployment.plusYears(year).getYear(), 1, 1),
                        LocalDate.of(dateOfEmployment.plusYears(year).getYear(), 12, 1)
                );
                LocalDate vacationEnd = vacationStart.plusDays(30);
                Vacation vacation = new Vacation(
                        vacationStart,
                        vacationEnd,
                        personalHistory);
                vacationService.save(vacation);
                year++;
            }
            personalHistories.add(personalHistory);
            doctor.setPersonalHistory(personalHistory);
            doctorService.merge(doctor);
        }

        //Условная единица трудозатрат УЕТ
        double price = BASE_PRICE_YET;
        personalHistories.sort(
                Comparator.comparing(PersonalHistory::getDateOfEmployment));
        LocalDate BEGIN_DATE = personalHistories.get(0).getDateOfEmployment();
        LocalDate dayFrom = BEGIN_DATE.withDayOfMonth(1);
        LocalDate dayTo;
        while (dayFrom.isBefore(LocalDate.now())) {
            Yet yet = new Yet();
            yet.setDayFrom(dayFrom);
            dayTo = dayFrom.withDayOfMonth(dayFrom.lengthOfMonth());
            yet.setDayTo(dayTo);
            //УЕТ меняется с вероятностью 40% каждый месяц
            if (Math.random() < 0.4) {
                //С вероятностью 45% цена будет +5% или -8%
                price *= Math.random() < 0.45 ? 1.05 : 0.92;
            }
            yet.setPrice((double) Math.round(price));
            yetService.save(yet);
            //Шаг в месяц
            dayFrom = dayTo.plusDays(1);
        }

        //Медицинские услуги отделения
        List<MedicalService> medicalServiceList = new ArrayList<>();
        int medWithoutDep = intInRange(FIVE, SOME); //Количество услуг без отделения
        for (int j = 1; j < MEDICAL_SERVICE_COUNT + medWithoutDep; j++) {
            MedicalService medicalService = new MedicalService(
                    "K32110" + j,
                    "serviceName" + j
            );
            medicalServiceService.save(medicalService);
            //Цена на медицинские услуги
            //Базовая цена на услугу
            //todo
            // Округлить базовую цену
            BigDecimal basePrice = BigDecimal.valueOf(FIVE * intInRange(FIVE, VERY_MANY) / VERY_MANY);
            dayFrom = BEGIN_DATE.withDayOfMonth(1);
            while (dayFrom.isBefore(LocalDate.now())) {
                PriceOfMedicalService medicalServicePrice = new PriceOfMedicalService();
                dayTo = (dayFrom.getDayOfMonth() == 1) ? dayFrom.plusDays(14) :
                        dayFrom.withDayOfMonth(dayFrom.lengthOfMonth());
                //2 раза в месяц с вероятностью 30% услуга может изменить цену
                //С вероятностью 50% цена будет +0.05 или -0.1
                if (Math.random() < 0.3) {
                    basePrice = BigDecimal.valueOf(Math.random() < 0.5 ? 1.05 : 0.9 * basePrice.doubleValue());
                }
                medicalServicePrice.setDayFrom(dayFrom);
                medicalServicePrice.setDayTo(dayTo);
                medicalServicePrice.setYet(basePrice);
                medicalServicePrice.setMedicalService(medicalService);
                priceOfMedicalServiceService.save(medicalServicePrice);
                dayFrom = dayTo.plusDays(1);
            }
            medicalServiceService.save(medicalService);
            medicalServiceList.add(medicalService);
        }

        //Раскидываем рандомно медицинские услуги по отделениям
        for (Department department : departmentsList) {
            for (int j = 1; j < MEDICAL_SERVICE_COUNT / DEPARTMENT_COUNT; j++) {
                //В списке медицинских услуг находим рандомно услугу без отделения
                int medId = intInRange(0, medicalServiceList.size() - 1);
                MedicalService medicalService = medicalServiceList.get(medId);
                while (medicalService.getDepartment() != null) {
                    medId = intInRange(0, medicalServiceList.size() - 1);
                    medicalService = medicalServiceList.get(medId);
                }
                medicalService.setDepartment(department);
                medicalServiceService.save(medicalService);
            }
        }

        // Администратор
        for (int i = 1; i <= ADMIN; i++) {
            Administrator administrator = new Administrator(
                    "administrator" + i + "@email.com",
                    "administrator" + i,
                    "administratorFirstName" + i,
                    "administratorLastName" + i,
                    "administratorSurname" + i,
                    randomBirthday(),
                    roleRegistrar
            );
            administrator.setPassword(passwordEncoder.encode(administrator.getPassword()));
            administratorRepository.save(administrator);
        }

        //HR Manager
        for (int i = 1; i <= HR_MANAGER; i++) {
            HrManager hrManager = new HrManager(
                    "hrManager" + i + "@email.com",
                    "hrManager" + i,
                    "hrManagerFirstName" + i,
                    "hrManagerLastName" + i,
                    "hrManagerSurname" + i,
                    randomBirthday(),
                    roleHrManager
            );
            hrManagerService.persist(hrManager);
        }

        //Экономист
        for (int i = 1; i <= ECONOMIST; i++) {
            Economist economist = new Economist(
                    "economist" + i + "@email.com",
                    "economist" + i,
                    "economistFirstName" + i,
                    "economistLastName" + i,
                    "economistSurname" + i,
                    randomBirthday(),
                    roleEconomist
            );
            economistService.persist(economist);
        }

        //Регистратор
        for (int i = 1; i <= REGISTRAR_COUNT; i++) {
            Registrar registrar = new Registrar(
                    "registrar" + i + "@email.com",
                    "registrar" + i,
                    "registrarFirstName" + i,
                    "registrarLastName" + i,
                    "registrarSurname" + i,
                    randomBirthday(),
                    roleRegistrar
            );
            registrarService.persist(registrar);
        }

        //Пациенты
        List<Patient> patients = new ArrayList<>();
        for (int i = 1; i <= PATIENT_COUNT; i++) {
            Patient patient = new Patient(
                    "patient" + i + "@email.com",
                    "patient" + i,
                    "patientFirstName" + i,
                    "patientLastName" + i,
                    "patientSurname" + i,
                    randomBirthday(),
                    rolePatient,
                    "passport" + i,
                    "polis" + i,
                    "snils" + i,
                    "address" + i
            );
            patientService.persist(patient);
            patients.add(patient);
        }

        //Каждому пациенту выдаем от FIVE до SOME рандомных талонов рандомному доктору
        int n = intInRange(FIVE, SOME);
        for (int i = 1; i <= n; i++) {
            //Выбираем рандомного доктора
            int doctorId = intInRange(0, doctorsList.size() - 1);
            Doctor doctor = doctorsList.get(doctorId);
            //У доктора берем список доступных пациенту талонов(ограничение
            // по времени - от LocalDate.now() до numberOfDaysPatient)
            List<Talon> avaliableTalons = new ArrayList<>();
            for (Talon talon : talonService.findAllByDoctorId(doctor.getId())) {
                if (talon.getTime().isAfter(LocalDateTime.now()) &&
                        talon.getTime().toLocalDate().isBefore(LocalDate.now()
                                .plusDays(numberOfDaysPatient)) && talon.getPatient() == null) {
                    avaliableTalons.add(talon);
                }
            }
            //Проверяем наличие нужных талонов в нужном количестве у доктора
            if (avaliableTalons.size() >= PATIENT_COUNT) {
                for (Patient patient : patients) {
                    //В списке находим рандомный свободный талон, не перезатираем пациента
                    int talonId = intInRange(0, avaliableTalons.size() - 1);
                    Talon talon = avaliableTalons.get(talonId);
                    while (talon.getPatient() != null) {
                        talonId = intInRange(0, avaliableTalons.size() - 1);
                        talon = avaliableTalons.get(talonId);
                    }
                    talon.setPatient(patient); //Даем талоны пациентам
                    talonService.save(talon);
                }
            }
        }

        //Создаем рандомные закрытые обращения рандомному доктору с рандомным заболеванием
        //не раньше даты трудоутройства доктора
        int appeals = intInRange(FIVE, SOME);
        List<Appeal> appealList = new ArrayList<>();
        for (int app = 1; app <= appeals * PATIENT_COUNT; app++) {
            //Выбираем рандомного доктора
            int doctorId = intInRange(0, doctorsList.size() - 1);
            Doctor doctor = doctorsList.get(doctorId);
            //В отделении доктора выбираем рандомное заболевание
            List<Disease> diseasesDepartList = new ArrayList<>();
            for (Disease disease : diseaseList) {
                if (Objects.equals(disease.getDepartment(), doctor.getDepartment())) {
                    diseasesDepartList.add(disease);
                }
            }
            int diseaseId = intInRange(0, diseasesDepartList.size() - 1);
            Disease disease = diseasesDepartList.get(diseaseId);
            //Рандомная дата обращения в прошлом - от даты трудоустройства до текущей минус месяц
            LocalDate dateAppeal = dateInRange(doctor.getPersonalHistory().getDateOfEmployment(),
                    LocalDate.now().minusMonths(1));
            //Создаем посещения
            Set<Visit> visitSet = new HashSet<>();
            for (int vis = 1; vis <= intInRange(TWO, FIVE); vis++) {
                //Список медицинских услуг для посещения из этого департамента, от TWO до FIVE
                List<MedicalService> medicalServicesOfDepartment = new ArrayList<>();
                for (MedicalService medicalService : medicalServiceList) {
                    if (Objects.equals(medicalService.getDepartment(), doctor.getDepartment())) {
                        medicalServicesOfDepartment.add(medicalService);
                    }
                }
                Set<MedicalService> medicalServicesSet = new HashSet<>();
                for (int med = 1; med <= intInRange(TWO, FIVE); med++) {
                    int medicalServiceId = intInRange(0, medicalServicesOfDepartment.size() - 1);
                    medicalServicesSet.add(medicalServicesOfDepartment.get(medicalServiceId));
                }
                //Дата посещения - рандомная, с момента обращения, плюс месяц
                LocalDate dateVisit = dateInRange(dateAppeal, dateAppeal.plusMonths(1));
                //Создаем посещения
                Visit visit = new Visit(
                        dateVisit,
                        doctor,
                        null,
                        null
                );
                visitService.save(visit);
                visitSet.add(visit);
                //Сохраняем медицинские услуги для посещения
                for (MedicalService medicalService : medicalServicesSet) {
                    medicalService.setVisit(visit);
                    medicalServiceService.save(medicalService);
                }
            }
            //Создаем обращения
            Appeal appeal = new Appeal(
                    null,
                    disease,
                    null,
                    null,
                    true,
                    dateAppeal
            );
            appealService.save(appeal);
            //Сохраняем посещения для обращений
            for (Visit visit : visitSet) {
                visit.setAppeal(appeal);
                visitService.save(visit);
            }
            appealService.save(appeal);
            appealList.add(appeal);
        }

        //Добавляем пациентам от FIVE до SOME рандомных обращений
        for (Patient patient : patients) {
            for (int i = 1; i <= appeals; i++) {
                int appealId = intInRange(0, appealList.size() - 1);
                Appeal appeal = appealList.get(appealId);
                while (appeal.getPatient() != null) {
                    appealId = intInRange(0, appealList.size() - 1);
                    appeal = appealList.get(appealId);
                }
                appeal.setPatient(patient);
                appealService.save(appeal);
            }
        }

        //todo
        // - каждому доктору создать от TWO до FIVE открытых обращений не старше SOME дней
        // с рандомным заболеванием из перечня отделения,
        // - на каждое открытое обращение создать от ONE до TWO посещений

    }

}





