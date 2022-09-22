package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mis2022.models.dto.user.UserDto;
import ru.mis2022.models.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    User findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndId(String name, Long id);

    @Query("""
            SELECT u FROM User u JOIN FETCH u.role WHERE (u.email = :email AND u.id <> :id)
            """)
    User findByEmailAndExceptCurrentId(String email, Long id);

    @Query("""
            SELECT u FROM User u
                JOIN FETCH u.role
            WHERE u.role.name <> :roleName
                AND LOWER(CONCAT(u.lastName,' ',u.firstName))
                LIKE LOWER(CONCAT('%',:fullName,'%'))
            ORDER BY u.lastName, u.firstName, u.id
            """)
    List<User> findPersonalByFullName(String fullName, String roleName);

    @Query("""
    SELECT DISTINCT new ru.mis2022.models.dto.user.UserDto(
        d.id,
        d.firstName,
        d.lastName,
        d.surname,
        CAST(d.birthday as string),
        d.email,
        d.role.name)
    FROM User u, Doctor d
    JOIN u.role
    WHERE 
    d.department.id = :depId AND
    d.role.id = u.role.id 
    ORDER BY d.id 
    """)
    List<UserDto> findDoctorsByDepartment(Long depId);

    @Query("""
        SELECT new ru.mis2022.models.dto.user.UserDto(
        u.id,
        u.firstName,
        u.lastName,
        u.surname,
        CAST(u.birthday as string),
        u.email,
        u.role.name)
        FROM User u
            JOIN u.role
        WHERE u.role.name <> 'DOCTOR' AND
              u.role.name <> 'CHIEF_DOCTOR' AND
              u.role.name <> 'MAIN_DOCTOR' AND
              u.role.name <> 'PATIENT'
        ORDER BY u.id
        """)
    List<UserDto> findStaffByDepartment(Long depId);

    User findUserById(Long id);
    User save(User user);
}
