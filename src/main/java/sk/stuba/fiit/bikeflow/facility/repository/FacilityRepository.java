package sk.stuba.fiit.bikeflow.facility.repository;

import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FacilityRepository extends JpaRepository<Facility, UUID> {

    List<Facility> findByCityIgnoreCaseOrderByNameAsc(String city);
}
