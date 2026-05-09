# SJSU Rent-A-Room

An online appointment style booking system for reserving rooms across the SJSU campus. Students browse available rooms, view open time slots, book a slot, and view or cancel their reservations. Admins manage rooms and availability.

Built as a CMPE 172 (Enterprise Software Platforms) term project. Stack:

- **Backend:** Java 17, Spring Boot, Spring JDBC (no ORM), Spring Security + JWT, Micrometer + Actuator
- **Frontend:** React 19, Vite, React Router
- **Database:** MySQL 8

The architecture is a classic layered enterprise design (Controller → Service → Repository → MySQL), with manual optimistic locking on availability slots to prevent double booking, and a mock external Notification Service to demonstrate a distribution boundary.

---

## Running Locally

### Prerequisites
- JDK 17+
- Node.js 18+ and npm
- MySQL 8 (e.g. `brew install mysql` on macOS)

### 1. Database setup

Start MySQL and create the database:

```bash
mysql -u root -p
```
```sql
CREATE DATABASE RoomDB;
EXIT;
```

The application creates its tables automatically on first startup via `schema.sql` (idempotent, safe to re-run).

### 2. Backend

```bash
cd sjsurentaroombackend
./mvnw spring-boot:run
```

By default the backend connects to `jdbc:mysql://localhost:3306/RoomDB` as `root`. If your MySQL setup differs, override via env vars before running:

```bash
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/RoomDB'
export SPRING_DATASOURCE_USERNAME='root'
export SPRING_DATASOURCE_PASSWORD='your-password'
```

The server starts on **http://localhost:8080**. Verify with:

```bash
curl http://localhost:8080/actuator/health
```

### 3. Frontend

In a second terminal:

```bash
cd sjsurentaroomfrontend
npm install
npm run dev
```

The app is served at **http://localhost:5173**.


## Project Layout

```
sjsurentaroom/
├── sjsurentaroombackend/    Spring Boot API (port 8080)
└── sjsurentaroomfrontend/   React + Vite SPA (port 5173)
```

## Key Endpoints

| Method | Path | Purpose |
|---|---|---|
| POST | `/auth/register` | Create a student account |
| POST | `/auth/login` | Obtain a JWT |
| GET | `/rooms` | List rooms |
| GET | `/availability` | List availability slots |
| POST | `/reservations/book` | Book a slot (auth required) |
| PUT | `/reservations/{id}/cancel` | Cancel a reservation |
| GET | `/actuator/health` | Service health |
| GET | `/actuator/metrics/reservations.bookings` | Booking success/failure counters |
