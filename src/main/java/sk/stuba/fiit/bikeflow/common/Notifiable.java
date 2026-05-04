package sk.stuba.fiit.bikeflow.common;

/**
 * Marks a domain object that carries enough contact information
 * for the NotificationService to deliver confirmations or status updates.
 * Corresponds to the Notifiable interface defined in the Common package
 * of the F4 class diagrams (BCE architecture).
 */
public interface Notifiable {
    String getEmail();
    String getPhone();
}
