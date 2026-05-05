# BikeFlow – Konzistenčný súhrn kódu voči EA modelu (BikeFlow_v5.qea)

> Dátum: 2026-05-05  
> EA model: `BikeFlow_v5.qea`  
> Kód: `PSI_Project/src/main/java/sk/stuba/fiit/bikeflow`  
> Posledná aktualizácia: UI-R11 – globálny audit; pridané feedback UI (UC05), service_worker + warehouse_staff seed dáta (pozri `ChangeLog.md`)

---

## 1. Použitá metodika

EA model (`.qea`) bol prečítaný ako SQLite databáza. Z tabuliek `t_object`, `t_attribute` a `t_operation` boli extrahované všetky UML elementy (triedy, rozhrania, enumerácie, aktéri, use case-y). Kód bol mapovaný manuálne na základe súborovej štruktúry a deklarácií tried.

---

## 2. Common / Cross-cutting

| EA element | Kód | Status |
|---|---|---|
| `DateRange` (Class, `from`/`to`) | `common/DateRange.java` (@Embeddable) | ✅ Konzistentné |
| `Notifiable` (Interface, `getEmail()`, `getPhone()`) | `common/Notifiable.java` (`getEmail()`, `getPhone()`) | ✅ Konzistentné |
| `Cancellable` (Interface, `cancel()`, `isCancelled()`) | `common/Cancellable.java` | ✅ Konzistentné |
| `NotificationService` (Class) | `common/NotificationService.java` (logging) | ✅ Konzistentné |

---

## 3. Rental doména (UC04–UC06)

| EA element | Kód | Status |
|---|---|---|
| `RentalReservation` (Class) | `rental/domain/Rental.java` | ⚠️ Skrátené meno (`Rental` vs `RentalReservation`) |
| `Rental implements Cancellable, Notifiable` | `Rental implements Cancellable, Notifiable` | ✅ Konzistentné |
| `Feedback` (Class) | `rental/domain/Feedback.java` | ✅ Konzistentné |
| `RentalIssueReport` | `rental/domain/RentalIssueReport.java` | ✅ Konzistentné |
| `RentalStatus`: PRELIMINARY, ACTIVE, COMPLETED, ISSUE_REPORTED, CANCELLED | `RentalStatus` enum | ✅ Konzistentné |
| `ReservationFormUI` (Boundary) | `rental/api/RentalController.java` | ⚠️ Existuje, nepomenovaná ako UI (Spring Boot konvencia) |
| `ReservationController` (Control) | `rental/application/RentalService.java` | ⚠️ Existuje, nepomenovaná ako Controller (Spring Boot konvencia) |

---

## 4. Service doména (UC01–UC02)

| EA element | Kód | Status |
|---|---|---|
| `ServiceReservation` (Class) | `servicebooking/domain/ServiceBooking.java` | ⚠️ Iné meno (`ServiceBooking` vs `ServiceReservation`) |
| `ServiceOrder` (Class) | — | ❌ Chýba: v EA samostatná trieda; v kóde zlúčená so `ServiceBooking` |
| `ServiceBooking implements Cancellable, Notifiable` | `ServiceBooking implements Cancellable, Notifiable` | ✅ Konzistentné |
| `TimeSlot` (Class) | `servicebooking/domain/TimeSlot.java` | ✅ Konzistentné |
| `ServiceCapacity` (Class) | `servicebooking/domain/ServiceCapacity.java` | ✅ Konzistentné |
| `ServiceWorker` (Class) | `servicebooking/domain/ServiceWorker.java` | ✅ Konzistentné |
| `ServiceBookingStatus`: SCHEDULED, RECEIVED, WAITING_FOR_PARTS, IN_REPAIR, DONE, CANCELLED | `ServiceBookingStatus` enum | ✅ Konzistentné |

---

## 5. Spare parts (UC02 – objednávanie dielov)

| EA element | Kód | Status |
|---|---|---|
| `SparePart` (Class, `stockQuantity`) | `sparepart/domain/SparePart.java` | ✅ Konzistentné |
| `OrderSparePart` (Class) | `sparepart/domain/OrderSparePart.java` | ✅ Konzistentné |
| `PartOrder` (Class, status = ORDERED) | `sparepart/domain/PartOrder.java` (status = ORDERED/DELIVERED) | ✅ Konzistentné |
| `PartOrder` → `ServiceReservation` vzťah | `PartOrder.serviceBooking` (ManyToOne) | ✅ Konzistentné |

---

## 6. Inventory / Logistics doména (UC03)

| EA element | Kód | Status |
|---|---|---|
| `StockItem` (Class, `quantity`, `minimumQuantity`) | `inventory/domain/StockItem.java` (`quantity`, `minimumQuantity`, `isBelowMinimum()`) | ✅ Konzistentné |
| `SaleRecord` (Class) | `inventory/domain/SaleRecord.java` | ✅ Konzistentné |
| `Product` (Class) | `product/domain/Product.java` | ✅ Konzistentné |
| `ExpeditionRequest` / `ExpeditionRequestItem` | `dispatch/domain/ExpeditionRequest.java` / `ExpeditionRequestItem.java` | ✅ Konzistentné |
| `ExpeditionFormUI` (Boundary) | `dispatch/api/ExpeditionRequestController.java` | ✅ Konzistentné |
| `DistributionController` (Control) | `dispatch/application/ExpeditionRequestService.java` | ✅ Konzistentné |
| `RequestPriority` (Enum) | `dispatch/domain/RequestPriority.java` | ✅ Konzistentné |
| `RequestStatus` (Enum) | `dispatch/domain/RequestStatus.java` | ✅ Konzistentné |
| `Warehouse` (Class) | — | ❌ Modelovaná ako `Facility` s `type=WAREHOUSE` (akceptované zjednodušenie) |
| `CalendarEntry` / `PlanningCalendar` | — | ❌ Chýba (mimo rozsahu F5) |

