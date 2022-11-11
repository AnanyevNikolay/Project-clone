
package ru.mis2022.controllers.registrar;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.talon.TalonDto;
import ru.mis2022.models.dto.talon.converter.TalonDtoConverter;
import ru.mis2022.models.entity.Talon;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.DepartmentService;
import ru.mis2022.service.entity.TalonService;
import ru.mis2022.utils.validation.ApiValidationUtils;


@RestController
@RequestMapping("/api/registrar/talon")
@PreAuthorize("hasRole('REGISTRAR')")
@RequiredArgsConstructor
public class RegistrarTalonRestController {

    private final TalonService talonService;

    private final DepartmentService departmentService;

    private final TalonDtoConverter talonDtoConverter;

    @ApiOperation("Регистратор переносит запись к другому врачу")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Запись перенесена к другому врачу"),
            @ApiResponse(code = 402, message = "Талона со старым id нет"),
            @ApiResponse(code = 403, message = "Талона с новым id нет"),
            @ApiResponse(code = 404, message = "Талоны находятся в разных отделениях"),

    })
    @PostMapping("/transferAppointment")
    public Response<TalonDto> transferAppointmentToAnotherDoctor(@RequestParam(name = "oldId") long oldId,
                                                                 @RequestParam(name = "newId") long newId,
                                                                 @RequestParam(name = "isDelete") boolean isDelete) {
        Talon oldTalon = talonService.findTalonWithPatientByTalonId(oldId);
        ApiValidationUtils.expectedNotNull(oldTalon, 402, "Талона со старым id нет");
        Talon newTalon = talonService.findTalonWithPatientByTalonId(newId);
        ApiValidationUtils.expectedNotNull(newTalon, 403, "Талона с новым id нет");
        ApiValidationUtils.expectedEqual(
                departmentService.findDepartmentByTalonId(oldId),
                departmentService.findDepartmentByTalonId(newId),
                404, "Талоны находятся в разных отделениях"
        );
        return Response.ok(talonDtoConverter.talonToTalonDto(talonService.
                transferPatientToAnotherTalon(oldTalon, newTalon, isDelete)
        ));
    }

}