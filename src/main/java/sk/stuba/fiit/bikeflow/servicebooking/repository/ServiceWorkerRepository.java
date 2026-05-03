package sk.stuba.fiit.bikeflow.servicebooking.repository;

import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceWorker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ServiceWorkerRepository extends JpaRepository<ServiceWorker, UUID> {
    List<ServiceWorker> findByServicePointId(UUID servicePointId);
}
