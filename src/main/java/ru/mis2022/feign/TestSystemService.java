package ru.mis2022.feign;

public interface TestSystemService {

    boolean login();

    boolean updateToken();

    PatientResponseDtoTS findPeople(PatientRequestDtoTS patientRequestDtoTS);

}
