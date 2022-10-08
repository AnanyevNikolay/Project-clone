package ru.mis2022.service.entity;

import org.springframework.lang.Nullable;
import ru.mis2022.models.dto.talon.TalonDto;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.Talon;

import java.util.List;


public interface TalonService {

    List<Talon> persistTalonsForDoctor(Doctor doctor,
                                       int numberOfDays,
                                       int numbersOfTalons,
                                       @Nullable String startDate,
                                       @Nullable String endDate);

    long findTalonsCountByIdAndDoctor(int countDays,
                                      Long doctorId,
                                      @Nullable String startDate,
                                      @Nullable String endDate);

    Talon save(Talon talon);

    List<Talon> findAllByDoctorId(Long id);

    Talon findTalonById(Long id);

    Talon getTalonByIdAndDoctorId(Long talonId, Long doctorId);

    List<Doctor> findDoctorsWithTalonsSpecificTimeRange(int countDays, Long departmentId);

    Long findPatientIdByTalonId(Long talonId);

    TalonDto registerPatientInTalon(Talon talon, Patient patient);

    Boolean patientHaveTalonsFromDep(Long patientId, Long departmentId);

    Talon findTalonByIdWithDoctorAndPatient(Long id);

    void deleteAll();

}
