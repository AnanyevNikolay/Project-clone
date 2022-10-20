package ru.mis2022.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;

@Component
public class DataConvertor {

    public LocalDate toLocalDate(String date) {
        return LocalDate.parse(date, DATE_FORMATTER);
    }

    public String toStrings(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
}
