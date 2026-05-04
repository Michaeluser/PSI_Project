package sk.stuba.fiit.bikeflow.sparepart.repository;

import sk.stuba.fiit.bikeflow.sparepart.domain.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SparePartRepository extends JpaRepository<SparePart, UUID> {}
