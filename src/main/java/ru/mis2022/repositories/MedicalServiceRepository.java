package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mis2022.models.entity.MedicalService;

import java.util.Set;

public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {

   boolean existsByIdentifier(String identifier);

   boolean existsByName(String name);

   MedicalService getMedicalServiceById(Long id);

   @Query(value = """
           SELECT ms
           FROM MedicalService ms
           WHERE ms.id IN (:ids)
              AND ms.department.id = :departmentId
           """)
   Set<MedicalService> getMedicalServicesByIdsAndDepartment(Set<Long> ids, long departmentId);
}
