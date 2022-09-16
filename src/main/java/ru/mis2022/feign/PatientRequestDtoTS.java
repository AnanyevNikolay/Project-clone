package ru.mis2022.feign;

public record PatientRequestDtoTS(
        String firstName,
        String lastName,
        String surname,
        String passport,
        String snils,
        String polis
) {
}
