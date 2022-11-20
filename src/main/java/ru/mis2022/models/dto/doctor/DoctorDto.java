package ru.mis2022.models.dto.doctor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.mis2022.utils.validation.OnCreate;
import ru.mis2022.utils.validation.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DoctorDto {

    @Null(groups = OnCreate.class, message = "id должен быть равен null")
    @Positive(groups = OnUpdate.class, message = "id должен быть положительным")
    private Long id;

    @NotBlank(message = "email не должен быть пустым")
    @Pattern(regexp = "^(.+)@(\\S+)$", groups = {OnCreate.class, OnUpdate.class},
            message = "email должен быть корректным адресом электронной почты")
    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String surname;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "дата не должна быть пустой")
    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d", groups = {OnCreate.class, OnUpdate.class},
            message = "Дата должна быть в формате: DD.MM.YYYY")
    private String birthday;

    private String role;

    private String department;
    private int daysForVacations;

    public DoctorDto(Long id,
                     String email,
                     String s,
                     String f_name,
                     String l_name,
                     String surName,
                     String s1,
                     String doctor,
                     String departmentTest) {
    }

    public DoctorDto(String firstName,
                     String lastName,
                     String surname,
                     String department,
                     int daysForVacations) {
    }

}
