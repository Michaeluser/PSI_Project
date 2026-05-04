package sk.stuba.fiit.bikeflow.servicebooking.domain;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Employee responsible for performing bike service repairs.
 * Corresponds to the ServiceWorker class in the F4 Service Class Diagram.
 */
@Entity
@Table(name = "service_worker")
public class ServiceWorker {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_point_id")
    private sk.stuba.fiit.bikeflow.facility.domain.Facility servicePoint;

    public ServiceWorker() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public sk.stuba.fiit.bikeflow.facility.domain.Facility getServicePoint() { return servicePoint; }
    public void setServicePoint(sk.stuba.fiit.bikeflow.facility.domain.Facility servicePoint) { this.servicePoint = servicePoint; }
}
