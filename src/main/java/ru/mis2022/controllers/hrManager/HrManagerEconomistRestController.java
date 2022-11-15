package ru.mis2022.controllers.hrManager;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.economist.EconomistDto;
import ru.mis2022.models.dto.economist.converter.EconomistDtoConverter;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.EconomistService;
import ru.mis2022.service.entity.UserService;
import ru.mis2022.utils.validation.ApiValidationUtils;
import ru.mis2022.utils.validation.OnCreate;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR_MANAGER')")
@RequestMapping("/api/hr_manager/economist")
public class HrManagerEconomistRestController {

    private final EconomistService economistService;

    private final EconomistDtoConverter economistDtoConverter;

    private final UserService userService;

    @ApiOperation("Кадровик сохраняет нового экономиста")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Экономист добавлен в базу."),
            @ApiResponse(code = 400, message = "Некорректные данные переданы в ДТО."),
            @ApiResponse(code = 412, message = "Такой адрес электронной почты уже используется!")
    })
    @Validated(OnCreate.class)
    @PostMapping("/createEconomist")
    public Response<EconomistDto> createEconomist(@Valid @RequestBody EconomistDto economistDto) {
        ApiValidationUtils
                .expectedFalse(userService.existsByEmail(economistDto.getEmail()),
                        412, "Такой адрес электронной почты уже используется!");
        Economist economist = economistService.
                saveAndSendRegistInviteToEconomist(economistDtoConverter.toEntity(economistDto));
        return Response.ok(economistDtoConverter.toDto(economist));
    }

}
