package sk.stuba.fiit.bikeflow.sparepart.repository;

import sk.stuba.fiit.bikeflow.sparepart.domain.OrderSparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OrderSparePartRepository extends JpaRepository<OrderSparePart, UUID> {
    List<OrderSparePart> findByServiceBookingId(UUID serviceBookingId);
}
