package sk.stuba.fiit.bikeflow.servicebooking.domain;

public enum ServiceBookingStatus {
    SCHEDULED,
    RECEIVED,
    WAITING_FOR_PARTS,
    IN_REPAIR,
    DONE,
    CANCELLED,
    REJECTED,
    NO_SHOW
}
