package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mis2022.models.dto.account.AccountDto;
import ru.mis2022.models.entity.Account;

import java.time.LocalDate;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("""
            SELECT new ru.mis2022.models.dto.account.AccountDto(
                acc.id,
                acc.name,
                acc.date,
                acc.money,
                acc.isFormed)
            FROM Account acc
                WHERE acc.date
                BETWEEN :fromDate AND :dateTo
            """)
    List<AccountDto> findAccountsDtoByRangeDate(@Param("fromDate") LocalDate fromDate,
                                                @Param("dateTo") LocalDate dateTo);
}
