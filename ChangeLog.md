# BikeFlow – ChangeLog

> Projekt: PSI_Project (FIIT STU, PSI 2025/26, Fáza 5)  
> Autor zmien: Andrej Mikloš  
> Obdobie: 2026-05-04

---

## [F5] Implementácia kódu pre Fázu 5

### Common package

| Súbor | Zmena |
|---|---|
| `common/Cancellable.java` | **Nový** – rozhranie z F4 Common: `cancel()`, `isCancelled()` |
| `common/Notifiable.java` | **Nový** – rozhranie z F4 Common: `getEmail()`, `getPhone()` |
| `common/NotificationService.java` | **Nový** – cross-cutting služba; notifikácie cez SLF4J log (nie SMTP/SMS) |
| `common/DateRange.java` | **Nový** – `@Embeddable` hodnotový objekt z F4 Common; `from`/`to` + `contains()` |

---

### ServiceBooking doména (UC01–UC02)

| Súbor | Zmena |
|---|---|
| `servicebooking/domain/ServiceBooking.java` | `implements Cancellable, Notifiable`; `preferredFrom`/`preferredTo` nahradené `@Embedded DateRange preferredWindow`; `getEmail()`, `getPhone()` |
| `servicebooking/domain/TimeSlot.java` | **Nový** – doménová trieda z EA; obaľuje `OffsetDateTime`; `next()`, `isAfter()` |
| `servicebooking/domain/ServiceCapacity.java` | **Nový** – doménová trieda z EA; `maxSlots`, `isFull(activeCount)` |
| `servicebooking/domain/ServiceWorker.java` | **Nový** – entita z EA; `fullName`, `email`, väzba na `Facility` |
| `servicebooking/repository/ServiceWorkerRepository.java` | **Nový** – `findByServicePointId(UUID)` |
| `servicebooking/application/ServiceBookingService.java` | Injektovaný `NotificationService`; `create()` volá `sendConfirmation`; `updateStatus()` volá `sendStatusUpdate`; `findFirstAvailableSlot` vracia `TimeSlot` a používa `ServiceCapacity.isFull()`; nahradená konštanta `SLOT_CAPACITY` objektom `ServiceCapacity` |

---

### Rental doména (UC04–UC06)

| Súbor | Zmena |
|---|---|
| `rental/domain/Rental.java` | `implements Cancellable, Notifiable`; `getEmail()`, `getPhone()` |
| `rental/domain/Feedback.java` | **Nový** – entita pre hodnotenie po skončenom prenájme (UC05); polia `rating`, `comment`, `submittedAt` |
| `rental/repository/FeedbackRepository.java` | **Nový** – `existsByRentalId(UUID)` |
| `rental/api/SubmitFeedbackRequest.java` | **Nový** – `rating` (1–5), `comment` |
| `rental/api/FeedbackResponse.java` | **Nový** – `id`, `rentalId`, `rating`, `comment`, `submittedAt` |
| `rental/api/RentalController.java` | Pridaný endpoint `POST /{rentalId}/feedback` |
| `rental/application/RentalService.java` | Injektovaný `FeedbackRepository` a `NotificationService`; `submitFeedback()`; `preRegister()` volá `sendConfirmation`; `finishRental()` volá `sendStatusUpdate` |

---

### Spare parts (UC02 – náhradné diely)

