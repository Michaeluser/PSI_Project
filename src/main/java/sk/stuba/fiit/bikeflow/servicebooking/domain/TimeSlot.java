package sk.stuba.fiit.bikeflow.servicebooking.domain;

import java.time.OffsetDateTime;

/**
 * Represents a single one-hour appointment slot in the service calendar.
 * Corresponds to the TimeSlot class in the F4 Service Class Diagram.
 */
public class TimeSlot {

    private final OffsetDateTime start;

    public TimeSlot(OffsetDateTime start) {
        this.start = start.withMinute(0).withSecond(0).withNano(0);
    }

    public OffsetDateTime getStart() { return start; }

    public TimeSlot next() {
        return new TimeSlot(start.plusHours(1));
    }

    public boolean isAfter(OffsetDateTime bound) {
        return start.isAfter(bound);
    }
}
