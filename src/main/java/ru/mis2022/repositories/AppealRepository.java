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

}