| Súbor | Zmena |
|---|---|
| `sparepart/domain/SparePart.java` | **Nový** – entita: `sku`, `name`, `stockQuantity` |
| `sparepart/domain/OrderSparePart.java` | **Nový** – väzobná entita `ServiceBooking` ↔ `SparePart` + `quantity` |
| `sparepart/domain/PartOrder.java` | **Nový** – externá objednávka dielov: `orderedQuantity`, `estimatedDelivery`, `status`, `createdAt` |
| `sparepart/domain/PartOrderStatus.java` | **Nový** – enum `ORDERED`, `DELIVERED` (pôvodne `PENDING`, zmenené na `ORDERED` podľa EA) |
| `sparepart/repository/SparePartRepository.java` | **Nový** |
| `sparepart/repository/OrderSparePartRepository.java` | **Nový** – `findByServiceBookingId(UUID)` |
| `sparepart/repository/PartOrderRepository.java` | **Nový** – `findByServiceBookingId(UUID)` |
| `sparepart/api/SparePartResponse.java` | **Nový** – `id`, `sku`, `name`, `stockQuantity` |
| `sparepart/api/AddSparePartRequest.java` | **Nový** – `sparePartId`, `quantity`, `estimatedDelivery` |
| `sparepart/api/AddSparePartResponse.java` | **Nový** – `orderSparePartId`, `partOrderCreated`, `partOrderId`, ... |
| `sparepart/api/SparePartController.java` | **Nový** – `GET /api/spare-parts`, `POST /api/service-bookings/{id}/parts` |
| `sparepart/application/SparePartService.java` | **Nový** – `getAll()`, `addToBooking()`: rezervuje sklad alebo vytvára `PartOrder` + mení status na `WAITING_FOR_PARTS` |

---

### Inventory doména

| Súbor | Zmena |
|---|---|
| `inventory/domain/InventoryStock.java` | Pridané pole `minimumQuantity` + metóda `isBelowMinimum()` (podľa EA `StockItem`) |
| `inventory/domain/InventoryStock.java` | Konštruktor zmenený z `protected` na `public` (fix pre unit testy) |

---

### Facility doména

| Súbor | Zmena |
|---|---|
| `facility/domain/Facility.java` | Konštruktor zmenený z `protected` na `public` (fix pre unit testy) |

---

### Infraštruktúra / konfigurácia

| Súbor | Zmena |
|---|---|
| `src/main/resources/application.properties` | Pridané `spring.sql.init.mode=always`, `spring.jpa.defer-datasource-initialization=true` |
| `src/main/resources/data.sql` | **Nový** – seed dáta pre H2: 3 zákazníci, 3 prevádzky, 3 bicykle, 3 service bookings, produkty, sklad (`minimumQuantity`), predajná história, 3 náhradné diely (2 na sklade, 1 vypredaný) |

---

### Testy

| Súbor | Zmena |
|---|---|
| `test/.../RentalServiceTest.java` | Pridaný mock `FeedbackRepository` a `NotificationService` do konštruktora `RentalService` |
| `test/.../ServiceBookingServiceTest.java` | Pridaný mock `NotificationService` do konštruktora `ServiceBookingService` |

---

### Konzistenčné opravy (EA → kód)

| Zmena | Dôvod |
|---|---|
| `PartOrderStatus.PENDING` → `ORDERED` | EA model používa hodnotu `ORDERED` |
| `Notifiable.getNotificationEmail()` → `getEmail()` | Zhodné s názvom metódy v EA rozhraní |
| `Notifiable.getNotificationReference()` → `getPhone()` | Zhodné s názvom metódy v EA rozhraní |
| Pridaná trieda `TimeSlot` | EA má `TimeSlot` ako doménovú triedu |
| Pridaná trieda `ServiceCapacity` | EA má `ServiceCapacity` ako doménovú triedu |
| `ServiceBookingService`: konštanta `SLOT_CAPACITY` nahradená `ServiceCapacity` objektom | Konzistencia s EA doménovým modelom |
| `ServiceBookingService.findFirstAvailableSlot()` vracia `TimeSlot` | Konzistencia s EA doménovým modelom |
| `InventoryStock.minimumQuantity` | EA `StockItem` obsahuje toto pole |
| Pridaná entita `ServiceWorker` + `ServiceWorkerRepository` | EA `ServiceWorker` class |

---

### Dokumentácia (Round 1)

| Súbor | Zmena |
|---|---|
| `CONSISTENCY_REPORT.md` | **Nový** – mapovanie EA modelu voči kódu; aktualizovaný po opravách |
| `ChangeLog.md` | **Nový** – tento súbor |

---

## [F5-R2] Konzistenčné opravy Round 2 – zosúladenie ⚠️ odchýlok

### Dispatch → Expedition (EA: `ExpeditionRequest`)

