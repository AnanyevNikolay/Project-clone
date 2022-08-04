package ru.mis2022.controllers.doctor;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.talon.TalonDto;
import ru.mis2022.models.dto.talon.convector.TalonConverter;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.models.entity.User;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.TalonService;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.util.List;
import java.util.Set;


@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
@RequestMapping("/api/doctor/talon")
public class DoctorTalonsRestController {
    @Value("${mis.property.doctorSchedule}")
    private Integer numberOfDays;
    @Value("${mis.property.talon}")
    private Integer numbersOfTalons;

    private final DoctorService doctorService;
    private final TalonService talonService;
    private final TalonConverter talonConverter;



    @PostMapping("/add")
    public Response<List<TalonDto>> addTalons() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Doctor doctor = doctorService.findByEmail(currentUser.getEmail());

        ApiValidationUtils
                .expectedFalse(talonService.findTalonsCountByIdAndDoctor(numberOfDays, doctor) >= 1, 401,
                        "У доктора есть талоны на данные дни");

        Set<Talon> talons = talonService.persistTalonsForDoctor(doctor, numberOfDays, numbersOfTalons);

        return Response.ok(talonConverter.toTalonDto(talons, doctor.getId()));
    }
}
