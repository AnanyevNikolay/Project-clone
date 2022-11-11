package ru.mis2022.controllers.hrManager;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.doctor.DoctorDto;
import ru.mis2022.models.dto.doctor.converter.DoctorDtoConverter;
import ru.mis2022.models.dto.user.UserDto;
import ru.mis2022.models.dto.user.converter.UserDtoConverter;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.User;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.UserService;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR_MANAGER')")
@RequestMapping("/api/hr_manager")
public class HrManagerPersonalRestController {
    private final UserService userService;
    private final UserDtoConverter converter;
    private final DoctorService doctorService;
    private final DoctorDtoConverter doctorDtoConverter;

    @ApiOperation("Кадровик получает всех сотрудников по паттерну")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список сотрудников"),
    })
    @GetMapping("/allUsers")
    public Response<List<UserDto>> findPersonalByFirstAndLastName(
            @RequestParam(required = false, defaultValue = "") String fullName) {
        String fullNames = fullName.replaceAll("\\s+", "%");
        return Response.ok(converter.toListDto(
                userService.findPersonalByFullName(fullNames, Role.RolesEnum.PATIENT.name())));
    }

    @ApiOperation("Список именинников за указанный период")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список именинников")
    })
    @GetMapping("/findBirthdayInRange")
    public Response<List<UserDto>> findAllBirthdayInRange(
            @RequestParam(required = false, defaultValue = "30") int daysCount) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysCount);
        List<User> allBirthday;

        //Костыль для учета ДР после НГ + Сортировка по порядку ДР
        if (start.getYear() == end.getYear()) {
            allBirthday = userService.findPersonalByBirthdayInRange(start, end);
        } else {
            allBirthday = userService.findPersonalByBirthdayInRange(
                    start, (LocalDate.of(start.getYear(), 12, 31)));
            allBirthday.addAll(userService.findPersonalByBirthdayInRange(
                    (LocalDate.of(end.getYear(), 1, 1)), end));
        }
        return Response.ok(converter.toListDto(allBirthday));
    }

    @ApiOperation("Кадровик назначает заведующего отделением")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Заведующий отделением успешно назначен"),
            @ApiResponse(code = 411, message = "Доктора с таким id не существует"),
            @ApiResponse(code = 412, message = "В отделении уже две заведующих отделением")
    })
    @PostMapping("/addChiefDoctorForDepartment")
    public Response<DoctorDto> addChiefDoctor(@RequestParam(name = "departmentId") Long departmentId,
                                              @RequestParam(name = "doctorId") Long doctorId) {

        Doctor doctor = doctorService.findByIdAndDepartment(doctorId, departmentId);

        ApiValidationUtils.expectedNotNull(doctor,
                411, "Доктора с таким id не существует");

        ApiValidationUtils.expectedFalse(doctorService.countOfChiefDoctorInDepartment(departmentId),
                412, "В отделении уже две заведующих отделением");

        return Response.ok(doctorDtoConverter.toDto(doctorService
                .changeRoleDoctor(doctor, Role.RolesEnum.CHIEF_DOCTOR.name())));

    }

    @ApiOperation("Кадровик назначает главного врача")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Главный врач успешно назначен"),
            @ApiResponse(code = 411, message = "Доктора с таким id не существует"),
            @ApiResponse(code = 412, message = "В организации уже две главных врачей")
    })
    @PostMapping("/addMainDoctorForOrganization")
    public Response<DoctorDto> addMainDoctor(@RequestParam Long doctorId) {

        Doctor doctor = doctorService.findById(doctorId);

        ApiValidationUtils.expectedNotNull(doctor,
                411, "Доктора с таким id не существует");

        ApiValidationUtils.expectedFalse(doctorService.findByIdForMain(),
                412, "В организации уже две главных врачей");

        return Response.ok(doctorDtoConverter.toDto(doctorService
                .changeRoleDoctor(doctor, Role.RolesEnum.MAIN_DOCTOR.name())));

    }

    @ApiOperation("Список сотрудников идущих в отпуск за указанный период")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список сотрудников идущих в отпуск за указанный период"),
    })
    @GetMapping("/findAllEmployeesWhoGoVacationInRange")
    public Response<List<UserDto>> findAllEmployeesWhoGoVacationInRange(
            @RequestParam(required = false, defaultValue = "30") int daysCount) {

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysCount);
        List<User> allEmployeesGoVacation = userService.findPersonalWhoGoVacationInRange(start, end);
        return Response.ok(converter.toListDto(allEmployeesGoVacation));
    }
}