| Súbor | Zmena |
|---|---|
| `dispatch/domain/ExpeditionRequest.java` | **Nový** – premenovaná trieda z `DispatchRequest`; pridané pole `requestedBy: WarehouseStaff` |
| `dispatch/domain/ExpeditionRequestItem.java` | **Nový** – premenovaná trieda z `DispatchRequestItem` |
| `dispatch/domain/RequestPriority.java` | **Nový** – premenovaný enum z `DispatchPriority` (EA: `RequestPriority`) |
| `dispatch/domain/RequestStatus.java` | **Nový** – premenovaný enum z `DispatchStatus` (EA: `RequestStatus`) |
| `dispatch/repository/ExpeditionRequestRepository.java` | **Nový** – premenovaný z `DispatchRequestRepository` |
| `dispatch/api/CreateExpeditionRequest.java` | **Nový** – premenovaný z `CreateDispatchRequest`; pridané pole `requestedById` |
| `dispatch/api/ExpeditionRequestItemPayload.java` | **Nový** – premenovaný z `DispatchRequestItemPayload` |
| `dispatch/api/ExpeditionRequestResponse.java` | **Nový** – premenovaný z `DispatchRequestResponse`; pridané pole `requestedById` |
| `dispatch/api/ExpeditionRequestController.java` | **Nový** – premenovaný z `DispatchRequestController`; endpoint `/api/expedition-requests` |
| `dispatch/application/ExpeditionRequestService.java` | **Nový** – premenovaný z `DispatchRequestService`; používa `StockItem`, `WarehouseStaff` |
| `dispatch/domain/DispatchRequest.java` | Vyprázdnené (len package deklarácia) |
| `dispatch/domain/DispatchRequestItem.java` | Vyprázdnené |
| `dispatch/domain/DispatchPriority.java` | Vyprázdnené |
| `dispatch/domain/DispatchStatus.java` | Vyprázdnené |
| `dispatch/repository/DispatchRequestRepository.java` | Vyprázdnené |
| `dispatch/api/CreateDispatchRequest.java` | Vyprázdnené |
| `dispatch/api/DispatchRequestItemPayload.java` | Vyprázdnené |
| `dispatch/api/DispatchRequestResponse.java` | Vyprázdnené |
| `dispatch/api/DispatchRequestController.java` | Vyprázdnené |
| `dispatch/application/DispatchRequestService.java` | Vyprázdnené |

---

### Inventory → EA mená (`StockItem`, `SaleRecord`)

| Súbor | Zmena |
|---|---|
| `inventory/domain/StockItem.java` | **Nový** – premenovaná trieda z `InventoryStock`; pridané pole `minimumQuantity` + `isBelowMinimum()` |
| `inventory/domain/SaleRecord.java` | **Nový** – premenovaná trieda z `SalesHistory` |
| `inventory/repository/StockItemRepository.java` | **Nový** – premenovaný z `InventoryStockRepository` |
| `inventory/repository/SaleRecordRepository.java` | **Nový** – premenovaný z `SalesHistoryRepository` |
| `inventory/api/InventoryController.java` | Aktualizovaný – používa `StockItemRepository`, `SaleRecordRepository` |
| `inventory/domain/InventoryStock.java` | Vyprázdnené |
| `inventory/domain/SalesHistory.java` | Vyprázdnené |
| `inventory/repository/InventoryStockRepository.java` | Vyprázdnené |
| `inventory/repository/SalesHistoryRepository.java` | Vyprázdnené |

---

### Facility – `FacilityType` → `FacilityStatus` (EA: `FacilityStatus`)

| Súbor | Zmena |
|---|---|
| `facility/domain/FacilityStatus.java` | **Nový** – premenovaný enum z `FacilityType` |
| `facility/domain/Facility.java` | Aktualizovaný – používa `FacilityStatus`; konštruktor `protected` → `public` |
| `facility/api/FacilityResponse.java` | Aktualizovaný – používa `FacilityStatus` |
| `facility/domain/FacilityType.java` | Vyprázdnené |

---