---

## 7. Users / Customer doména

| EA element | Kód | Status |
|---|---|---|
| `Customer` (Class, Actor) | `customer/domain/CustomerAccount.java` | ⚠️ Iné meno; bez auth polí |
| `ServiceWorker` (Class) | `servicebooking/domain/ServiceWorker.java` | ✅ Konzistentné |
| `WarehouseStaff` (Class) | `dispatch/domain/WarehouseStaff.java` | ✅ Doplnené; väzba na `Facility` (warehouse) a `ExpeditionRequest.requestedBy` |
| `Employee` (Class) | — | ❌ Chýba (mimo rozsahu F5) |
| `CustomerSupportAgent` (Class) | — | ❌ Chýba (mimo rozsahu F5) |

---

## 8. Facility doména

| EA element | Kód | Status |
|---|---|---|
| `Facility` (Class, `name`, `type`, `location`) | `facility/domain/Facility.java` | ✅ Konzistentné |
| `FacilityStatus` (Enumeration) | `facility/domain/FacilityStatus.java` (`SERVICE_POINT`, `WAREHOUSE`, `SHOP`) | ✅ Konzistentné |

---

## 9. Bike doména

| EA element | Kód | Status |
|---|---|---|
| `Bike` (Class) | `bike/domain/Bike.java` | ✅ Konzistentné |
| `BikeStatus` | `bike/domain/BikeStatus.java` | ✅ Konzistentné |
| `BikeCategory` | `bike/domain/BikeCategory.java` | ✅ Konzistentné |

---

## 10. Use Cases – pokrytie

| Use Case | EA | Kód | Status |
|---|---|---|---|
| UC01 – Rezervácia servisu | ✅ | `ServiceBookingService.create()` | ✅ |
| UC02 – Oprava + náhradné diely | ✅ | `SparePartService.addToBooking()`, `ServiceBookingService.updateStatus()` | ✅ |
| UC03 – Sklad / dispatching | ✅ | `ExpeditionRequestService`, `InventoryController` | ✅ |
| UC04 – Životný cyklus prenájmu | ✅ | `RentalService.preRegister/start/finish` | ✅ |
| UC05 – Feedback po prenájme | ✅ | `RentalService.submitFeedback()` | ✅ |
| UC06 – Poškodený/chýbajúci bicykel | ✅ | `RentalService.reportIssue()` | ✅ |
| UC07 – Personalizácia bicykla | ✅ | — | ❌ Mimo rozsahu F5 |
| UC08 – Prihlásenie | ✅ | — | ❌ Mimo rozsahu F5 |
| UC09 – Zákazníková podpora | ✅ | — | ❌ Mimo rozsahu F5 |

---

## 11. BCE (Boundary–Control–Entity) – dodržanie vzoru

| Vrstva | Kód | Status |
|---|---|---|
| **Boundary** | `*Controller.java` (api balíček) | ✅ |
| **Control** | `*Service.java` (application balíček) | ✅ |
| **Entity** | `domain/*.java` | ✅ |
| **Repository** | `repository/*.java` | ✅ |

BCE vzor je konzistentne dodržaný naprieč všetkými doménami.

---

## 12. Súhrn odchýlok (po opravách Round 2)

| Kategória | Po Round 1 | Po Round 2 |
|---|---|---|
| ✅ Plne konzistentné | 34 | 43 |
| ⚠️ Čiastočné / iné pomenovanie | 14 | 5 |
| ❌ V EA, chýba v kóde | 7 | 4 |
| UC pokryté kódom | 6 / 9 | 6 / 9 |

### Zostávajúce odchýlky (akceptované)
1. **`RentalReservation` → `Rental`** – konceptuálne rovnaké; skrátené meno je bežná Spring Boot konvencia
2. **`ServiceReservation` → `ServiceBooking`** – konceptuálne rovnaké; meno rešpektuje Spring Boot konvenciu
3. **`ServiceOrder`** – v EA samostatná trieda; v kóde zlúčená so `ServiceBooking` (zjednodušenie)
4. **`Customer` → `CustomerAccount`** – dlhšie meno; bez auth polí (mimo rozsahu F5)
5. **`Employee`, `CustomerSupportAgent`** – mimo rozsahu F5; UC07–UC09 nie sú implementované
6. **`CalendarEntry` / `PlanningCalendar`** – mimo rozsahu F5
7. **`Warehouse`** – modelovaná ako `Facility` s `type=WAREHOUSE` (akceptované zjednodušenie)
8. **UC07–UC09** – v EA modelované, v F5 neimplementované
