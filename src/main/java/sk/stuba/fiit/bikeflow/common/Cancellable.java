package sk.stuba.fiit.bikeflow.common;

/**
 * Marks a domain object whose lifecycle can be explicitly cancelled.
 * Corresponds to the Cancellable interface defined in the Common package
 * of the F4 class diagrams (BCE architecture).
 */
public interface Cancellable {
    void cancel();
    boolean isCancelled();
}
