package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Account;

import java.util.List;

public interface AccountService {

    Account save(Account account);

    void deleteAll();

    List<Account> findAll();
}
