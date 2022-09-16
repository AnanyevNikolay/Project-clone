package ru.mis2022.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value="TestSystem", url="${testsystem.server.address}")
public interface TestSystemFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/auth/login", consumes = "application/json")
    AuthTokenTS login(AuthRequestDtoTS authRequest);

    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/registrar/findpeople")
    PatientResponseDtoTS getAllPeoples(@RequestHeader("Authorization") String token, @RequestBody PatientRequestDtoTS patientRequestDtoTS);


}
