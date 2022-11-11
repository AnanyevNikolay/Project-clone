package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.dto.talon.TalonDto;
import ru.mis2022.models.dto.talon.converter.TalonDtoConverter;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Patient;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.repositories.DoctorRepository;
import ru.mis2022.repositories.PatientRepository;
import ru.mis2022.repositories.TalonRepository;
import ru.mis2022.service.entity.TalonService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;


@Service
@RequiredArgsConstructor
public class TalonServiceImpl implements TalonService {

    private final TalonRepository talonRepository;
    private final TalonDtoConverter talonDtoConverter;
    private final PatientRepository patientRepository;


    @Override
    public Talon save(Talon talon) {
        return talonRepository.save(talon);
    }

    @Override
    @Transactional
    public List<Talon> persistTalonsForDoctor(Doctor doctor,
                                              int numberOfDays,
                                              int numbersOfTalons,
                                              @Nullable String startDate,
                                              @Nullable String endDate) {

        List<Talon> talons = new ArrayList<>();
        LocalDateTime time;

        if (startDate == null && endDate == null) {
            time = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0));
        } else {
            LocalDate start = LocalDate.parse(Objects.requireNonNull(startDate), DATE_FORMATTER);
            LocalDate end = LocalDate.parse(Objects.requireNonNull(endDate), DATE_FORMATTER);
            time = LocalDateTime.of(start, LocalTime.of(8, 0));
            numberOfDays = end.getDayOfYear() - start.getDayOfYear();
        }

        for (int day = 0; day < numberOfDays; day++) {
            for (int hour = 0; hour < numbersOfTalons; hour++) {
                talons.add(talonRepository.save(new Talon(time.plusDays(day).plusHours(hour), doctor)));
            }
        }
        return talons;
    }

    @Override
    public long findTalonsCountByIdAndDoctor(int countDays,
                                             Long doctorId,
                                             @Nullable String startDate,
                                             @Nullable String endDate) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(countDays);

        if (startDate != null && endDate != null) {
            start = LocalDateTime.of(LocalDate.parse(startDate, DATE_FORMATTER), LocalTime.of(8, 0));
            end = LocalDateTime.of(LocalDate.parse(endDate, DATE_FORMATTER), LocalTime.MAX);
        }

        return talonRepository.findCountTalonsByParameters(doctorId, start, end);
    }

    @Override
    public List<Talon> findAllByDoctorId(Long id) {
        return talonRepository.findAllByDoctorId(id);
    }

    @Override
    public Talon findTalonById(Long id) {
        return talonRepository.findTalonById(id);
    }

    @Override
    public Talon getTalonByIdAndDoctorId(Long talonId, Long doctorId) {
        return talonRepository.getTalonByIdAndDoctorId(talonId, doctorId);
    }

    @Override
    public List<Doctor> findDoctorsWithTalonsSpecificTimeRange(int countDays, Long departmentId) {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusDays(countDays).with(LocalTime.MAX);
        return talonRepository.findDoctorsWithTalonsSpecificTimeRange(startTime, endTime, departmentId);
    }

    @Override
    public TalonDto registerPatientInTalon(Talon talon, Patient patient) {
        talon.setPatient(patient);
        talon = talonRepository.save(talon);
        return talonDtoConverter.talonToTalonDto(talon);
    }

    @Override
    public Talon findTalonByIdWithDoctorAndPatient(Long id) {
        return talonRepository.findTalonByWithDoctorAndPatient(id);
    }

    @Override
    public Boolean patientHaveTalonsFromDep(Long patientId, Long departmentId) {
        return talonRepository.patientHaveTalonsFromThisDep(patientId, departmentId);
    }

    @Override
    public void deleteAll() {
        talonRepository.deleteAll();
    }

    public Long findPatientIdByTalonId(Long talonId) {
        return patientRepository.findPatientIdByTalonId(talonId);
    }

    @Override
    public Talon findTalonWithDoctorAndPatientByTalonId(Long id) {
        return talonRepository.findTalonWithDoctorAndPatientByTalonId(id);
    }

    @Override
    public void deleteTalonById(Long id) {
        talonRepository.deleteById(id);
    }

    @Override
    public Talon findTalonWithPatientByTalonId(Long talonId) {
        return talonRepository.findTalonWithPatientByTalonId(talonId);
    }

    public Talon transferPatientToAnotherTalon(Talon oldTalon, Talon newTalon, boolean isDelete) {
        newTalon.setPatient(oldTalon.getPatient());
        talonRepository.save(newTalon);
        if (isDelete) {
            talonRepository.deleteById(oldTalon.getId());
        } else {
            oldTalon.setPatient(null);
            talonRepository.save(oldTalon);
        }
        return newTalon;
    }

}
