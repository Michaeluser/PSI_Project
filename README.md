# BikeFlow Local v2

Local Spring Boot + PostgreSQL implementation for UC01–UC06.

## Covered use cases

- UC01: reserve service appointment
- UC02: process and manage service repair
- UC03: create dispatch request
- UC04: rent a bike
- UC05: preliminary registration before rental
- UC06: report damaged or missing bike

## Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Flyway
- PostgreSQL
- Maven
- Static HTML/CSS/JavaScript frontend

## Run

1. Create PostgreSQL database:
   - database: `bikeflow`
   - user: `postgres`
   - password: `postgres`
2. Adjust `src/main/resources/application.properties` if needed.
3. Run:
   - `mvn spring-boot:run`
4. Open:
   - `http://localhost:8080`

## Notes

- No security is used.
- Frontend is intentionally simple and meant for manual scenario testing.
- Flyway creates schema and inserts seed data automatically.

## Main API endpoints

- `GET /api/customers`
- `GET /api/facilities`
- `GET /api/bikes?city=Trnava`
- `GET /api/service-bookings`
- `POST /api/service-bookings`
- `PATCH /api/service-bookings/{bookingId}/status`
- `GET /api/products`
- `GET /api/inventory/overview?facilityId=...`
- `GET /api/inventory/sales-analysis?facilityId=...&days=30`
- `GET /api/dispatch-requests`
- `POST /api/dispatch-requests`
- `GET /api/rentals`
- `POST /api/rentals/pre-register`
- `POST /api/rentals/{rentalId}/start`
- `POST /api/rentals/{rentalId}/finish`
- `POST /api/rentals/{rentalId}/issue`
