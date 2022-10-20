package ru.mis2022.service.dto;

import ru.mis2022.models.dto.account.AccountDto;

import java.time.LocalDate;
import java.util.List;

public interface AccountDtoService {

    List<AccountDto> findAccountsDtoByRange(LocalDate dateFrom, LocalDate dateTo);
}
