package ru.mis2022.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mis2022.models.dto.patient.CurrentPatientDto;
import ru.mis2022.models.dto.patient.PatientDto;
import ru.mis2022.models.entity.Patient;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("""
        SELECT new ru.mis2022.models.dto.patient.CurrentPatientDto(
            p.firstName,
            p.lastName,
            p.birthday,
            p.passport,
            p.polis,
            p.snils,
            p.address,
            r.name)
        FROM Patient p
            JOIN Role r ON p.role.id = r.id
        WHERE p.email = :email
        """)
    CurrentPatientDto getCurrentPatientDtoByEmail(String email);

    Patient findByEmail(String email);

    @Query("SELECT p FROM Patient p WHERE p.id = :id")
    Patient findPatientById(Long id);


    @Query("""
            SELECT p FROM Patient p
            WHERE LOWER(CONCAT(p.lastName,' ',p.firstName))
                LIKE LOWER(CONCAT('%',:fullName,'%'))
            ORDER BY p.lastName, p.firstName, p.id
            """)
    List<Patient> findPatientByFullName (String fullName);


    @Query("SELECT t.patient.id FROM Talon t WHERE t.id = :talonId")
    Long findPatientIdByTalonId(Long talonId);

    @Query("""
            SELECT new ru.mis2022.models.dto.patient.PatientDto(
            p.id,
            p.firstName,
            p.lastName,
            p.surname,
            p.birthday,
            p.passport,
            p.polis,
            p.snils
            )
            FROM Patient p
            WHERE p.firstName LIKE :firstName OR p.lastName LIKE :lastName
            OR p.polis LIKE :polis OR p.snils LIKE :snils
            """)
    List<PatientDto> findPatientsByFirstNameOrLastNameOrPolisOrSnilsPattern(
            String firstName, String lastName, String polis, String snils, Pageable pageable
    );

}
