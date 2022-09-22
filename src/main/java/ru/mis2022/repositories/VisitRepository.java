package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mis2022.models.entity.Visit;

import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    @Query(value = """
            SELECT v
            FROM Visit v
                JOIN Doctor d on v.doctor.id = d.id
                JOIN v.medicalServices
            WHERE v.appeal.patient.id = :id
            """)
    Optional<List<Visit>> getVisitsOfCurrentPatientById(long id);
}
