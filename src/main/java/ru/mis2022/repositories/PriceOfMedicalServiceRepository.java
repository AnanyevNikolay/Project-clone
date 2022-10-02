package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mis2022.models.entity.PriceOfMedicalService;

import java.time.LocalDate;
import java.util.List;

public interface PriceOfMedicalServiceRepository extends JpaRepository<PriceOfMedicalService, Long> {
    @Query("""
            SELECT pof
            FROM PriceOfMedicalService pof
            WHERE pof.dayFrom >= :dayFrom and pof.dayTo <= :dayTo
            """)
    List<PriceOfMedicalService> findAllPricesBetweenDayFromAndDayTo(LocalDate dayFrom, LocalDate dayTo);

}
