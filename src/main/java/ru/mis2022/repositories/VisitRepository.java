package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mis2022.models.entity.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    @Query(value = """
            SELECT v
            FROM Visit v
                JOIN FETCH v.appeal
            WHERE v.id = :visitId 
                AND v.doctor.id = :doctorId
            """)
    Visit findByIdAndDoctorIdWithAppeal(long visitId, long doctorId);
}
