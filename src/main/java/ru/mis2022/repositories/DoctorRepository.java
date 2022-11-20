package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mis2022.models.dto.doctor.CurrentChiefReportDto;
import ru.mis2022.models.dto.doctor.CurrentDoctorDto;
import ru.mis2022.models.dto.doctor.DoctorDto;
import ru.mis2022.models.entity.Doctor;

import java.time.LocalDateTime;
import java.util.List;


public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("""
        SELECT new ru.mis2022.models.dto.doctor.CurrentDoctorDto(
            p.firstName,
            p.lastName,
            p.birthday,
            r.name,
            d.name)
        FROM Doctor p
            JOIN Role r ON p.role.id = r.id
            JOIN Department d ON p.department.id = d.id
        WHERE p.email = :email
        """)
    CurrentDoctorDto getCurrentDoctorDtoByEmail(String email);

    Doctor findByEmail(String email);

    @Query("""
    SELECT new ru.mis2022.models.dto.doctor.DoctorDto(
        d.id,
        d.email,
        d.password,
        d.firstName,
        d.lastName,
        d.surname,
        CAST(d.birthday as string),
        d.role.name,
        d.department.name
    )
    FROM
        Doctor d
    WHERE
        d.department.id = :deptId
    
    """)
    List<DoctorDto> findAllByDepartmentIdDto(Long deptId);

    //Отчет заведующего отделения по загруженности докторов его департамента
    @Query("""
    SELECT new ru.mis2022.models.dto.doctor.CurrentChiefReportDto(
        d.id,
        CONCAT(d.firstName, ' ', d.lastName, ' ', d.surname),
        TO_CHAR(t.time,'YYYY-MM-DD'),
        SUM(
            CASE
                WHEN (t.patient is not null) THEN 1
                ELSE 0
            END
            ),
        SUM(
            CASE
                WHEN (t.id is not null) THEN 1
                ELSE 0
            END
            )
    )
    FROM  Doctor d
    LEFT JOIN Talon t
        on t.doctor.id = d.id
        AND t.time BETWEEN :dateHome AND :DateEnd
    WHERE
        d.department.id = :deptId
    GROUP BY
        d.id,
        d.firstName,
        TO_CHAR(t.time,'YYYY-MM-DD')
    ORDER BY
        d.id,
        TO_CHAR(t.time,'YYYY-MM-DD')
""")
    List<CurrentChiefReportDto> getWorkloadEmployeesReport(@Param("deptId") Long deptId, @Param("dateHome") LocalDateTime dateHome, @Param("DateEnd") LocalDateTime DateEnd);

    @Query("""
    SELECT d
    FROM Doctor d
        JOIN Department dep ON dep.id = d.department.id
    WHERE d.id = :doctorId AND d.department.id = :departmentId
    """)
    Doctor findByIdAndDepartment(long doctorId,
                                 long departmentId);

    @Query("""
    SELECT COUNT (d.id) FROM Doctor d
    WHERE d.department.id = :departmentId AND d.role.name LIKE ('CHIEF_DOCTOR')
    """)
    int findByRoleForChief(@Param("departmentId") long departmentId);

    @Query("""
    SELECT d
    FROM Doctor d
        JOIN Department dep ON dep.id = d.department.id
    WHERE d.id = :doctorId
    """)
    Doctor findDoctorById(long doctorId);

    @Query("""
    SELECT COUNT (d.id) FROM Doctor d
    WHERE d.role.name LIKE ('MAIN_DOCTOR')
    """)
    int findByRoleForMain();

    @Query("""
    SELECT d
    FROM Doctor d
    JOIN PersonalHistory p ON p.id = d.personalHistory.id
    JOIN FETCH Vacation v ON v.id = d.personalHistory.id
    WHERE v.personalHistory.id = d.personalHistory.id
    """)
    List<Doctor> findAllWhoNeedVacation();

}
