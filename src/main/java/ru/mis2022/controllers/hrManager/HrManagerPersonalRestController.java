package ru.mis2022.controllers.hrManager;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.user.UserDto;
import ru.mis2022.models.dto.user.converter.UserDtoConverter;
import ru.mis2022.models.entity.Role;
import ru.mis2022.models.entity.User;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.UserService;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR_MANAGER')")
@RequestMapping("/api/hr_manager")
public class HrManagerPersonalRestController {
    private final UserService userService;
    private final UserDtoConverter converter;

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
}
