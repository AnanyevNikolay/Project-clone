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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.disease.DiseaseDto;
import ru.mis2022.models.dto.disease.converter.DiseaseDtoConverter;
import ru.mis2022.models.entity.Disease;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.dto.DiseaseDtoService;
import ru.mis2022.service.entity.DiseaseService;
import ru.mis2022.utils.validation.ApiValidationUtils;
import ru.mis2022.utils.validation.OnCreate;

import java.util.List;


@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ECONOMIST')")
@RequestMapping("/api/economist/disease")
public class EconomistDiseaseRestController {
    private final DiseaseDtoService diseaseDtoService;
    private final DiseaseService diseaseService;
    private final DiseaseDtoConverter converter;

    @ApiOperation(value = "Экономист получает все заболевания")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Все заболевания были получены из базы данных.")
    })
    @GetMapping("/getAll")
    public Response<List<DiseaseDto>> getAllDisease() {
        return Response.ok(diseaseDtoService.findAllDiseaseDto());
    }

    @ApiOperation(value = "Экономист сохраняет новое заболевание")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Заболевание было сохранено."),
            @ApiResponse(code = 410, message = "Заболевание с данным идентификатором уже существует."),
    })
    @Validated(OnCreate.class)
    @PostMapping("/create")
    public Response<DiseaseDto> persistDisease(@RequestBody DiseaseDto diseaseDto) {
        ApiValidationUtils
                .expectedFalse(diseaseService.isExistByIdentifier(diseaseDto.identifier()), 410,
                        "Заболевание с данным идентификатором уже существует");
        return Response.ok(converter.toDiseaseDto(
                diseaseService.save(Disease.builder()
                        .identifier(diseaseDto.identifier())
                        .name(diseaseDto.name())
                        .build())));
    }

    @ApiOperation(value = "Экономист удаляет заболевание")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Заболевание было удалено."),
            @ApiResponse(code = 411, message = "Заболевание с переданным id не существует."),
    })
    @DeleteMapping("/delete/{diseaseId}")
    public Response<Void> deleteDiseaseById(@PathVariable Long diseaseId) {
        ApiValidationUtils
                .expectedTrue(diseaseService.isExistById(diseaseId), 411,
                        "Заболевание с переданным id не существует");
        diseaseService.deleteById(diseaseId);
        return Response.ok();
    }
}
