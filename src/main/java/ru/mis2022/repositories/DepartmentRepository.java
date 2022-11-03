package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mis2022.models.dto.department.DepartmentDto;
import ru.mis2022.models.entity.Department;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

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

    // todo подтягивать id департамента, используая таблицу Department
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
    SELECT dep
    FROM Department dep
    LEFT JOIN Doctor doc ON dep.id = doc.department.id
    WHERE doc.id = :docId
    """)
    Department findDepartmentByDoctorId(@Param("docId") Long docId);

    @Query("""
            SELECT d
            FROM Department d
                JOIN Doctor doc ON doc.department.id = d.id
                JOIN Talon t ON t.doctor.id = doc.id
                JOIN Disease dis ON dis.department.id = d.id
                JOIN Appeal a ON a.disease.id = dis.id
            WHERE t.id = :talonId AND a.id = :appealId
            """)
    Department isExistByTalonIdAndAppealId(Long talonId, Long appealId);
}
