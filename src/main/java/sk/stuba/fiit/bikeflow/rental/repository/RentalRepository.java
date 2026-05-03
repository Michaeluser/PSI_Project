package sk.stuba.fiit.bikeflow.rental.repository;

import sk.stuba.fiit.bikeflow.rental.domain.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RentalRepository extends JpaRepository<Rental, UUID> {
}
