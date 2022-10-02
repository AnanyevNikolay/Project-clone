package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mis2022.models.dto.yet.YetDto;
import ru.mis2022.models.entity.Yet;

import java.time.LocalDate;
import java.util.List;

public interface YetRepository extends JpaRepository<Yet, Long> {

    @Query("""
                select y from Yet y where y.dayFrom <= :dayTo and y.dayTo >= :dayFrom 
            """)
    List<Yet> existYetDayFromDayTo(LocalDate dayFrom, LocalDate dayTo);

    @Query("""
                select y from Yet y where y.dayFrom <= :dayTo and y.dayTo >= :dayFrom and y.id <> :id
            """)
    List<Yet> existYetDayFromDayToExceptCurrentId(Long id, LocalDate dayFrom, LocalDate dayTo);

    @Query("""
                select y from Yet y where y.id = :id
            """)
    Yet existById(Long id);

    @Query("""
    SELECT new ru.mis2022.models.dto.yet.YetDto(
        y.id,
        y.price,
        y.dayFrom ,
         y.dayTo 
    )
    FROM Yet y
    """)
    List<YetDto> findAllYetDto();

    @Query("""
            SELECT y
            FROM Yet y
            WHERE y.dayFrom >= :dayFrom and y.dayTo <= :dayTo
            """)
    List<Yet> findAllYetsBetweenDayFromAndDayTo(LocalDate dayFrom, LocalDate dayTo);

    void deleteAll();
}
