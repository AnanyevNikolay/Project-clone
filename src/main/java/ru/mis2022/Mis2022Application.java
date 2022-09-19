package ru.mis2022;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
@EnableFeignClients
public class Mis2022Application {

    public static void main(String[] args) {
        SpringApplication.run(Mis2022Application.class, args);
    }

}
