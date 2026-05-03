package sk.stuba.fiit.bikeflow.rental.repository;

import sk.stuba.fiit.bikeflow.rental.domain.RentalIssueReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RentalIssueReportRepository extends JpaRepository<RentalIssueReport, UUID> {
}
