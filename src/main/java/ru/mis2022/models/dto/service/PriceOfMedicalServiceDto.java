package ru.mis2022.models.dto.service;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PriceOfMedicalServiceDto(BigDecimal price,
                                       @JsonSerialize(using = LocalDateSerializer.class)
                                       @JsonDeserialize(using = LocalDateDeserializer.class)
                                       @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
                                       LocalDate dayFrom,
                                       @JsonSerialize(using = LocalDateSerializer.class)
                                       @JsonDeserialize(using = LocalDateDeserializer.class)
                                       @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
                                       LocalDate dayTo
                                       ) {}
