package ru.mis2022.models.dto.talon.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.dto.patient.converter.PatientDtoConverter;
import ru.mis2022.models.dto.talon.TalonByDay;
import ru.mis2022.models.dto.talon.TalonDto;
import ru.mis2022.models.entity.Talon;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.mis2022.utils.DateFormatter.DATE_FORMATTER;

@Service
@RequiredArgsConstructor
public class TalonDtoConverter {
    private final PatientDtoConverter patientDtoConverter;

    public List<TalonByDay> groupByDay(List<TalonDto> dtos) {
        Map<String, List<TalonDto>> map = new HashMap<>();
        for (TalonDto dto : dtos) {
            String date = dto.getTime().toLocalDate().format(DATE_FORMATTER);
            if (map.containsKey(date)) {
                List<TalonDto> list = new ArrayList<>(map.get(date));
                list.add(dto);
                map.put(date, list);
            } else {
                map.put(date, List.of(dto));
            }
        }
        List<TalonByDay> result = new ArrayList<>();
        for (String date : map.keySet()) {
            result.add(new TalonByDay(date, map.get(date)));
        }
        result.sort(Comparator.comparing(o -> LocalDate.parse(o.date(), DATE_FORMATTER)));
        return result;
    }

    public List<TalonDto> toTalonDtoByDoctorId(List<Talon> talons, Long doctorId) {
        List<TalonDto> setDto = new ArrayList<>();
        for (Talon talon : talons) {
            setDto.add(TalonDto.builder()
                    .time(talon.getTime())
                    .id(talon.getId())
                    .doctorId(doctorId)
                    .build());
        }
        return setDto;
    }

    public TalonDto talonToTalonDto(Talon talon) {
        return TalonDto.builder()
                .id(talon.getId())
                .time(talon.getTime())
                .doctorId(talon.getDoctor().getId())
                .patient(patientDtoConverter.toDto(talon.getPatient()))
                .build();
    }
}
