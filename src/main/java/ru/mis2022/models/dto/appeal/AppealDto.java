package ru.mis2022.models.dto.appeal;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mis2022.models.entity.Account;
import ru.mis2022.models.entity.Visit;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppealDto {

    private Long id;

    private Long patientId;

    private Long diseaseId;

    private Set<Visit> visits;

    private Account account;

    private Boolean isClosed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate localDate;
}
