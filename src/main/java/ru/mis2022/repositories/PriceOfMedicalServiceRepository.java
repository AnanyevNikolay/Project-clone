package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mis2022.models.entity.PriceOfMedicalService;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PriceOfMedicalServiceRepository extends JpaRepository<PriceOfMedicalService, Long> {
    @Query("""
            SELECT pof
            FROM PriceOfMedicalService pof
            WHERE pof.dayFrom >= :dayFrom and pof.dayTo <= :dayTo
            """)
    List<PriceOfMedicalService> findAllPricesBetweenDayFromAndDayTo(LocalDate dayFrom, LocalDate dayTo);

    @Query("""
            SELECT pof
            FROM PriceOfMedicalService pof
                left join pof.medicalService ms on ms.id = pof.medicalService.id
            WHERE pof.dayFrom >= :dayFrom and pof.dayTo <= :dayTo and ms.id = :medicalServiceId
            """)
    PriceOfMedicalService getPriceOfMedicalServiceBetweenDayFromAndDayToWithMedicalService(
            LocalDate dayFrom, LocalDate dayTo, Long medicalServiceId);

}
