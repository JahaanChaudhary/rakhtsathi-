# 🩸 RakhtSathi — Real-Time Blood Donor Finder

> A production-grade full-stack web application that connects blood donors with people in urgent need — featuring smart compatibility search, async email notifications, privacy-safe donor proximity, and a real-time admin analytics dashboard.

## 🌐 Live Demo

🔗 **[rakhtsathi-production.up.railway.app](https://rakhtsathi-production.up.railway.app)**

## 📌 The Problem It Solves

In emergency situations, finding a compatible blood donor quickly can be the difference between life and death. Existing solutions are slow, fragmented, and lack real-time donor availability. RakhtSathi solves this by maintaining a live registry of donors, instantly notifying matching donors via email the moment a request is submitted, and surfacing results in seconds.

---

## ✨ Key Features

### 🔍 Smart Donor Search
- Searches by blood group and city with **compatibility fallback** — if no exact match, automatically finds compatible blood groups using a `HashMap<String, List<String>>` lookup
- **Privacy-safe proximity** — uses the **Haversine formula** server-side to compute distance ranges ("Within 2 km") instead of exposing exact donor GPS coordinates

### 📧 Async Email Notifications
- Instantly notifies all matching donors via Gmail SMTP using `@Async` — search results load immediately without waiting for emails to send
- Sends confirmation to requester with number of donors notified

### 🔐 Security
- **BCrypt password hashing** — plain text passwords never stored
- **Spring Security** role-based access control — DONOR and ADMIN roles with protected routes
- **DTO pattern** — raw entities never sent to frontend; password field never exposed
- **Circular dependency fix** — `BCryptPasswordEncoder` extracted to separate `PasswordConfig` bean

### 🛡️ Admin Dashboard
- Real-time **Chart.js analytics** — blood group distribution (doughnut), city-wise donors (bar chart), request status ratio (doughnut)
- **Client-side pagination** — all data loaded once as JSON, JavaScript handles paging with zero server round-trips
- **Live search filter** — filter donors by name, city, blood group, and availability in real time
- Request management — mark blood requests as fulfilled

### 🩸 Blood Compatibility Guide
- Interactive compatibility reference table — who can donate to whom, population percentages, Universal Donor (O-) and Universal Recipient (AB+) facts

### 🏗️ Production Architecture
- **DTO projection** — `DonorDTO`, `UserDTO`, `SearchRequestDTO` prevent entity exposure
- **Custom exception hierarchy** — `ResourceNotFoundException`, `DuplicateEmailException`, `DonorProfileNotFoundException`
- **`@ControllerAdvice`** global exception handler with user-friendly error pages
- **`@Transactional`** on all write methods; `readOnly = true` on all reads for performance
- **`@Slf4j`** structured logging across all service layers
- **DataSeeder** — seeds 1 admin + 20 donors with coordinates + 8 blood requests on first run

---

## 🛠️ Tech Stack

| Layer       | Technology                                      |
|-------------|-------------------------------------------------|
| Backend     | Spring Boot 3.1.5, Java 17                      |
| Security    | Spring Security 6, BCrypt                       |
| Database    | PostgreSQL 15, Spring Data JPA, Hibernate       |
| Frontend    | Thymeleaf, Bootstrap 5, Chart.js, Leaflet.js    |
| Email       | JavaMailSender, Gmail SMTP, `@Async`            |
| Build       | Maven 3.9, Lombok 1.18.30                       |
| Deploy      | Docker, Railway, managed PostgreSQL             |

---

## 🔑 Key Technical Decisions

### 1. Circular Dependency Resolution
`SecurityConfig` needs `UserService` for auth. `UserService` needs `BCryptPasswordEncoder`. If the encoder was defined inside `SecurityConfig`, Spring would detect a circular bean dependency. **Fix:** `BCryptPasswordEncoder` extracted to a standalone `PasswordConfig` class, breaking the cycle entirely.

### 2. Privacy-Safe Location
Showing exact GPS coordinates on a map exposes donor home/work locations to anyone. **Fix:** Haversine formula runs server-side, coordinates are nulled out on the DTO before the response is sent, and only a bucketed range ("Within 2 km") reaches the frontend.

### 3. Blood Compatibility Logic
```java
COMPATIBLE.put("AB+", List.of("A+","A-","B+","B-","AB+","AB-","O+","O-"));
COMPATIBLE.put("O-",  List.of("O-"));
```
HashMap gives O(1) lookup. If exact blood group + city search returns zero results, the fallback queries compatible groups — matching real-world transfusion rules.

### 4. Async Email
Without `@Async`, the search results page would block for 3–5 seconds while each email sends. With `@Async`, results render instantly and emails are dispatched in a background thread pool.

### 5. Client-Side Pagination
All donor and request data is loaded once as JSON using Jackson serialization. JavaScript slices the array per page — no server round-trip on Next/Prev. `e.preventDefault()` stops browser navigation entirely.

---

## 🚀 Running Locally

### Prerequisites
- Java 17
- PostgreSQL 15
- Maven 3.9

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/JahaanChaudhary/rakhtsathi-.git
cd rakhtsathi-
```

**2. Create PostgreSQL database**
```sql
CREATE DATABASE blood_donor_db;
```

**3. Configure application.properties**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blood_donor_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.mail.username=yourgmail@gmail.com
spring.mail.password=your_16_char_app_password
```

**4. Run**
```bash
mvn spring-boot:run
```

**5. Open** `http://localhost:8080`

On first run, DataSeeder automatically creates:
- 1 admin account
- 20 donor profiles across Mumbai, Pune, Delhi, Bangalore, Chennai
- 8 sample blood requests

---

## 🌍 Deployment

Deployed on **Railway** using Docker with a multi-stage build:

```dockerfile
FROM maven:3.9.4-eclipse-temurin-17 AS build
# Build stage — compiles and packages the JAR

FROM eclipse-temurin:17-jre
# Runtime stage — minimal image, just the JRE + JAR
```

All secrets managed via Railway environment variables — no credentials in the codebase.

---

## 🧑‍💻 Author

**Jahaan Chaudhary**
[github.com/JahaanChaudhary](https://github.com/JahaanChaudhary)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
