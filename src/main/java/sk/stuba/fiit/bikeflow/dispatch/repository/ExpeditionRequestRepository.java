package sk.stuba.fiit.bikeflow.dispatch.repository;

import sk.stuba.fiit.bikeflow.dispatch.domain.ExpeditionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExpeditionRequestRepository extends JpaRepository<ExpeditionRequest, UUID> {
}
