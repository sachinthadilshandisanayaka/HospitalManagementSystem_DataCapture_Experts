# Clinic Appointment & Notification API

Spring Boot 3 + MongoDB REST API for managing clinic patients, practitioners, and appointments — built as a Java assessment project (DCE/LK/ST3).

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21+ (tested on Java 24) |
| Framework | Spring Boot 3.3.5 |
| Database | MongoDB 7.0 via Docker |
| Validation | Jakarta Bean Validation |
| Documentation | Swagger / OpenAPI 3 (SpringDoc) |
| Build | Maven |
| Testing | JUnit 5 + Mockito (subclass mode for Java 24) |
| Containerisation | Docker + Docker Compose (MongoDB + Mongo Express) |

---

## Project Structure

```
co.clinic.appointment
├── config/           # MongoConfig (@EnableMongoAuditing), SwaggerConfig
├── security/         # ApiKeyFilter — X-API-KEY header enforcement
├── entity/           # BaseEntity → Person (abstract) → Patient, Practitioner; Appointment
├── dto/
│   ├── request/      # PatientRequest, PractitionerRequest, AppointmentRequest, UpdateNotesRequest
│   └── response/     # PatientResponse, PractitionerResponse, AppointmentResponse, PageResponse<T>
├── mapper/           # AppointmentMapper, PatientMapper, PractitionerMapper
├── repository/       # Spring Data MongoDB interfaces
├── service/          # PatientService, PractitionerService, AppointmentService
├── controller/       # REST controllers
├── notification/     # Strategy + Factory: Email / SMS / Console channels
├── audit/            # AuditLog entity + AuditService (audit_logs collection)
├── history/          # AppointmentHistory + AppointmentHistoryService
├── exception/        # BaseException hierarchy + GlobalExceptionHandler
├── response/         # ResponseCode constants, SuccessResponse, ErrorDetail
└── loader/           # SampleDataLoader (seeds DB on first startup when empty)
```

---

## How to Run

### Prerequisites

- Java 21 or later (`java -version`)
- Maven 3.8+ (`mvn -version`)
- Docker Desktop installed and running (`docker --version`)

---

### Step 1 — Start MongoDB with Docker Compose

```bash
cd /path/to/HospitalManagmentSystem
docker-compose up -d
```

Expected output:
```
[+] Running 3/3
 ✔ Network hospitalmanagmentsystem_clinic-network  Created
 ✔ Container clinic-mongodb                        Started
 ✔ Container clinic-mongo-express                  Started
```

Verify containers are running:
```bash
docker ps
```

Both `clinic-mongodb` (port 27017) and `clinic-mongo-express` (port 8081) should show status `Up`.

---

### Step 2 — Build the JAR

```bash
mvn clean package -DskipTests
```

The JAR is created at: `target/appointment-api-1.0.0.jar`

---

### Step 3 — Run the Application

```bash
MONGO_URI="mongodb://admin:admin123@localhost:27017/clinicdb?authSource=admin" \
API_KEY="clinic-secret-key" \
java -jar target/appointment-api-1.0.0.jar
```

The app starts on port **8080**.

---

### Step 4 — Verify

```bash
# Should return paginated list with sample data
curl -H "X-API-KEY: clinic-secret-key" \
     "http://localhost:8080/api/practitioners?page=0&size=10"
```

- **Swagger UI**: http://localhost:8080/swagger-ui
- **Mongo Express** (DB browser): http://localhost:8081

---

### Step 5 — Stop Everything

```bash
docker-compose down          # stop containers, keep data
docker-compose down -v       # stop containers and delete all data
```

---

## Running Tests

```bash
mvn test
```

- 3 unit tests: appointment conflict rule logic
- 3 service-layer integration tests: happy path, 409 conflict, 404 not found
- All 6 tests run without any database connection (pure Mockito)

---

## API Endpoints

All endpoints require the header: `X-API-KEY: clinic-secret-key`

### Patients — `GET /api/patients`

All query parameters are optional. Only `page` and `size` are required for pagination.

| Method | Path | Params | Description |
|---|---|---|---|
| `POST` | `/api/patients` | — | Create a new patient |
| `GET` | `/api/patients/{id}` | — | Get patient by ID |
| `GET` | `/api/patients` | `page`, `size` | List all patients (paginated) |
| `GET` | `/api/patients` | `nationalId`, `page`, `size` | Search by national ID (partial match) |
| `GET` | `/api/patients` | `name`, `page`, `size` | Search by name (case-insensitive) |

