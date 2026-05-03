package sk.stuba.fiit.bikeflow.inventory.repository;

import sk.stuba.fiit.bikeflow.inventory.domain.SaleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SaleRecordRepository extends JpaRepository<SaleRecord, UUID> {

    @Query("""
            select s
            from SaleRecord s
            join fetch s.product p
            where s.facility.id = :facilityId
              and s.salesDate between :fromDate and :toDate
            order by p.name asc, s.salesDate desc
            """)
    List<SaleRecord> findByFacilityAndPeriod(UUID facilityId, LocalDate fromDate, LocalDate toDate);
}
