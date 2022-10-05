package ru.mis2022.controllers.economist;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.economist.DatesToCheckDto;
import ru.mis2022.models.entity.PriceOfMedicalService;
import ru.mis2022.models.entity.Yet;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.PriceOfMedicalServiceService;
import ru.mis2022.service.entity.YetService;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/economist/yetChecks")
public class EconomistCheckRestController {
    private final YetService yetService;
    private final PriceOfMedicalServiceService priceOfMedicalServiceService;

    @ApiOperation(value = "Экономист проверяет цены на УЕТ")
    @ApiResponses(value = {
            //todo list2 поле со статусом 200 не должно быть пустым - оно должно отобразить какое задание мы выполнии успешно
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 409, message = "Есть наложение УЕТ друг на друга или наложение цены услуг"),
            @ApiResponse(code = 417, message = "Есть действующие услуги, когда УЕТ не задано"),
            @ApiResponse(code = 422, message = "Одна из дат не задана")
    })
    @PostMapping("/datesOverlap")
    public Response<Void> checkPricesByYet(@RequestBody DatesToCheckDto datesToCheckDto) {
        LocalDate dayFrom = datesToCheckDto.dayFrom();
        LocalDate dayTo = datesToCheckDto.dateTo();

        //todo list3 вынести валидацию полей на null на уровень дто, поправить тесты и убедиться что все работает
        ApiValidationUtils.expectedNotNull(dayFrom, 422, "Одна из дат не задана");
        ApiValidationUtils.expectedNotNull(dayTo, 422, "Одна из дат не задана");

        //todo list3 findAllYetsBetweenDayFromAndDayTo() находит уеты но не сортирует их, это делает checkIfThereAnyDateOverlap() второй итерацией, что делает код неэффективным. Необходимо сразу сортировать уеты в первом методе, а во втором вырезать сортировку
        List<Yet> yets = yetService.findAllYetsBetweenDayFromAndDayTo(dayFrom, dayTo);

        ApiValidationUtils.expectedFalse(yetService.checkIfThereAnyDateOverlap(yets),
                409,
                "Есть наложение УЕТ друг на друга или наложение цены услуг");

        //todo list3 аналогичная проблема с сортировкой
        List<PriceOfMedicalService> priceOfMedicalServices = priceOfMedicalServiceService.findAllPricesBetweenDayFromAndDayTo(dayFrom, dayTo);

        ApiValidationUtils.expectedFalse(priceOfMedicalServiceService.checkIfThereAnyDateOverlap(priceOfMedicalServices),
                409,
                "Есть наложение УЕТ друг на друга или наложение цены услуг");

        //todo list3 аналогичная проблема с сортировкой
        ApiValidationUtils.expectedFalse(priceOfMedicalServiceService.checkIfThereAnyActiveServiceWhileYetIsNot(priceOfMedicalServices, yets),
                417,
                "Есть действующие услуги, когда УЕТ не задано");

        return Response.ok();

    }
}
