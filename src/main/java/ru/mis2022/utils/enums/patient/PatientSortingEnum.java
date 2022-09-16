package ru.mis2022.utils.enums.patient;

public enum PatientSortingEnum {
    FIRST_NAME("firstName"), LAST_NAME("lastName"),
    POLIS("polis"), SNILS("snils"), ID("id");

    private final String value;

    PatientSortingEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
