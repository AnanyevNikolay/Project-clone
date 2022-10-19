package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mis2022.models.dto.administrator.CurrentAdministratorDto;
import ru.mis2022.models.entity.Administrator;


@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

    @Query("""
        SELECT new ru.mis2022.models.dto.administrator.CurrentAdministratorDto(
            p.firstName,
            p.lastName,
            p.birthday,
            r.name)
        FROM Administrator p
            JOIN Role r ON p.role.id = r.id
        WHERE p.email = :email
        """)
    CurrentAdministratorDto getCurrentAdministratorDtoByEmail(String email);

    Administrator findByEmail(String email);
}
