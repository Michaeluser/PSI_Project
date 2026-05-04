package sk.stuba.fiit.bikeflow.rental.repository;

import sk.stuba.fiit.bikeflow.rental.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    boolean existsByRentalId(UUID rentalId);
}
