package ru.mis2022.models.dto.economist.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mis2022.models.dto.economist.EconomistDto;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.service.entity.RoleService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class EconomistDtoConverter {
    private final RoleService roleService;

    public EconomistDto toDto(Economist entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String birthday = entity.getBirthday().format(formatter);

        return EconomistDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .surname(entity.getSurname())
                .birthday(birthday.replaceAll("-", "."))
                .role(entity.getRole().getName())
                .build();
    }

    public Economist toEntity(EconomistDto dto) {
        DateTimeFormatter df = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("dd.MM.yyyy")
                .toFormatter(Locale.ENGLISH);

        return new Economist(
                dto.getEmail(),
                dto.getPassword(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getSurname(),
                LocalDate.parse(dto.getBirthday(), df),
                roleService.findByName(dto.getRole())
        );
    }
}
