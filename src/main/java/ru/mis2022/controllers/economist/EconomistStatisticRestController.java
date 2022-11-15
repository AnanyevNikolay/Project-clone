package ru.mis2022.controllers.economist;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.dto.StatisticAboutDoctorDtoService;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ECONOMIST')")
@RequestMapping("/api/economist/statistic")
public class EconomistStatisticRestController {

    @ApiOperation(value = "Экономист получает отчет о работе врача")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Метод возвращает отчет"),
    })
    @GetMapping("/doctor")
    public Response<StatisticAboutDoctorDtoService> getCurrentEconomistDto() {
        Economist economist = (Economist) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Response.ok();
    }
}
