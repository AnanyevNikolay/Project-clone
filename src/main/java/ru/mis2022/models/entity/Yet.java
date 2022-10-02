package ru.mis2022.models.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;


/**
 * УЕТ - Условная единица трудозатрат - относительная сложность выполнения какой-либо работы.
 * Введена для удобства изменения оплаты труда сразу по всем услугам
 * Редактируется экономистом.
 * Цена может изменятьсо со временем.
 * Цена не может отсутствовать в какой либо период времени.
 * Цена не может равняться 0.
 * УЕТ может стоить отпределенную сумму денег в фиксированный период времени кратный месяцам,
 * например:
 * с 01.01.2021 по 31.05.2021 цена 78.50
 * с 01.06.2021 по null       цена 78.00 (означает - по настоящее время)
 */

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Yet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double price;

    private LocalDate dayFrom;

    private LocalDate dayTo;

    public Yet(Double price, LocalDate dayFrom, LocalDate dayTo) {
        this.price = price;
        this.dayFrom = dayFrom;
        this.dayTo = dayTo;
    }
}
