package sk.stuba.fiit.bikeflow.servicebooking.repository;

import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBooking;
import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, UUID> {

    @Query("""
            select count(sb)
            from ServiceBooking sb
            where sb.scheduledAt = :slot
              and sb.status in :activeStatuses
            """)
    long countActiveBySlot(OffsetDateTime slot, List<ServiceBookingStatus> activeStatuses);

    Optional<ServiceBooking> findByBookingNumber(String bookingNumber);
}
