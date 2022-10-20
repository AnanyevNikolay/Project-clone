package ru.mis2022.service.dto.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.dto.account.AccountDto;
import ru.mis2022.repositories.AccountRepository;
import ru.mis2022.service.dto.AccountDtoService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountDtoServiceImpl implements AccountDtoService {
    private final AccountRepository accountRepository;

    @Override
    public List<AccountDto> findAccountsDtoByRange(LocalDate dateFrom, LocalDate dateTo) {
        return accountRepository.findAccountsDtoByRangeDate(dateFrom, dateTo);
    }
}
