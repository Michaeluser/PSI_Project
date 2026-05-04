package sk.stuba.fiit.bikeflow.sparepart.api;

import sk.stuba.fiit.bikeflow.sparepart.domain.PartOrderStatus;
import java.time.LocalDate;
import java.util.UUID;

public record AddSparePartResponse(
        UUID orderSparePartId,
        UUID sparePartId,
        String sparePartName,
        int quantity,
        boolean partOrderCreated,
        UUID partOrderId,
        LocalDate estimatedDelivery,
        PartOrderStatus partOrderStatus
) {}
