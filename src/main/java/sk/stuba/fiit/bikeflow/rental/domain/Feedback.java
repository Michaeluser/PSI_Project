package sk.stuba.fiit.bikeflow.rental.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Customer satisfaction record captured after a completed rental (UC04, step 12).
 * Corresponds to the Feedback class in the F4 Rental Class Diagram.
 * A rental can have at most one Feedback entry (1..0..1 relationship).
 */
@Entity
@Table(name = "rental_feedback")
public class Feedback {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rental_id", unique = true)
    private Rental rental;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private OffsetDateTime submittedAt;

    public Feedback() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Rental getRental() { return rental; }
    public void setRental(Rental rental) { this.rental = rental; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public OffsetDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(OffsetDateTime submittedAt) { this.submittedAt = submittedAt; }
}
