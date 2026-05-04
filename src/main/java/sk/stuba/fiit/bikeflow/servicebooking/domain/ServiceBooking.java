package sk.stuba.fiit.bikeflow.servicebooking.domain;

import sk.stuba.fiit.bikeflow.common.Cancellable;
import sk.stuba.fiit.bikeflow.common.DateRange;
import sk.stuba.fiit.bikeflow.common.Notifiable;
import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Embedded
    private DateRange preferredWindow;

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

    private OffsetDateTime receivedAt;

    @Column(length = 1500)
    private String technicalState;

    @Column(length = 1500)
    private String additionalFindings;

    private OffsetDateTime clientApprovedAt;

    private OffsetDateTime completedAt;

    private OffsetDateTime clientNotifiedAt;

    private int loyaltyDiscountPercent;

    @Column(precision = 10, scale = 2)
    private BigDecimal loyaltyDiscountAmount;

    @Column(length = 1500)
    private String partsOrderSummary;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_point_id")
    private Facility servicePoint;

    @Column(length = 1500)
    private String notes;

    @OneToMany(mappedBy = "serviceBooking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceWorkItem> workItems = new ArrayList<>();

    @OneToMany(mappedBy = "serviceBooking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceRequiredPart> requiredParts = new ArrayList<>();

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
    public DateRange getPreferredWindow() { return preferredWindow; }
    public void setPreferredWindow(DateRange preferredWindow) { this.preferredWindow = preferredWindow; }
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
    public OffsetDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(OffsetDateTime receivedAt) { this.receivedAt = receivedAt; }
    public String getTechnicalState() { return technicalState; }
    public void setTechnicalState(String technicalState) { this.technicalState = technicalState; }
    public String getAdditionalFindings() { return additionalFindings; }
    public void setAdditionalFindings(String additionalFindings) { this.additionalFindings = additionalFindings; }
    public OffsetDateTime getClientApprovedAt() { return clientApprovedAt; }
    public void setClientApprovedAt(OffsetDateTime clientApprovedAt) { this.clientApprovedAt = clientApprovedAt; }
    public OffsetDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(OffsetDateTime completedAt) { this.completedAt = completedAt; }
    public OffsetDateTime getClientNotifiedAt() { return clientNotifiedAt; }
    public void setClientNotifiedAt(OffsetDateTime clientNotifiedAt) { this.clientNotifiedAt = clientNotifiedAt; }
    public int getLoyaltyDiscountPercent() { return loyaltyDiscountPercent; }
    public void setLoyaltyDiscountPercent(int loyaltyDiscountPercent) { this.loyaltyDiscountPercent = loyaltyDiscountPercent; }
    public BigDecimal getLoyaltyDiscountAmount() { return loyaltyDiscountAmount; }
    public void setLoyaltyDiscountAmount(BigDecimal loyaltyDiscountAmount) { this.loyaltyDiscountAmount = loyaltyDiscountAmount; }
    public String getPartsOrderSummary() { return partsOrderSummary; }
    public void setPartsOrderSummary(String partsOrderSummary) { this.partsOrderSummary = partsOrderSummary; }
    public Facility getServicePoint() { return servicePoint; }
    public void setServicePoint(Facility servicePoint) { this.servicePoint = servicePoint; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public void cancel() { this.status = ServiceBookingStatus.CANCELLED; }

    @Override
    public boolean isCancelled() { return this.status == ServiceBookingStatus.CANCELLED; }

    @Override
    public String getEmail() { return customerEmail; }

    @Override
    public String getPhone() { return bookingNumber; }
    public List<ServiceWorkItem> getWorkItems() { return workItems; }
    public List<ServiceRequiredPart> getRequiredParts() { return requiredParts; }
}