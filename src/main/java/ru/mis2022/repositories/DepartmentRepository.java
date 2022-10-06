package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mis2022.models.dto.department.DepartmentDto;
import ru.mis2022.models.entity.Department;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findAllByMedicalOrganizationId(Long id);

    @Query("""
            SELECT d FROM Department d WHERE d.id = :id
            """)
    Department findDepartmentById(Long id);

    @Query("""
    SELECT new ru.mis2022.models.dto.department.DepartmentDto(
        d.id,
        d.name
    )
    FROM
        Department d
    WHERE
        d.medicalOrganization.id = :medId
    """)
    List<DepartmentDto> findAllByMedicalOrganizationIdDto(Long medId);

    @Query("""
    SELECT d.department.id
    FROM  Doctor d
    WHERE d.id = :docId
    """)
    Long getDepartmentIdByDoctorId(@Param("docId") Long docId);

    @Query("""
    SELECT new ru.mis2022.models.dto.department.DepartmentDto(
        d.id,
        d.name
    )
    FROM Department d
    """)
    List<DepartmentDto> getAllDepartments();

    @Query("""
    SELECT d.department
    FROM  Doctor d
    WHERE d.id = :docId
    """)
    Department findDepartmentByDoctorId(@Param("docId") Long docId);
}
