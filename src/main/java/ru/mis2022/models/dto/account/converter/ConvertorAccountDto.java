package ru.mis2022.models.dto.account.converter;

import org.springframework.stereotype.Component;
import ru.mis2022.models.dto.account.AccountDto;
import ru.mis2022.models.entity.Account;

@Component
public class ConvertorAccountDto {

    public AccountDto toDto(Account entity){
        return AccountDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .money(entity.getMoney())
                .data(entity.getDate())
                .isFormed(entity.isFormed())
                .build();
    }

    public Account toEntity(AccountDto dto){
        return Account.builder()
                .id(dto.id())
                .name(dto.name())
                .money(dto.money())
                .date(dto.data())
                .isFormed(dto.isFormed())
                .build();
    }
}
