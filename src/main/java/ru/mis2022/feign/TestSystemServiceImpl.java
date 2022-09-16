package ru.mis2022.feign;

import feign.FeignException;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TestSystemServiceImpl implements TestSystemService {

    TestSystemFeignClient feignClient;

    AuthRequestDtoTS authRequest;

    public TestSystemServiceImpl(TestSystemFeignClient feignClient, AuthRequestDtoTS authRequestDtoTS) {
        this.feignClient = feignClient;
        this.authRequest = authRequestDtoTS;
    }

    @Getter
    private static AuthTokenTS authToken;


    public boolean checkExpiration() {
        if (authToken == null) {
            return false;
        }
        Date dateCreated = new Date(authToken.created());
        Date dateEnd = new Date(authToken.created() + authToken.expiration());

        return   dateCreated.compareTo(dateEnd) == -1; //true, если время еще не истекло
    }

    @Override
    public boolean login() {
       try {
           authToken = feignClient.login(authRequest);
       }
       catch (FeignException e) {
            return false;
        }
        return authToken!=null;
    }

    @Override
    public boolean updateToken() {
        if (authToken == null || !checkExpiration()) {
            login();
        }
        return checkExpiration();
    }

    @Override
    public PatientResponseDtoTS findPeople(PatientRequestDtoTS patientRequestDto) {
        return feignClient.getAllPeoples(authToken.token(), patientRequestDto);
    }
}

