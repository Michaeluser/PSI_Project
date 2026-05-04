package sk.stuba.fiit.bikeflow.dispatch.repository;

import sk.stuba.fiit.bikeflow.dispatch.domain.WarehouseStaff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WarehouseStaffRepository extends JpaRepository<WarehouseStaff, UUID> {
    List<WarehouseStaff> findByWarehouseId(UUID warehouseId);
}
