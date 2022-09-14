package ru.mis2022.service.entity;

import ru.mis2022.models.dto.talon.TalonDto;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.Talon;
import java.util.List;


public interface TalonService {

    Talon save(Talon talon);

    List<Talon> findAllByDoctorId(Long id);

    long findTalonsCountByIdAndDoctor(int countDays, Doctor doctor);

    List<Talon> persistTalonsForDoctor(Doctor doctor, int numberOfDays, int numbersOfTalons);

    Talon findTalonById(Long id);

    Talon getTalonByIdWithDoctor(Long id);

    List<Doctor> findDoctorsWithTalonsSpecificTimeRange(int countDays, Long departmentId);

    Long findPatientIdByTalonId(Long talonId);

    TalonDto registerPatientInTalon(Talon talon, Patient patient);

    void deleteAll();
}
