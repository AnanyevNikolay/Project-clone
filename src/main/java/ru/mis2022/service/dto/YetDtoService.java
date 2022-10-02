package ru.mis2022.service.dto;

import ru.mis2022.models.dto.yet.YetDto;

import java.util.List;

public interface YetDtoService {
    List<YetDto> findAllYetDto();
}
