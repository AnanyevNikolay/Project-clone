package ru.mis2022.service.dto.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.dto.yet.YetDto;
import ru.mis2022.repositories.YetRepository;
import ru.mis2022.service.dto.YetDtoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class YetDtoServiceImpl implements YetDtoService {
    private final YetRepository yetRepository;

    @Override
    public List<YetDto> findAllYetDto() {
        return yetRepository.findAllYetDto();
    }
}
