package sk.stuba.fiit.bikeflow.sparepart.repository;

import sk.stuba.fiit.bikeflow.sparepart.domain.PartOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PartOrderRepository extends JpaRepository<PartOrder, UUID> {
    List<PartOrder> findByServiceBookingId(UUID serviceBookingId);
}
