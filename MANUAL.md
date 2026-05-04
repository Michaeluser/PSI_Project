# BikeFlow – Manuál spustenia a testovania

> Projekt: PSI_Project (FIIT STU, PSI 2025/26, Fáza 5)

---

## 1. Spustenie aplikácie

### Požiadavky

| Nástroj | Verzia |
|---|---|
| Java | 21+ |
| Maven | 3.9+ |

PostgreSQL **nie je potrebný** — aplikácia používa vstavanú H2 in-memory databázu.

### Spustenie

```bash
mvn spring-boot:run
```

Aplikácia beží na **http://localhost:8080**.

Pri každom štarte Hibernate schému zmaže a znovu vytvorí (`ddl-auto=create-drop`), potom `data.sql` naplní tabuľky testovacími dátami. Po reštarte sa dáta resetujú na seed stav.

### H2 konzola (voliteľné)

Priamy pohľad do databázy je dostupný na **http://localhost:8080/h2-console**:

```
JDBC URL:  jdbc:h2:mem:bikeflow
User:      sa
Password:  (prázdne)
```

### Zastavenie

`Ctrl + C` v termináli.

---

## 2. Seed dáta (predvyplnené po štarte)

### Zákazníci

| ID (skrátené) | Meno | Email | Kredit | Karta |
|---|---|---|---|---|
| `11111111-...` | Andrej Tester | andrej@example.com | 120.00 € | áno |
| `22222222-...` | Lucia Rider | lucia@example.com | 8.00 € | áno |
| `33333333-...` | Guest Without Card | guest@example.com | 70.00 € | nie |

### Prevádzky

| ID (skrátené) | Kód | Názov | Typ |
|---|---|---|---|
| `aaaa...a1` | SER-BA-01 | BikeFlow Servis Bratislava | `SERVICE_POINT` |
| `aaaa...a2` | WH-BA-01 | BikeFlow Sklad Bratislava | `WAREHOUSE` |
| `aaaa...a3` | SHOP-TT-01 | BikeFlow Predajna Trnava | `SHOP` |

### Bicykle (všetky v Trnave, stav `AVAILABLE`)

| ID (skrátené) | Kód | Model | Kategória | Cena/min |
|---|---|---|---|---|
| `bbbb...b1` | BK-001 | Urban Flow 300 | CITY | 0.25 € |
| `bbbb...b2` | BK-002 | Trail Pro X | MOUNTAIN | 0.35 € |
| `bbbb...b3` | BK-003 | Volt Ride E1 | ELECTRIC | 0.55 € |

Skutočné UUID hodnôt zisti cez:

```
GET http://localhost:8080/api/bikes
GET http://localhost:8080/api/customers
GET http://localhost:8080/api/facilities
GET http://localhost:8080/api/service-bookings
```

---

## 3. Testovanie UC01 – Rezervácia servisného termínu

**Endpoint:** `POST /api/service-bookings`

Zákazník si chce rezervovať servis bicykla. Servisné stredisko je `SER-BA-01` (Bratislava).

```bash
curl -s -X POST http://localhost:8080/api/service-bookings \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Andrej Tester",
    "customerEmail": "andrej@example.com",
    "bikeBrand": "Trek",
    "bikeModel": "Marlin 7",
    "problemDescription": "Predne brzdy nereaguju spravne.",
    "preferredFrom": "2026-06-10T08:00:00+02:00",
    "preferredTo": "2026-06-12T18:00:00+02:00",
    "servicePointId": "<UUID servisného strediska>"
  }'
```

**Očakávaný výsledok:** stav `SCHEDULED`, vyplnené `scheduledAt` (zaokrúhlené na celú hodinu v rámci preferovaného okna).

---

## 4. Testovanie UC02 – Spracovanie servisnej opravy

Prebieha v dvoch krokoch na existujúcej rezervácii (napr. seed rezervácia `SB-2026-002` v stave `IN_REPAIR`).

### Krok 1 – Príjem bicykla do servisu

**Endpoint:** `POST /api/service-bookings/{bookingId}/repair-intake`

```bash
curl -s -X POST http://localhost:8080/api/service-bookings/<bookingId>/repair-intake \
  -H "Content-Type: application/json" \
  -d '{
    "technicalState": "Disk je pokriveny, brzdy nefunkcne.",
    "additionalFindings": "Retaz tiez vyzaduje vymenu.",
    "workItems": [
      { "description": "Vymena brzdovych platniciek", "price": 45.00 },
      { "description": "Nastavenie diskovych brzd", "price": 20.00 }
    ],
    "requiredParts": [
      { "productId": "<UUID produktu Disc brake set>", "quantity": 1, "unitPrice": 30.00 }
    ],
    "clientApproved": true
  }'
```

