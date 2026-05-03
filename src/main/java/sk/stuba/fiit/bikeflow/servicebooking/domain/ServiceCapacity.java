package sk.stuba.fiit.bikeflow.servicebooking.domain;

/**
 * Encapsulates the maximum number of concurrent service bookings per time slot.
 * Corresponds to the ServiceCapacity class in the F4 Service Class Diagram.
 */
public class ServiceCapacity {

    private final int maxSlots;

    public ServiceCapacity(int maxSlots) {
        if (maxSlots < 1) throw new IllegalArgumentException("ServiceCapacity must be at least 1.");
        this.maxSlots = maxSlots;
    }

    public int getMaxSlots() { return maxSlots; }

    public boolean isFull(long activeCount) {
        return activeCount >= maxSlots;
    }
}
