package sk.stuba.fiit.bikeflow.servicebooking.domain;

import sk.stuba.fiit.bikeflow.common.Cancellable;
import sk.stuba.fiit.bikeflow.common.Notifiable;
import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "service_booking")
public class ServiceBooking implements Cancellable, Notifiable {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String bookingNumber;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String bikeBrand;

    @Column(nullable = false)
    private String bikeModel;

    @Column(nullable = false, length = 1500)
    private String problemDescription;

    @Column(nullable = false)
    private OffsetDateTime preferredFrom;

    @Column(nullable = false)
    private OffsetDateTime preferredTo;

    @Column(nullable = false)
    private OffsetDateTime scheduledAt;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceBookingStatus status;

    @Column(precision = 10, scale = 2)
    private BigDecimal preliminaryPrice;

    private OffsetDateTime estimatedCompletionAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_point_id")
    private Facility servicePoint;

    @Column(length = 1500)
    private String notes;

    public ServiceBooking() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getBookingNumber() { return bookingNumber; }
    public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getBikeBrand() { return bikeBrand; }
    public void setBikeBrand(String bikeBrand) { this.bikeBrand = bikeBrand; }
    public String getBikeModel() { return bikeModel; }
    public void setBikeModel(String bikeModel) { this.bikeModel = bikeModel; }
    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }
    public OffsetDateTime getPreferredFrom() { return preferredFrom; }
    public void setPreferredFrom(OffsetDateTime preferredFrom) { this.preferredFrom = preferredFrom; }
    public OffsetDateTime getPreferredTo() { return preferredTo; }
    public void setPreferredTo(OffsetDateTime preferredTo) { this.preferredTo = preferredTo; }
    public OffsetDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(OffsetDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public ServiceBookingStatus getStatus() { return status; }
    public void setStatus(ServiceBookingStatus status) { this.status = status; }
    public BigDecimal getPreliminaryPrice() { return preliminaryPrice; }
    public void setPreliminaryPrice(BigDecimal preliminaryPrice) { this.preliminaryPrice = preliminaryPrice; }
    public OffsetDateTime getEstimatedCompletionAt() { return estimatedCompletionAt; }
    public void setEstimatedCompletionAt(OffsetDateTime estimatedCompletionAt) { this.estimatedCompletionAt = estimatedCompletionAt; }
    public Facility getServicePoint() { return servicePoint; }
    public void setServicePoint(Facility servicePoint) { this.servicePoint = servicePoint; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public void cancel() { this.status = ServiceBookingStatus.CANCELLED; }

    @Override
    public boolean isCancelled() { return this.status == ServiceBookingStatus.CANCELLED; }

    @Override
    public String getNotificationEmail() { return customerEmail; }

    @Override
    public String getNotificationReference() { return bookingNumber; }
}
