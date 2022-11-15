package ru.mis2022.service.dto;

import java.time.LocalDate;
import java.util.List;

public interface StatisticAboutDoctorDtoService {
    List<StatisticAboutDoctorDtoService> getReport(long doctorId,
                                                   LocalDate dateStart,
                                                   LocalDate dateEnd,
                                                   boolean isClosed);
}