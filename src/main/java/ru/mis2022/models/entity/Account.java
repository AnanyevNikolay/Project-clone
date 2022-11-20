package ru.mis2022.models.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.Set;

/**
 * Account - Счет
 * В последний день каждого месяца экономист формирует счет на оплату в министерство здравоохранения.
 * В этот счет попадают все закрытые обращения в рамках ОМС.
 * Обращение может находиться только в одном счете.
 * Обращения, которые находятся в счете нельзя модифицировать
 * счет можно удалять и пересоздавать - Обращения не должны удаляться.
 * из счета можно удалять обращения - они не должны удаляться
 * можно проводить поиск новых закрытых обращений, которых нет в счете для добавления в счет
 */


@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate date;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Set<Appeal> appeals;

    private Long money;

    @Column(name = "is_formed")
    private boolean isFormed;
}