### WarehouseStaff (EA: `WarehouseStaff`)

| Súbor | Zmena |
|---|---|
| `dispatch/domain/WarehouseStaff.java` | **Nový** – entita: `fullName`, `email`, FK `warehouse_id → Facility`; môže vytvárať `ExpeditionRequest` |
| `dispatch/repository/WarehouseStaffRepository.java` | **Nový** – `findByWarehouseId(UUID)` |
| `dispatch/domain/ExpeditionRequest.java` | Pridané pole `requestedBy: WarehouseStaff` (ManyToOne, optional) |

---

### Infraštruktúra / konfigurácia (Round 2)

| Súbor | Zmena |
|---|---|
| `src/main/resources/data.sql` | **Nový** (v worktree) – seed dáta vrátane `minimum_quantity` pre inventory_stock a spare parts |
| `src/main/resources/application.properties` | Pridané `spring.sql.init.mode=always`, `spring.jpa.defer-datasource-initialization=true` |

---

### Dokumentácia (Round 2)

| Súbor | Zmena |
|---|---|
| `CONSISTENCY_REPORT.md` | Aktualizovaný – odchýlky Round 2 opravené; 43 konzistentných vs 34 pred Round 2 |
| `ChangeLog.md` | Aktualizovaný – táto sekcia |

---

## [F5-R3] Aktualizácia projektovej dokumentácie (SK4_F5_BikeFlow.docx)

| Sekcia | Zmena |
|---|---|
| **3.1 Tabuľka tried** | `DispatchRequest.java` → `ExpeditionRequest.java`; `DispatchRequestItem.java` → `ExpeditionRequestItem.java`; `InventoryStock.java` → `StockItem.java`; `SalesHistory.java` → `SaleRecord.java`; poznámky k riadkom aktualizované |
| **3.2 BCE vrstvy** | Referencie `DispatchRequestController`, `DispatchRequestService` → `ExpeditionRequestController`, `ExpeditionRequestService` |
| **UC03** | `DispatchRequestController/Service` → `ExpeditionRequestController/Service`; endpoint `/api/dispatch-requests` → `/api/expedition-requests`; entity `DispatchRequest/Item` → `ExpeditionRequest/Item` |
| **5.2 Notifiable** | `getNotificationEmail()` → `getEmail()`; `getNotificationReference()` → `getPhone()` |
| **5.4 (nová)** | Doménové triedy ServiceBooking: `TimeSlot`, `ServiceCapacity`, `ServiceWorker` |
| **5.5 (nová)** | Spare parts doména: `SparePart`, `OrderSparePart`, `PartOrder`; endpoint `POST /api/service-bookings/{id}/parts` |
| **5.6 (nová)** | `WarehouseStaff` – zamestnanec skladu; väzba na `ExpeditionRequest.requestedBy` |
| **5.7 (nová)** | Common hodnotové objekty: `DateRange` (@Embeddable), `NotificationService` |
| **6. Databázová schéma** | Flyway migrácie nahradené: Hibernate `ddl-auto=create-drop` + `data.sql` (`spring.sql.init.mode=always`) |

---

## [F5-R4] Build fix – Notifiable + ServiceBooking

| Súbor | Problém | Oprava |
|---|---|---|
| `common/Notifiable.java` | Rozhranie malo `getNotificationEmail()`/`getNotificationReference()`, ale `NotificationService` volal `getEmail()`/`getPhone()` | Metódy premenované na `getEmail()`, `getPhone()` |
| `servicebooking/domain/ServiceBooking.java` | Chýbal `@Embedded DateRange preferredWindow`; `ServiceBookingService` volal `setPreferredWindow()`/`getPreferredWindow()` ktoré neexistovali; implementoval staré metódy `Notifiable` | Nahradené `preferredFrom`/`preferredTo` za `@Embedded DateRange preferredWindow`; metódy opravené na `getEmail()`/`getPhone()` |
| `rental/domain/Rental.java` | Implementoval staré metódy `Notifiable` (`getNotificationEmail`, `getNotificationReference`) | Premenované na `getEmail()`, `getPhone()` |
