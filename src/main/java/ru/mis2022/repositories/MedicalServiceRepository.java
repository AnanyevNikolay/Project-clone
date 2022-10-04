package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mis2022.models.entity.MedicalService;

import java.util.List;
import java.util.Optional;

public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {

   boolean existsByIdentifier(String identifier);

   boolean existsByName(String name);

   @Query(value = """
         SELECT ms
         FROM MedicalService ms
            JOIN Visit v ON ms.visit.id = v.id
            JOIN Appeal a ON v.appeal.id = a.id
         WHERE ms.visit.appeal.patient.id = :id
         """)
   Optional<List<MedicalService>> getMedicalServicesDtoVisitedByCurrentPatientWithId(long id);

   MedicalService getMedicalServiceById(Long id);
}
