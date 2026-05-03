package sk.stuba.fiit.bikeflow.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.OffsetDateTime;

@Embeddable
public class DateRange {

    @Column(name = "preferred_from", nullable = false)
    private OffsetDateTime from;

    @Column(name = "preferred_to", nullable = false)
    private OffsetDateTime to;

    public DateRange() {}

    public DateRange(OffsetDateTime from, OffsetDateTime to) {
        if (from == null || to == null) throw new IllegalArgumentException("DateRange bounds must not be null.");
        if (!to.isAfter(from)) throw new IllegalArgumentException("DateRange end must be after start.");
        this.from = from;
        this.to = to;
    }

    public OffsetDateTime getFrom() { return from; }
    public OffsetDateTime getTo() { return to; }

    public boolean contains(OffsetDateTime moment) {
        return !moment.isBefore(from) && !moment.isAfter(to);
    }
}
