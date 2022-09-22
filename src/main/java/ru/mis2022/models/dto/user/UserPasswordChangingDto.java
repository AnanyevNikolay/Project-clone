package ru.mis2022.models.dto.user;

public record UserPasswordChangingDto(String token, String password) {

}
