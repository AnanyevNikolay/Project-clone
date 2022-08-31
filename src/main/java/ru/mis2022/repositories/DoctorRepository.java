package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mis2022.models.dto.doctor.CurrentChiefReportDto;
import ru.mis2022.models.dto.doctor.CurrentDoctorDto;
import ru.mis2022.models.dto.doctor.DoctorDto;
import ru.mis2022.models.entity.Department;
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
        SELECT new ru.mis2022.models.dto.doctor.CurrentDoctorDto(
            doc.firstName,
            doc.lastName,
            doc.birthday,
            role.name,
            dep.name)
        FROM Doctor doc
            JOIN Role role ON doc.role.id = role.id
            JOIN Department dep ON doc.department.id = dep.id
        WHERE doc.email = :email
        AND doc.department = :department
        """)
        //todo имя метода неправильное - отобразить получение коллекции
    List<CurrentDoctorDto> findDoctorDtoByDepartment(Department department);

    //todo надо искать по ид департамента и необходимо писать запрос с джойнами
    List<Doctor> findByDepartment (Department department);

    //todo дубль удалить
    List<Doctor> findAllByDepartmentId(Long id);

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

    @Query("""
            SELECT d FROM Doctor d WHERE d.id = :id
            """)
    Doctor findDoctorById(Long id);

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
    SELECT d.department.id
    FROM  Doctor d
    WHERE d.id = :docId
    """)
    Long getDepartmentIdByDoctorId(@Param("docId") Long docId);

}
