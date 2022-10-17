package ru.mis2022.controllers.hrManager;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.doctor.DoctorDto;
import ru.mis2022.models.dto.doctor.converter.DoctorDtoConverter;
import ru.mis2022.models.entity.Doctor;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.DoctorService;
import ru.mis2022.service.entity.InviteService;
import ru.mis2022.service.entity.MailService;
import ru.mis2022.service.entity.UserService;
import ru.mis2022.utils.validation.ApiValidationUtils;
import ru.mis2022.utils.validation.OnCreate;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR_MANAGER')")
@RequestMapping("/api/hr_manager/doctor")
public class HrManagerDoctorRestController {

    private final UserService userService;
    private final DoctorService doctorService;
    private final MailService mailService;
    private final InviteService inviteService;
    private final DoctorDtoConverter doctorDtoConverter;
    private final DepartmentService departmentService;

    @ApiOperation("Кадровик сохраняет нового доктора")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Доктор добавлен в базу."),
            @ApiResponse(code = 400, message = "Некорректные данные переданы в ДТО."),
            @ApiResponse(code = 411, message = "Такого отделения не существует!"),
            @ApiResponse(code = 412, message = "Такой адрес электронной почты уже используется!")
    })
    @Validated(OnCreate.class)
    @PostMapping("/{departmentId}/createDoctor")
    public Response<DoctorDto> createDoctor(@Valid @RequestBody DoctorDto doctorDto,
                                            @PathVariable("departmentId") Long departmentId) {
        ApiValidationUtils
                .expectedTrue(departmentService.isExistById(departmentId),
                        411, "Такого отделения не существует!");
        ApiValidationUtils
                .expectedFalse(userService.existsByEmail(doctorDto.getEmail()),
                        412, "Такой адрес электронной почты уже используется!");
        Doctor doctor = doctorService.persist(doctorDtoConverter.toEntity(doctorDto));
        doctor.setDepartment(departmentService.findDepartmentById(departmentId));
        mailService.sendRegistrationInviteByEmail(inviteService.persist(doctor), doctor);
        return Response.ok(doctorDtoConverter.toDto(doctor));
    }
}
