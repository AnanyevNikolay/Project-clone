package ru.mis2022.service.entity;

import ru.mis2022.models.dto.talon.DoctorTalonsDto;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.Talon;
import java.time.LocalDateTime;
import java.util.List;


public interface TalonService {

    Talon save(Talon talon);
    List<Talon> findAllByDoctorId(Long id);

    long findTalonsCountByIdAndDoctor(int countDays, Doctor doctor);

    List<Talon> persistTalonsForDoctor(Doctor doctor, int numberOfDays, int numbersOfTalons);

    List<Talon> findAllByPatientId(Long id);

    Talon findTalonById(Long id);

    List<Doctor> findDoctorsWithTalonsSpecificTimeRange(int countDays, Long departmentId);

    List<DoctorTalonsDto> getTalonsByDoctorIdAndDay(long doctorId, LocalDateTime startDayTime, LocalDateTime endDayTime);

}
