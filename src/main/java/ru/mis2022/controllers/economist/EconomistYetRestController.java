package ru.mis2022.controllers.economist;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.yet.YetDto;
import ru.mis2022.models.dto.yet.converter.YetDtoConverter;
import ru.mis2022.models.entity.Yet;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.dto.YetDtoService;
import ru.mis2022.service.entity.YetService;
import ru.mis2022.utils.validation.ApiValidationUtils;
import ru.mis2022.utils.validation.OnCreate;
import ru.mis2022.utils.validation.OnUpdate;

import javax.validation.Valid;
import java.util.List;


@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ECONOMIST')")
@RequestMapping("/api/economist/yet")
public class EconomistYetRestController {
    private final YetService yetService;
    private final YetDtoService yetDtoService;
    private final YetDtoConverter yetDtoConverter;

    @ApiOperation("Экономист создает новую УЕТ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные yet добавлены в базу."),
            @ApiResponse(code = 400, message = "Некорректные данные переданы в ДТО."),
            @ApiResponse(code = 415, message = "Данные yet на этот период времени уже установлены!"),
            @ApiResponse(code = 417, message = "Даты указаны неверно!")
    })
    @Validated(OnCreate.class)
    @PostMapping("/create")
    public Response<YetDto> createYet(@Valid @RequestBody YetDto yetDto) {
        ApiValidationUtils
                .expectedFalse(yetDto.dayFrom().isAfter(yetDto.dayTo()),
                        417, "Даты указаны неверно!");
        Yet yet = yetDtoConverter.toEntity(yetDto);
        ApiValidationUtils.expectedFalse(
                yetService.existYetDayFromDayTo(yet.getDayFrom(), yet.getDayTo()).size()!=0,
                415, "Данные yet на этот период времени уже установлены!");
        yetService.save(yet);
        return Response.ok(yetDtoConverter.toDto(yet));
    }

    @ApiOperation("Экономист обновляет существующую УЕТ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные Yet обновлены в базе."),
            @ApiResponse(code = 400, message = "Некорректные данные переданы в ДТО."),
            @ApiResponse(code = 415, message = "Данные yet на этот период времени уже установлены!"),
            @ApiResponse(code = 416, message = "По переданному id запись в базе отсутствует!"),
            @ApiResponse(code = 417, message = "Даты указаны неверно!")
    })
    @Validated(OnUpdate.class)
    @PutMapping("/update")
    public Response<YetDto> updateYet(@Valid @RequestBody YetDto yetDto) {
        ApiValidationUtils
                .expectedFalse(yetDto.dayFrom().isAfter(yetDto.dayTo()),
                        417, "Даты указаны неверно!");
        ApiValidationUtils
                .expectedNotNull(yetService.existById(yetDto.id()),
                        416, "По переданному id запись в базе отсутствует!");
        Yet yet = yetDtoConverter.toEntity(yetDto);
        ApiValidationUtils.expectedFalse(
                yetService.existYetDayFromDayToExceptCurrentId(
                        yet.getId(), yet.getDayFrom(), yet.getDayTo()).size()!=0,
                415, "Данные yet на этот период времени уже установлены!");
        yetService.save(yet);
        return Response.ok(yetDto);
    }

    @ApiOperation("Экономист удаляет УЕТ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные Yet удалены из базы."),
            @ApiResponse(code = 416, message = "По переданному id запись в базе отсутствует!")
    })
    @DeleteMapping("/deleteByID/{id}")
    public Response<Void> deleteYet(@Valid @PathVariable Long id) {
        ApiValidationUtils
                .expectedNotNull(yetService.existById(id),
                        416, "По переданному id запись в базе отсутствует!");
        yetService.deleteById(id);
        return Response.ok();
    }

    @ApiOperation("Экономист получет все УЕТ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список Yet."),
    })
    @GetMapping("/getAllYet")
    public Response<List<YetDto>> getAllYet() {
        return Response.ok(yetDtoService.findAllYetDto());
    }

}

