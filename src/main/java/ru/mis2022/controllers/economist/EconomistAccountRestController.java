package ru.mis2022.controllers.economist;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.models.dto.account.AccountDto;
import ru.mis2022.models.dto.account.converter.ConvertorAccountDto;
import ru.mis2022.models.dto.appeal.converter.AppealDtoConverter;
import ru.mis2022.models.entity.Account;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.dto.AccountDtoService;
import ru.mis2022.service.entity.AccountService;
import ru.mis2022.service.entity.AppealService;
import ru.mis2022.utils.DataConvertor;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ECONOMIST')")
@RequestMapping("/api/economist/account")
public class EconomistAccountRestController {

    private final AccountService accountService;
    private final ConvertorAccountDto convertorAccountDto;
    private final AccountDtoService accountDtoService;
    private final DataConvertor dataConvertor;
    private final AppealService appealService;
    private final AppealDtoConverter appealDtoConverter;

    @ApiOperation("Экономист сохраняет новый счет")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Создан новый пустой счет."),
    })
    @PostMapping("/create")
    public Response<AccountDto> createEmptyAccount(@RequestParam("lastDate") String lastDate,
                                                   @RequestParam("name") String name) {
        Account account = Account.builder().date(dataConvertor.toLocalDate(lastDate)).name(name).build();
        accountService.save(account);
        return Response.ok(convertorAccountDto.toDto(account));
    }

    @ApiOperation("Экономист наполняет счет")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Счет наполнен"),
            @ApiResponse(code = 411, message = "Счет не найден"),
            @ApiResponse(code = 412, message = "Счет уже сформирован"),
            @ApiResponse(code = 413, message = "Обращения не найдены"),
    })
    @PutMapping("/updateAccount/{accountId}")
    public Response <AccountDto> updateAccount(@PathVariable Long accountId){

        ApiValidationUtils.expectedNotNull(accountService.findAccountById(accountId),
                411, "Счет не найден");
        ApiValidationUtils.expectedFalse(accountService.findAccountById(accountId).isFormed(),
                412, "Счет уже сформирован");

        Account account = accountService.findAccountById(accountId);

        List<Appeal> appealList = appealService.findAllCloseAppeals(account.getDate());

        appealService.saveChangeData(appealList, account);

        return Response.ok(convertorAccountDto.toDto(account));
    }

    @ApiOperation("Экономист получает счета в диапазоне.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Счета получены."),
            @ApiResponse(code = 422, message = "Неверная последовательность указанных дат."),
    })
    @GetMapping("/getByRangeDataOrGetAllAccounts")
    public Response<List<AccountDto>> getAccountsByRangeDataOrGetAllAccounts(@Nullable @RequestParam("dateFrom")
                                                                             String dateFrom,
                                                                             @Nullable @RequestParam("dateTo")
                                                                             String dateTo) {
        LocalDate localDateFrom = dataConvertor.toLocalDate(Optional.ofNullable(dateFrom)
                .orElse(dataConvertor.toStrings(LocalDate.EPOCH)));
        LocalDate localDateTo = dataConvertor.toLocalDate(Optional.ofNullable(dateTo)
                .orElse(dataConvertor.toStrings(LocalDate.now())));
        ApiValidationUtils.expectedFalse(
                localDateFrom.isAfter(localDateTo),
                422,
                "Неверная последовательность указанных дат.");
        return Response.ok(accountDtoService.findAccountsDtoByRange(localDateFrom, localDateTo));
    }
}



