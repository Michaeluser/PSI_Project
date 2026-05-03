package sk.stuba.fiit.bikeflow.rental.application;

import sk.stuba.fiit.bikeflow.bike.domain.Bike;
import sk.stuba.fiit.bikeflow.bike.domain.BikeStatus;
import sk.stuba.fiit.bikeflow.bike.repository.BikeRepository;
import sk.stuba.fiit.bikeflow.common.exception.BusinessRuleException;
import sk.stuba.fiit.bikeflow.common.exception.NotFoundException;
import sk.stuba.fiit.bikeflow.customer.domain.CustomerAccount;
import sk.stuba.fiit.bikeflow.customer.repository.CustomerAccountRepository;
import sk.stuba.fiit.bikeflow.rental.api.FeedbackResponse;
import sk.stuba.fiit.bikeflow.rental.api.PreRegisterRentalRequest;
import sk.stuba.fiit.bikeflow.rental.api.RentalResponse;
import sk.stuba.fiit.bikeflow.rental.api.ReportRentalIssueRequest;
import sk.stuba.fiit.bikeflow.rental.api.SubmitFeedbackRequest;
import sk.stuba.fiit.bikeflow.rental.domain.Feedback;
import sk.stuba.fiit.bikeflow.rental.domain.Rental;
import sk.stuba.fiit.bikeflow.rental.domain.RentalIssueReport;
import sk.stuba.fiit.bikeflow.rental.domain.RentalStatus;
import sk.stuba.fiit.bikeflow.rental.repository.FeedbackRepository;
import sk.stuba.fiit.bikeflow.rental.repository.RentalIssueReportRepository;
import sk.stuba.fiit.bikeflow.rental.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RentalService {

    private final RentalRepository rentalRepository;
    private final RentalIssueReportRepository rentalIssueReportRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final BikeRepository bikeRepository;
    private final FeedbackRepository feedbackRepository;

    public RentalService(
            RentalRepository rentalRepository,
            RentalIssueReportRepository rentalIssueReportRepository,
            CustomerAccountRepository customerAccountRepository,
            BikeRepository bikeRepository,
            FeedbackRepository feedbackRepository) {
        this.rentalRepository = rentalRepository;
        this.rentalIssueReportRepository = rentalIssueReportRepository;
        this.customerAccountRepository = customerAccountRepository;
        this.bikeRepository = bikeRepository;
        this.feedbackRepository = feedbackRepository;
    }

    public List<RentalResponse> getAll() {
        return rentalRepository.findAll().stream().map(this::toResponse).toList();
    }

    public RentalResponse preRegister(PreRegisterRentalRequest request) {
        CustomerAccount customer = customerAccountRepository.findById(request.customerId())
                .orElseThrow(() -> new NotFoundException("Customer was not found."));
        Bike bike = bikeRepository.findById(request.bikeId())
                .orElseThrow(() -> new NotFoundException("Bike was not found."));

        ensureCustomerCanRent(customer);
        if (bike.getStatus() != BikeStatus.AVAILABLE) {
            throw new BusinessRuleException("Bike is not available for preliminary registration.");
        }

        BigDecimal estimatedPrice = bike.getPricePerMinute().multiply(BigDecimal.valueOf(request.plannedMinutes()));
        if (customer.getCreditBalance().compareTo(estimatedPrice) < 0) {
            throw new BusinessRuleException("Customer does not have enough credits for the estimated rental price.");
        }

        bike.setStatus(BikeStatus.PRE_RESERVED);
        bike.setReservedByCustomer(customer);

        Rental rental = new Rental();
        rental.setId(UUID.randomUUID());
        rental.setRentalNumber("RT-" + System.currentTimeMillis());
        rental.setCustomer(customer);
        rental.setBike(bike);
        rental.setStatus(RentalStatus.PRE_REGISTERED);
        rental.setPlannedMinutes(request.plannedMinutes());
        rental.setEstimatedPrice(estimatedPrice);
        rental.setCreatedAt(OffsetDateTime.now());

        bikeRepository.save(bike);
        return toResponse(rentalRepository.save(rental));
    }

    public RentalResponse startRental(UUID rentalId) {
        Rental rental = getRental(rentalId);
        if (rental.getStatus() != RentalStatus.PRE_REGISTERED) {
            throw new BusinessRuleException("Only preliminary registrations can be started.");
        }

        rental.setStatus(RentalStatus.ACTIVE);
        rental.setStartedAt(OffsetDateTime.now());

        Bike bike = rental.getBike();
        bike.setStatus(BikeStatus.RENTED);
        bikeRepository.save(bike);

        return toResponse(rentalRepository.save(rental));
    }

    public RentalResponse finishRental(UUID rentalId) {
        Rental rental = getRental(rentalId);
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new BusinessRuleException("Only active rentals can be finished.");
        }

        rental.setStatus(RentalStatus.FINISHED);
        rental.setEndedAt(OffsetDateTime.now());
        rental.setFinalPrice(rental.getEstimatedPrice());

        CustomerAccount customer = rental.getCustomer();
        customer.setCreditBalance(customer.getCreditBalance().subtract(rental.getEstimatedPrice()));
        customerAccountRepository.save(customer);

        Bike bike = rental.getBike();
        bike.setStatus(BikeStatus.AVAILABLE);
        bike.setReservedByCustomer(null);
        bikeRepository.save(bike);

        return toResponse(rentalRepository.save(rental));
    }

    public RentalResponse reportIssue(UUID rentalId, ReportRentalIssueRequest request) {
        Rental rental = getRental(rentalId);

        Bike bike = rental.getBike();
        bike.setStatus(switch (request.issueType()) {
            case DAMAGED -> BikeStatus.DAMAGED;
            case MISSING -> BikeStatus.MISSING;
        });
        bike.setReservedByCustomer(null);
        bikeRepository.save(bike);

        rental.setStatus(RentalStatus.ISSUE_REPORTED);
        rentalRepository.save(rental);

        RentalIssueReport issueReport = new RentalIssueReport();
        issueReport.setId(UUID.randomUUID());
        issueReport.setRental(rental);
        issueReport.setBike(bike);
        issueReport.setIssueType(request.issueType());
        issueReport.setDescription(request.description());
        issueReport.setCreatedAt(OffsetDateTime.now());
        rentalIssueReportRepository.save(issueReport);

        return toResponse(rental);
    }

    public FeedbackResponse submitFeedback(UUID rentalId, SubmitFeedbackRequest request) {
        Rental rental = getRental(rentalId);
        if (rental.getStatus() != RentalStatus.FINISHED) {
            throw new BusinessRuleException("Feedback can only be submitted for finished rentals.");
        }
        if (feedbackRepository.existsByRentalId(rentalId)) {
            throw new BusinessRuleException("Feedback has already been submitted for this rental.");
        }

        Feedback feedback = new Feedback();
        feedback.setId(UUID.randomUUID());
        feedback.setRental(rental);
        feedback.setRating(request.rating());
        feedback.setComment(request.comment());
        feedback.setSubmittedAt(OffsetDateTime.now());
        feedbackRepository.save(feedback);

        return new FeedbackResponse(
                feedback.getId(),
                rentalId,
                feedback.getRating(),
                feedback.getComment(),
                feedback.getSubmittedAt()
        );
    }

    private Rental getRental(UUID rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new NotFoundException("Rental was not found."));
    }

    private void ensureCustomerCanRent(CustomerAccount customer) {
        if (!customer.isActive()) {
            throw new BusinessRuleException("Inactive customer cannot rent a bike.");
        }
        if (!customer.isVerifiedPaymentCard()) {
            throw new BusinessRuleException("Customer must have a verified payment card.");
        }
    }

    private RentalResponse toResponse(Rental rental) {
        return new RentalResponse(
                rental.getId(),
                rental.getRentalNumber(),
                rental.getCustomer().getId(),
                rental.getCustomer().getFullName(),
                rental.getBike().getId(),
                rental.getBike().getCode(),
                rental.getBike().getModelName(),
                rental.getStatus(),
                rental.getPlannedMinutes(),
                rental.getEstimatedPrice(),
                rental.getFinalPrice(),
                rental.getCreatedAt(),
                rental.getStartedAt(),
                rental.getEndedAt()
        );
    }
}
