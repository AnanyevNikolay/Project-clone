package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mis2022.models.entity.Appeal;

import java.util.List;
import java.util.Optional;

public interface AppealRepository extends JpaRepository<Appeal, Long> {

    @Query(value = """
            SELECT a
            FROM Appeal a
                LEFT JOIN FETCH a.visits
            WHERE a.patient.id = :patientId
            """)
    Optional<List<Appeal>> getAppealsDtoByPatientId(long patientId);

    @Query(value = """
            SELECT DISTINCT a
            FROM Appeal a      
                JOIN Doctor d
                    ON a.disease.department.id = d.department.id
                LEFT JOIN a.visits v                
                LEFT JOIN v.medicalServices ms                    
            WHERE a.patient.id = :patientId AND a.isClosed = :isClosed AND d.id = :doctorId
            """)
    List<Appeal> getOpenAppealsDtoByPatientId(long patientId, boolean isClosed, long doctorId);

    @Query("SELECT a FROM Appeal a WHERE a.id = :id")
    Appeal findAppealById(Long id);
}
