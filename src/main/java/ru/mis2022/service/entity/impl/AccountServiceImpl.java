package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.entity.Account;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.repositories.AccountRepository;
import ru.mis2022.service.entity.AccountService;
import ru.mis2022.service.entity.AppealService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AppealService appealService;

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public void deleteAll() {
        accountRepository.deleteAll();
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findAccountById(Long id) {
        return accountRepository.findAccountById(id);
    }

    @Override
    public void saveChangeData(Account account) {
        List<Appeal> appealList = appealService.findAllCloseAppeals((account).getDate());
        appealService.saveChangeData(appealList, account);
    }
}
