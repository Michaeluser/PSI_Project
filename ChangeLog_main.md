# BikeFlow – ChangeLog (main project)

> Projekt: PSI_Project (FIIT STU, PSI 2025/26, Fáza 5)
> Autor zmien: Andrej Mikloš
> Obdobie: 2026-05-04

---

## [UI-R1] Rental tab – wizard pre pre-registráciu prenájmu

### Problém

Rental tab mal flat form bez vizuálneho vedenia používateľa. Po kliknutí na „Load available bikes" sa bicykle plnili do skrytého `<select>` dropdownu – žiadny viditeľný zoznam. Tlačidlo „Continue" a kroková navigácia neexistovali.

### Zmeny

| Súbor | Zmena |
|---|---|
| `static/index.html` | Flat `pre-register-form` nahradený 3-krokovým wizard layoutom: stepper header + 3 wizard panely; zachovaná sekcia „Start / finish / report issue" |
| `static/styles.css` | Pridané štýly: `.wizard-stepper`, `.wizard-step-item`, `.step-circle`, `.step-connector`, `.city-row`, `.bike-cards`, `.bike-card`, `.bike-card-model/meta/price`, `.wizard-nav`, `.rental-summary`; `button:disabled` opacity |
| `static/app.js` | Nahradený `load-bikes-button` listener a odstránený `pre-register-form` submit handler; pridaná wizard logika |

### Wizard flow

| Krok | Obsah | Akcia |
|---|---|---|
| 1 – Select Bike | Dropdown mesta + „Load available bikes" → bikové karty (model, kód, kategória, lokalita, cena/min) | Klik na kartu ju označí a odblokuje „Continue →" |
| 2 – Customer & Duration | Výber zákazníka (select) + planned minutes | „Continue →" zostaví súhrn a prejde na krok 3 |
| 3 – Confirm | Súhrn: bicykel, kategória, lokalita, zákazník, minúty, odhadovaná cena | „Pre-register" → `POST /api/rentals/pre-register`; po úspechu reset wizarda na krok 1 |

### Poznámky

- Všetky seed bicykle sú v meste **Trnava**; výber Bratislavy zobrazí „No available bikes in this city."
- `customer-select` ID zachované – `loadReferenceData()` ho stále plní pri štarte aj po každom úspešnom prenájme.
- Backend (`BikeController`, `RentalController`, `RentalService`) zostal bez zmien.

---

## [UI-R2] Fix – prázdna DB po štarte (žiadne dostupné bicykle)

### Problém

`spring.flyway.enabled=false` + chýbajúci `data.sql` → Hibernate vytvorilo prázdne tabuľky pri každom štarte. `/api/bikes?city=Trnava` vracalo prázdne pole, wizard zobrazoval „No available bikes in this city."

### Zmeny

| Súbor | Zmena |
|---|---|
| `src/main/resources/data.sql` | **Nový** – seed dáta: 3 zákazníci, 3 prevádzky (Bratislava ×2, Trnava ×1), 3 bicykle v Trnave (AVAILABLE), 3 produkty, 6 skladových záznamov, 4 záznamy predaja |
| `src/main/resources/application.properties` | Pridané `spring.sql.init.mode=always` a `spring.jpa.defer-datasource-initialization=true` |

### Príčina

`spring.sql.init.mode=always` spustí `data.sql` automaticky po štarte. `spring.jpa.defer-datasource-initialization=true` zabezpečí, že SQL sa vykoná až po tom, čo Hibernate vytvorí schému – bez toho by INSERT zlyhal na neexistujúcich tabuľkách.