### Practitioners — `GET /api/practitioners`

| Method | Path | Params | Description |
|---|---|---|---|
| `POST` | `/api/practitioners` | — | Create a new practitioner |
| `GET` | `/api/practitioners/{id}` | — | Get practitioner by ID |
| `GET` | `/api/practitioners` | `page`, `size` | List all practitioners (paginated) |
| `GET` | `/api/practitioners` | `specialty`, `page`, `size` | Filter by specialty (case-insensitive) |

### Appointments — `GET /api/appointments`

All filter parameters are optional. Any combination is valid.

| Method | Path | Params | Description |
|---|---|---|---|
| `POST` | `/api/appointments` | — | Create appointment (validates conflict rule) |
| `GET` | `/api/appointments/{id}` | — | Get appointment by ID |
| `GET` | `/api/appointments` | `page`, `size` | List all appointments |
| `GET` | `/api/appointments` | `practitionerId`, `page`, `size` | Filter by practitioner |
| `GET` | `/api/appointments` | `practitionerId`, `date`, `page`, `size` | Filter by practitioner + date (`YYYY-MM-DD`) |
| `GET` | `/api/appointments` | `practitionerId`, `status`, `page`, `size` | Filter by practitioner + status |
| `GET` | `/api/appointments` | `date`, `page`, `size` | Filter by date |
| `GET` | `/api/appointments` | `status`, `page`, `size` | Filter by status (`BOOKED`, `CANCELLED`, `COMPLETED`) |
| `PATCH` | `/api/appointments/{id}/cancel` | — | Cancel a BOOKED appointment |
| `PATCH` | `/api/appointments/{id}/complete` | — | Mark a BOOKED appointment as COMPLETED |
| `PATCH` | `/api/appointments/{id}` | body: `notes` | Update appointment notes (BOOKED only) |

#### Appointment Status Transitions

```
BOOKED ──► CANCELLED   (PATCH /{id}/cancel)
BOOKED ──► COMPLETED   (PATCH /{id}/complete)
```

Notes can only be edited while status is `BOOKED`. Attempting to edit a `COMPLETED` appointment returns HTTP 422.

---

## Paginated Response Format

All list endpoints return a `PageResponse<T>`:

```json
{
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 10,
    "totalElements": 42,
    "totalPages": 5,
    "last": false
  },
  "message": "Operation completed successfully",
  "success": true,
  "code": 1000
}
```

---

## Response Formats

### Success

```json
{
  "data": { "..." },
  "message": "Patient created successfully",
  "success": true,
  "code": 1000
}
```

### Error

```json
{
  "timestamp": "2026-04-27T10:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Practitioner already has a BOOKED appointment overlapping the requested time slot",
  "path": "/api/appointments"
}
```

### Validation Error (400)

```json
{
  "timestamp": "2026-04-27T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    "fullName must be at least 3 characters",
    "nationalId must not be blank"
  ],
  "path": "/api/patients"
}
```

---

## Response Codes

| Code | Meaning |
|---|---|
| 1000 | Success |
| 1001 | Invalid input |
| 1002 | Data not found |
| 1003 | Operation error |
| 1004 | Invalid operation |
| 1005 | Appointment conflict |
| 1006 | Duplicate record |

---

## Business Rules

1. **Appointment Conflict**: A practitioner cannot have two BOOKED appointments whose time slots overlap.
   - Condition: `newStart < existingEnd AND newEnd > existingStart`
   - Returns HTTP 409

2. **Cancel Rule**: Only `BOOKED` appointments can be cancelled. Returns HTTP 422 otherwise.

3. **Complete Rule**: Only `BOOKED` appointments can be marked as completed. Returns HTTP 422 otherwise.

4. **Time Validation**: `endTime` must be after `startTime`. Returns HTTP 400 otherwise.

5. **Completed Lock**: Notes cannot be edited on a `COMPLETED` appointment. Returns HTTP 422.

6. **Duplicate Patient**: Two patients cannot share the same `nationalId`. Returns HTTP 409.

7. **Duplicate Practitioner**: Two practitioners cannot share the same `registrationNo`. Returns HTTP 409.

---

## Quick Test with curl

