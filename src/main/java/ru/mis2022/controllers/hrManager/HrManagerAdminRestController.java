package ru.mis2022.controllers.hrManager;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.administrator.AdministratorDto;
import ru.mis2022.models.dto.administrator.converter.AdministratorDtoConverter;
import ru.mis2022.models.entity.Administrator;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.AdministratorService;
import ru.mis2022.service.entity.InviteService;
import ru.mis2022.service.entity.MailService;
import ru.mis2022.service.entity.UserService;
import ru.mis2022.utils.validation.ApiValidationUtils;
import ru.mis2022.utils.validation.OnCreate;
import ru.mis2022.utils.validation.OnUpdate;

import javax.validation.Valid;


@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR_MANAGER')")
@RequestMapping("/api/hr_manager")
public class HrManagerAdminRestController {
    private final AdministratorService administratorService;
    private final AdministratorDtoConverter administratorDtoConverter;
    private final UserService userService;
    private final MailService mailService;
    private final InviteService inviteService;

    @ApiOperation("Кадровик сохраняет нового админа")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Администратор добавлен в базу."),
            @ApiResponse(code = 400, message = "Некорректные данные переданы в ДТО."),
            @ApiResponse(code = 412, message = "Такой адрес электронной почты уже используется!")
    })
    @Validated(OnCreate.class)
    @PostMapping("/admin/createAdmin")
    public Response<AdministratorDto> createAdmin(@Valid @RequestBody AdministratorDto administratorDto) {
        ApiValidationUtils
                .expectedFalse(userService.existsByEmail(administratorDto.getEmail()),
                        412, "Такой адрес электронной почты уже используется!");
        Administrator administrator = administratorService.persist(administratorDtoConverter.toEntity(administratorDto));
        mailService.sendRegistrationInviteByEmail(inviteService.persist(administrator), administrator);
        return Response.ok(administratorDtoConverter.toDto(administrator));
    }

    @ApiOperation("Кадровик обновляет существующего админа")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные администратора обновлены в базе."),
            @ApiResponse(code = 400, message = "Некорректные данные переданы в ДТО."),
            @ApiResponse(code = 410, message = "По переданному id администратор не найден."),
            @ApiResponse(code = 412, message = "Такой адрес электронной почты уже используется.")
    })
    @Validated(OnUpdate.class)
    @PutMapping("/admin/updateAdmin")
    public Response<AdministratorDto> updateAdmin(@Valid @RequestBody AdministratorDto administratorDto) {
        ApiValidationUtils
                .expectedTrue(administratorService.isExistById(administratorDto.getId()),
                        410, "По переданному id администратор не найден.");
        ApiValidationUtils.expectedFalse(
                        userService.isExistsByNameAndId(administratorDto.getEmail(),
                                administratorDto.getId()),
                412, "Такой адрес электронной почты уже используется!");
        Administrator administrator =
                administratorService.merge(
                        administratorDtoConverter.toEntity(
                                administratorDto));
        return Response.ok(administratorDtoConverter.toDto(administrator));
    }
}
