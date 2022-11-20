package ru.mis2022.models.dto.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AccountDto(Long id,
                         String name,
                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
                         LocalDate data,
                         Long money,
                         boolean isFormed)
{}