```bash
# 1. Create a practitioner
curl -s -X POST http://localhost:8080/api/practitioners \
  -H "X-API-KEY: clinic-secret-key" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Dr. Kamal Silva","registrationNo":"SLMC-001","specialty":"General"}' | jq .

# 2. Create a patient
curl -s -X POST http://localhost:8080/api/patients \
  -H "X-API-KEY: clinic-secret-key" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Sachi Dilshan","nationalId":"199801234567","dateOfBirth":"1998-01-23","email":"sachi@example.com"}' | jq .

# 3. Book an appointment (replace IDs with values returned above)
curl -s -X POST http://localhost:8080/api/appointments \
  -H "X-API-KEY: clinic-secret-key" \
  -H "Content-Type: application/json" \
  -d '{"patientId":"<PATIENT_ID>","practitionerId":"<PRACTITIONER_ID>","startTime":"2026-06-01T09:00:00Z","endTime":"2026-06-01T09:30:00Z"}' | jq .

# 4. Try a conflicting slot (expects 409)
curl -s -X POST http://localhost:8080/api/appointments \
  -H "X-API-KEY: clinic-secret-key" \
  -H "Content-Type: application/json" \
  -d '{"patientId":"<PATIENT_ID>","practitionerId":"<PRACTITIONER_ID>","startTime":"2026-06-01T09:15:00Z","endTime":"2026-06-01T09:45:00Z"}' | jq .

# 5. List all appointments paginated
curl -s -H "X-API-KEY: clinic-secret-key" \
     "http://localhost:8080/api/appointments?page=0&size=10" | jq .

# 6. Filter appointments by status
curl -s -H "X-API-KEY: clinic-secret-key" \
     "http://localhost:8080/api/appointments?status=BOOKED&page=0&size=10" | jq .

# 7. Complete an appointment
curl -s -X PATCH http://localhost:8080/api/appointments/<APPOINTMENT_ID>/complete \
  -H "X-API-KEY: clinic-secret-key" | jq .

# 8. Cancel an appointment
curl -s -X PATCH http://localhost:8080/api/appointments/<APPOINTMENT_ID>/cancel \
  -H "X-API-KEY: clinic-secret-key" | jq .
```

---

## Postman Collection

Import `API.postman_collection.json` into Postman for a full pre-built collection.

The collection includes:
- All CRUD operations for Patients, Practitioners, and Appointments
- All pagination filter combinations (practitionerId, date, status, name, specialty)
- Status transition requests (cancel, complete)
- Error scenario requests (401, 404, 400 validation, 409 conflict, 422 invalid operation)

Set the `patientId`, `practitionerId`, and `appointmentId` collection variables after each create call.

---

## Design Patterns

See [docs/DESIGN_PATTERNS.md](docs/DESIGN_PATTERNS.md) for a full breakdown of every pattern used and why.

| Pattern | Location | Purpose |
|---|---|---|
| Strategy | `notification/` | Swappable notification channels (Email, SMS, Console) |
| Factory | `NotificationStrategyFactory` | Auto-discovers and dispatches to all channels |
| Builder | `SuccessResponse`, `NotificationEvent`, `*Response` DTOs | Immutable, readable object construction |
| Template Method | Entity hierarchy (`BaseEntity → Person → Patient/Practitioner`) | Shared fields, enforced subclass contract |
| Template Method | Exception hierarchy (`BaseException → typed exceptions`) | One handler covers all domain exceptions |
| Static Factory | `SuccessResponseHandler`, `ResponseCode` | Centralised, non-instantiable utility helpers |
| Repository | `*Repository` interfaces | Decouple business logic from database queries |
| Chain of Responsibility | `ApiKeyFilter` | Gate all requests before controllers run |
| DTO + Mapper | `dto/`, `mapper/` | Decouple API contract from database entity structure |
| Singleton | All `@Service`, `@Component` | One shared, stateless Spring-managed instance per class |

---

## Assumptions & Trade-offs

- **No Lombok**: Lombok is incompatible with Java 24 (`TypeTag::UNKNOWN`). All code uses explicit getters, setters, and manual builder patterns.
- **Notifications**: Fire-and-forget console logging. Production would wire to SendGrid (email), Twilio (SMS), or a message queue.
- **`changedBy` in audit**: Hardcoded to `"system"`. Production would extract the authenticated user from the security context.
- **Optimistic locking**: `@Version` on `Appointment` prevents concurrent update races in MongoDB.
- **Sample data**: Automatically seeded on first startup when the DB is empty (`SampleDataLoader`). Skipped on subsequent runs.
- **Tests**: Pure Mockito unit tests — no embedded MongoDB needed. Service-layer logic is tested directly.
