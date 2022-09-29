package ru.mis2022.service.dto.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.dto.registrar.CurrentDepartamentDoctorTalonsDto;
import ru.mis2022.models.dto.talon.DoctorTalonsDto;
import ru.mis2022.models.dto.talon.TalonDto;
import ru.mis2022.models.dto.talon.converter.TalonDtoConverter;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.repositories.TalonRepository;
import ru.mis2022.service.dto.TalonDtoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TalonDtoServiceImpl implements TalonDtoService {
    private final TalonRepository talonRepository;
    private final TalonDtoConverter talonDtoConverter;

    @Override
    public List<TalonDto> findAllByDoctorId(long doctorId) {
        List<Talon> talonList = talonRepository.findAllDtoByDoctorId(doctorId);
        return talonList.stream().map(talonDtoConverter::talonToTalonDto).collect(Collectors.toList());
    }

    @Override
    public List<TalonDto> findTalonsByDoctorIdAndTimeBetween(Long doctorId, LocalDateTime timeNow, LocalDateTime timeEnd) {
        List<Talon> talonList = talonRepository.findTalonsByDoctorIdAndTimeBetween(doctorId, timeNow, timeEnd);
        return talonList.stream().map(talonDtoConverter::talonToTalonDto).collect(Collectors.toList());
    }

    @Override
    public List<TalonDto> findAllByPatientId(long patientId) {
        List<Talon> talonList = talonRepository.findAllDtoByPatientId(patientId);
        return talonList.stream().map(talonDtoConverter::talonToTalonDto).collect(Collectors.toList());
    }


    @Override
    public List<DoctorTalonsDto> getTalonsByDoctorIdAndDay(
            long doctorId, LocalDateTime startDayTime, LocalDateTime endDayTime) {
        return talonRepository.talonsByDoctorByDay(doctorId, startDayTime, endDayTime);
    }

    @Override
    public List<CurrentDepartamentDoctorTalonsDto> getCurrentDepartamentDoctorTalonsDto(LocalDateTime timeStrart, LocalDateTime timeEnd) {
        return talonRepository.getCurrentDepartamentDoctorTalonsDto(timeStrart, timeEnd);
    }
}
