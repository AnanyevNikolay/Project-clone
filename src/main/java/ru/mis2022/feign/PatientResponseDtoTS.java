package ru.mis2022.feign;

public record PatientResponseDtoTS(
    Long id,
    String firstName,
    String lastName,
    String surname,
    String address,
    String passport,
    String snils,
    String polis,
    String birthday,
    String dateOfDeath
) {
}
