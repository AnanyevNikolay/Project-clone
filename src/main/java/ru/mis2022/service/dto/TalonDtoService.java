package ru.mis2022.service.dto;

import ru.mis2022.models.dto.registrar.CurrentDepartamentDoctorTalonsDto;
import ru.mis2022.models.dto.talon.DoctorTalonsDto;
import ru.mis2022.models.dto.talon.TalonDto;

import java.time.LocalDateTime;
import java.util.List;

public interface TalonDtoService {

    List<TalonDto> findAllByDoctorId(long doctorId);

    List<TalonDto> findTalonsByDoctorIdAndTimeBetween(Long doctorId, LocalDateTime timeNow, LocalDateTime timeEnd);

    List<DoctorTalonsDto> getTalonsByDoctorIdAndDay(long doctorId, LocalDateTime startDayTime,
                                                    LocalDateTime endDayTime);

    List<TalonDto> findAllByPatientId(long id);

    List<CurrentDepartamentDoctorTalonsDto> getCurrentDepartamentDoctorTalonsDto(LocalDateTime timeStrart, LocalDateTime timeEnd);

}
