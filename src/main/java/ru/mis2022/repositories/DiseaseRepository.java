package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mis2022.models.dto.disease.DiseaseDto;
import ru.mis2022.models.entity.Disease;

import java.util.List;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Long> {

    @Query("""
            SELECT new ru.mis2022.models.dto.disease.DiseaseDto(
                d.id,
                d.identifier,
                d.name)
            FROM Disease d
            """)
    List<DiseaseDto> findAllDiseaseDto();

    boolean existsByIdentifier(String identifier);


    @Query("""
            select new  ru.mis2022.models.dto.disease.DiseaseDto(
            dis.id,
            dis.identifier,
            dis.name
            )
            from Disease dis
            join Department dep on dep.id = dis.department.id
            join Doctor doc on dep.id = doc.department.id
            where doc.id=:docId
            """)
    List<DiseaseDto> findDiseaseByDepartmentDoctors(@Param("docId") Long docId);
    Disease findDiseaseById(Long id);

}
