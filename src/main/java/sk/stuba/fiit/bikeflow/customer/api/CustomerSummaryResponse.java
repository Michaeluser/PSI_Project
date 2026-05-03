package sk.stuba.fiit.bikeflow.customer.api;

import java.math.BigDecimal;
import java.util.UUID;

public record CustomerSummaryResponse(
        UUID id,
        String fullName,
        String email,
        BigDecimal creditBalance,
        boolean verifiedPaymentCard) {
}
