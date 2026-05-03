package sk.stuba.fiit.bikeflow.bike.repository;

import sk.stuba.fiit.bikeflow.bike.domain.Bike;
import sk.stuba.fiit.bikeflow.bike.domain.BikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BikeRepository extends JpaRepository<Bike, UUID> {

    @Query("""
            select b
            from Bike b
            join fetch b.facility f
            where lower(f.city) = lower(:city)
              and b.status = :status
            order by b.modelName asc
            """)
    List<Bike> findAvailableInCity(String city, BikeStatus status);

    Optional<Bike> findByCode(String code);
}
