package ru.mis2022.feign;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Getter
public class AuthRequestDtoTS {

    @Value("${testsystem.client.username}")
    private String username;

    @Value("${testsystem.client.password}")
    private String password;
}
