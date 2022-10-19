package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mis2022.models.dto.organization.MedicalOrganizationDto;
import ru.mis2022.models.entity.MedicalOrganization;

import java.util.List;


public interface MedicalOrganizationRepository extends JpaRepository<MedicalOrganization, Long> {

    @Query("""
    SELECT  new  ru.mis2022.models.dto.organization.MedicalOrganizationDto(
    m.id,
    m.name,
    m.address
    )
    FROM 
        MedicalOrganization m
    """)
    List<MedicalOrganizationDto> findAllDto();

    boolean existsByName(String name);
}
