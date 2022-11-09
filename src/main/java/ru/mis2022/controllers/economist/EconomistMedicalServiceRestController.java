package ru.mis2022.controllers.economist;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.service.MedicalServiceDto;
import ru.mis2022.models.dto.service.PriceOfMedicalServiceDto;
import ru.mis2022.models.dto.service.converter.MedicalServiceDtoConverter;
import ru.mis2022.models.entity.MedicalService;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.MedicalServiceService;
import ru.mis2022.service.entity.PriceOfMedicalServiceService;
import ru.mis2022.utils.validation.ApiValidationUtils;
import ru.mis2022.utils.validation.OnCreate;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ECONOMIST')")
@RequestMapping("/api/economist/medicalService")
public class EconomistMedicalServiceRestController {

    private final MedicalServiceService medicalServiceService;
    private final MedicalServiceDtoConverter converter;
    private final PriceOfMedicalServiceService priceOfMedicalServiceService;


    @ApiOperation(value = "Экономист сохраняет новую медицинскую услугу")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Медицинская услуга была сохранена."),
            @ApiResponse(code = 410, message = "Медицинская услуга с таким идентификатором уже существует."),
            @ApiResponse(code = 412, message = "Медицинская услуга с таким именем уже существует"),
    })
    @Validated(OnCreate.class)
    @PostMapping("/create")
    public Response<MedicalServiceDto> persistDisease(@RequestBody MedicalServiceDto medicalServiceDto) {
        ApiValidationUtils
                .expectedFalse(medicalServiceService.isExistByIdentifier(medicalServiceDto.identifier()), 410,
                        "Медицинская услуга с данным  идентификатором уже существует");
        ApiValidationUtils
                .expectedFalse(medicalServiceService.isExistByName(medicalServiceDto.name()), 412,
                        "Медицинская услуга с таким именем уже существует");
        return Response.ok(converter.toMedicalServiceDto(
                medicalServiceService.save(MedicalService.builder()
                        .identifier(medicalServiceDto.identifier())
                        .name(medicalServiceDto.name())
                        .build())));

    }

    @ApiOperation(value = "Экономист устанавливает цену на медицинскую услугу")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Цена установлена"),
            @ApiResponse(code = 409, message = "В этот диапазон уже есть действующая цена")
    })
    @PostMapping("/setPrice/{medicalServiceId}")
    public Response<PriceOfMedicalServiceDto> setMedicalServicePrice(
            @RequestBody PriceOfMedicalServiceDto priceOfMedicalServiceDto,
            @PathVariable("medicalServiceId") Long medicalServiceId) {
        ApiValidationUtils.expectedNull(priceOfMedicalServiceService
                        .getPriceOfMedicalServiceBetweenDayFromAndDayToWithMedicalService(
                                priceOfMedicalServiceDto.dayFrom(), priceOfMedicalServiceDto.dayTo(), medicalServiceId),
                409,
                "В этот диапазон уже есть действующая цена");

        return Response.ok(priceOfMedicalServiceService
                .setPriceByDtoWithMedicalService(priceOfMedicalServiceDto, medicalServiceId));

    }

    @ApiOperation(value = "Экономист разблокирует медицинскую услугу")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Услуга разблокирована"),
            @ApiResponse(code = 410, message = "Услуга не найдена")
    })
    @PatchMapping("/changeIsDisabledOnTrue/{medicalServiceId}")
    public Response<MedicalServiceDto> changeIsDisabledOnTrue(@PathVariable Long medicalServiceId) {
        MedicalService medicalService = medicalServiceService.getMedicalServiceById(medicalServiceId);
        ApiValidationUtils.expectedNotNull(medicalService, 410, "Услуга не найдена");
        return Response.ok(medicalServiceService.changeMedicalServiceIsDisabled(medicalService, true));
    }

    @ApiOperation(value = "Экономист заблокирует медицинскую услугу")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Услуга заблокирована"),
            @ApiResponse(code = 410, message = "Услуга не найдена")
    })
    @PatchMapping("/changeIsDisabledOnFalse/{medicalServiceId}")
    public Response<MedicalServiceDto> changeIsDisableOnFalse(@PathVariable Long medicalServiceId) {
        MedicalService medicalService = medicalServiceService.getMedicalServiceById(medicalServiceId);
        ApiValidationUtils.expectedNotNull(medicalService, 410, "Услуга не найдена");
        return Response.ok(medicalServiceService.changeMedicalServiceIsDisabled(medicalService, false));
    }
}