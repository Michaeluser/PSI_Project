package sk.stuba.fiit.bikeflow.rental.domain;

import sk.stuba.fiit.bikeflow.bike.domain.Bike;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "rental_issue_report")
public class RentalIssueReport {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rental_id")
    private Rental rental;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bike_id")
    private Bike bike;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalIssueType issueType;

    @Column(nullable = false, length = 1500)
    private String description;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    public RentalIssueReport() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Rental getRental() { return rental; }
    public void setRental(Rental rental) { this.rental = rental; }
    public Bike getBike() { return bike; }
    public void setBike(Bike bike) { this.bike = bike; }
    public RentalIssueType getIssueType() { return issueType; }
    public void setIssueType(RentalIssueType issueType) { this.issueType = issueType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
