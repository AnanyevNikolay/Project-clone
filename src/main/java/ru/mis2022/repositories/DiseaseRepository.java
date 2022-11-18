package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mis2022.models.dto.disease.DiseaseDto;
import ru.mis2022.models.entity.Appeal;
import ru.mis2022.models.entity.Disease;

import java.util.List;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Long> {

    @Query("""
            SELECT new ru.mis2022.models.dto.disease.DiseaseDto(
                d.id,
                d.identifier,
                d.name,
                d.disabled)
            FROM Disease d
            """)
    List<DiseaseDto> findAllDiseaseDto();

    boolean existsByIdentifier(String identifier);


    @Query("""
            select new  ru.mis2022.models.dto.disease.DiseaseDto(
            dis.id,
            dis.identifier,
            dis.name,
            dis.disabled)
            from Disease dis
            join Department dep on dep.id = dis.department.id
            join Doctor doc on dep.id = doc.department.id
            where doc.id=:docId
                AND dis.disabled = :dis
            """)
    List<DiseaseDto> findDiseaseByDepartmentDoctors(@Param("docId") Long docId, @Param("dis") boolean disabled);

    Disease findDiseaseById(Long id);

    @Query("""
            SELECT case when count(dis)> 0 then true else false end
            FROM Disease dis
            LEFT JOIN Doctor doc
                ON dis.department.id = doc.department.id
            WHERE dis.id = :diseaseId
                AND doc.id = :doctorId
            """)
    boolean existsDiseaseByDiseaseIdAndDoctorId(Long diseaseId, Long doctorId);

    // проверяем, что нет ни одного обращения в отделении доктора, связанного с этим заболеванием
    @Query("""
            SELECT a FROM Appeal a
            JOIN Disease dis ON a.disease.id = dis.id
            JOIN Department dep ON a.disease.department.id = dep.id
            WHERE dis.id = :diseaseId AND a.isClosed = false 
            """)
    List<Appeal> existsAppealsByDiseaseId(Long diseaseId);

    @Query("""
                SELECT new ru.mis2022.models.dto.disease.DiseaseDto(
                    dis.id,
                    dis.identifier,
                    dis.name,
                    dis.disabled)
                 FROM Disease dis
                    LEFT JOIN Department dep ON dis.department.id = dep.id
                 WHERE dis.department IS NULL
            """)
    List<DiseaseDto> findDiseaseWithoutDepartment();

    @Query("""
                 SELECT dis
                 FROM Disease dis
                    LEFT JOIN Department dep ON dis.department.id = dep.id
                 WHERE dis.department IS null
            """)
    Disease findByIdWithoutDepartment(Long diseaseId);
}
