package ru.mis2022.service.dto.impl;

import ru.mis2022.service.dto.StatisticAboutDoctorDtoService;

import java.time.LocalDate;
import java.util.List;

public class StatisticAboutDoctorDtoServiceImpl implements StatisticAboutDoctorDtoService {

    @Override
    public List<StatisticAboutDoctorDtoService> getReport(long doctorId, LocalDate dateStart, LocalDate dateEnd, boolean isClosed) {
        return null;
    }
}
