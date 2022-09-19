package ru.mis2022.feign;

public record AuthTokenTS(String token, Long expiration, Long created) {

}