**Očakávaný výsledok:** stav `WAITING_FOR_PARTS` (ak produkt nie je na sklade pri servisnom stredisku) alebo `IN_REPAIR` (ak je dostupný). Vypočítaná `preliminaryPrice` = práca + diely − loyalty zľava (10 % z práce pre zákazníka s 2+ dokončenými servismi).

### Krok 2 – Dokončenie opravy

**Endpoint:** `POST /api/service-bookings/{bookingId}/complete`

```bash
curl -s -X POST http://localhost:8080/api/service-bookings/<bookingId>/complete
```

**Očakávaný výsledok:** stav `DONE`.

---

## 5. Testovanie UC03 – Expedičná požiadavka (sklad → predajňa)

**Endpoint:** `POST /api/expedition-requests`

Sklad (`WH-BA-01`) zasiela tovar do predajne (`SHOP-TT-01`). UUID skladov zisti cez `GET /api/facilities`.

```bash
curl -s -X POST http://localhost:8080/api/expedition-requests \
  -H "Content-Type: application/json" \
  -d '{
    "sourceFacilityId": "<UUID WH-BA-01>",
    "targetFacilityId": "<UUID SHOP-TT-01>",
    "priority": "NORMAL",
    "notes": "Doplnenie skladu pred sezónou.",
    "items": [
      { "productId": "<UUID produktu 29 inch tire>", "quantity": 5 },
      { "productId": "<UUID produktu 11-speed chain>", "quantity": 3 }
    ]
  }'
```

`priority` môže byť `NORMAL` alebo `URGENT`.

**Očakávaný výsledok:** vytvorená expedičná požiadavka so stavom `PENDING`, odpočítané množstvo zo skladových záznamov zdrojovej prevádzky.

---

## 6. Testovanie UC04 – Prenájom bicykla

Celý životný cyklus prenájmu: **pre-registrácia → štart → koniec**.

### Krok 1 – Pre-registrácia (cez webové rozhranie)

Otvor **http://localhost:8080** v prehliadači:

1. Záložka **Rentals** → wizard „Pre-register Rental"
2. **Krok 1:** vyber mesto `Trnava`, klikni „Load available bikes", klikni na bicykel (napr. Urban Flow 300)
3. **Krok 2:** vyber zákazníka (napr. Andrej Tester), zadaj počet minút (napr. `30`)
4. **Krok 3:** skontroluj súhrn, klikni „Pre-register" → bicykel sa zmení na `PRE_RESERVED`, vytvorí sa prenájom so stavom `PRELIMINARY`

Alebo cez API:

```bash
curl -s -X POST http://localhost:8080/api/rentals/pre-register \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "11111111-1111-1111-1111-111111111111",
    "bikeId": "<UUID bicykla>",
    "plannedMinutes": 30
  }'
```

Ulož si `id` z odpovede (`<rentalId>`).

### Krok 2 – Štart prenájmu

```bash
curl -s -X POST http://localhost:8080/api/rentals/<rentalId>/start
```

**Očakávaný výsledok:** stav `ACTIVE`, bicykel `RENTED`, zaznamenaný `startedAt`.

### Krok 3 – Ukončenie prenájmu

```bash
curl -s -X POST http://localhost:8080/api/rentals/<rentalId>/finish
```

**Očakávaný výsledok:** stav `COMPLETED`, vypočítaná skutočná cena odpočítaná z kreditov zákazníka, bicykel `AVAILABLE`.

### Alternatíva: zrušenie pred štartom

```bash
curl -s -X POST http://localhost:8080/api/rentals/<rentalId>/cancel
```

**Očakávaný výsledok:** stav `CANCELLED`, bicykel `AVAILABLE`, kredit sa **neodpočíta** (kredit sa čerpá až pri dokončení).

---

## 7. Overenie stavu cez API

```bash
# Všetky prenájmy
GET http://localhost:8080/api/rentals

# Všetky servisné rezervácie
GET http://localhost:8080/api/service-bookings

# História servisu pre zákazníka
GET http://localhost:8080/api/service-bookings/history?customerEmail=andrej@example.com

# Expedičné požiadavky
GET http://localhost:8080/api/expedition-requests

# Bicykle (voliteľne filtrovať podľa mesta)
GET http://localhost:8080/api/bikes?city=Trnava

# Sklad
GET http://localhost:8080/api/inventory
```