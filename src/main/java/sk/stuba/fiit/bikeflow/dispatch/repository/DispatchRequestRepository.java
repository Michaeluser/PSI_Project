package sk.stuba.fiit.bikeflow.dispatch.repository;

import sk.stuba.fiit.bikeflow.dispatch.domain.DispatchRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DispatchRequestRepository extends JpaRepository<DispatchRequest, UUID> {
}
