# Design Patterns — Clinic Appointment Management System

## Overview

This document describes every design pattern applied in the system, where each one lives in the codebase, and why it was chosen.

---

## 1. Strategy Pattern

**Location:** `src/main/java/co/clinic/appointment/notification/`

### What it is
Defines a family of algorithms (notification channels) behind a common interface so they can be swapped or extended independently.

### How it is used here
`NotificationStrategy` is the interface. Each notification channel is a separate implementation:

```
NotificationStrategy (interface)
    ├── EmailNotificationStrategy   → channel = "EMAIL"
    ├── SmsNotificationStrategy     → channel = "SMS"
    └── ConsoleNotificationStrategy → channel = "CONSOLE"
```

```java
// NotificationStrategy.java
public interface NotificationStrategy {
    void send(NotificationEvent event);
    String getChannel();
}

// EmailNotificationStrategy.java
@Component
public class EmailNotificationStrategy implements NotificationStrategy {
    @Override
    public void send(NotificationEvent event) { /* send email */ }

    @Override
    public String getChannel() { return "EMAIL"; }
}
```

### Why it was used
Adding a new notification channel (e.g., Push Notification) only requires creating one new `@Component` class. No existing code needs to change — the factory picks it up automatically.

---

## 2. Factory Pattern

**Location:** `src/main/java/co/clinic/appointment/notification/NotificationStrategyFactory.java`

### What it is
A factory that holds all strategy instances and decides which one(s) to invoke at runtime.

### How it is used here
Spring injects all `NotificationStrategy` beans as a `List`. The factory builds a channel-keyed map and provides a `notifyAll()` method that dispatches the event to every channel at once.

```java
@Component
public class NotificationStrategyFactory {

    private final Map<String, NotificationStrategy> strategyMap;

    // Spring auto-collects all NotificationStrategy beans
    public NotificationStrategyFactory(List<NotificationStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(NotificationStrategy::getChannel, Function.identity()));
    }

    public void notifyAll(NotificationEvent event) {
        strategyMap.values().forEach(strategy -> strategy.send(event));
    }
}
```

### Why it was used
Callers (e.g., `AppointmentService`) call `notificationFactory.notifyAll(event)` with no knowledge of which channels exist. The factory owns that responsibility entirely.

---

## 3. Builder Pattern

**Location:**
- `src/main/java/co/clinic/appointment/response/SuccessResponse.java`
- `src/main/java/co/clinic/appointment/notification/NotificationEvent.java`
- `src/main/java/co/clinic/appointment/dto/response/AppointmentResponse.java`

### What it is
Constructs a complex object step by step using a fluent API. The object becomes immutable once `build()` is called.

### How it is used here
Each class has a static inner `Builder` class. The outer class constructor is private — the only way to create an instance is through the builder.

```java
// SuccessResponse.java
public class SuccessResponse {

    private final Object data;
    private final String message;
    private final boolean success;
    private final int code;

    private SuccessResponse(Builder builder) { /* assign fields */ }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        public Builder data(Object data)       { this.data = data; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder success(boolean success){ this.success = success; return this; }
        public Builder code(int code)          { this.code = code; return this; }
        public SuccessResponse build()         { return new SuccessResponse(this); }
    }
}
```

Usage at the call site is readable and concise:

```java
SuccessResponse.builder()
        .data(result)
        .message("Patient created successfully")
        .success(true)
        .code(ResponseCode.SUCCESS)
        .build();
```

### Why it was used
Prevents partially constructed objects from being passed around. All fields are final after construction, making these objects safe to share across threads.

---

## 4. Template Method Pattern — Entity Hierarchy

**Location:** `src/main/java/co/clinic/appointment/entity/`

### What it is
An abstract base class defines a common structure and declares abstract methods that subclasses must implement.

### How it is used here
The entity hierarchy has two levels of inheritance:

```
BaseEntity   (abstract)  — id, createdAt, updatedAt, version
    └── Person  (abstract)  — fullName, abstract getPersonType()
            ├── Patient      — nationalId, dateOfBirth, email, phone  → "PATIENT"
            └── Practitioner — registrationNo, specialty              → "PRACTITIONER"
```

`Person` declares `getPersonType()` as abstract, forcing every concrete entity to identify itself:

```java
// Person.java
public abstract class Person extends BaseEntity {
    private String fullName;
    public abstract String getPersonType();
}

// Patient.java
public class Patient extends Person {
    @Override
    public String getPersonType() { return "PATIENT"; }
}

// Practitioner.java
public class Practitioner extends Person {
    @Override
    public String getPersonType() { return "PRACTITIONER"; }
}
```

### Why it was used
Audit fields (`createdAt`, `updatedAt`, `version`) are written once in `BaseEntity` and inherited by all entities. `Person` groups shared person fields without duplicating them in both `Patient` and `Practitioner`.

---

## 5. Template Method Pattern — Exception Hierarchy

**Location:** `src/main/java/co/clinic/appointment/exception/`

### What it is
The same Template Method idea applied to exceptions: a base class carries the full error structure; subclasses supply only the specific values.

### How it is used here

```
BaseException (abstract)  — error label, response code, HTTP status, details
    ├── DataNotFoundException        → 404 Not Found         | code 1002
    ├── DuplicateRecordException     → 409 Conflict          | code 1006
    ├── AppointmentConflictException → 409 Conflict          | code 1005
    ├── InvalidDataException         → 400 Bad Request       | code 1001
    └── InvalidOperationException    → 422 Unprocessable     | code 1004
```

Each subclass calls `super()` with its fixed values — the caller only needs to provide a message:

```java
// BaseException.java
public abstract class BaseException extends RuntimeException {
    private final String error;
    private final Integer code;
    private final HttpStatus httpStatus;
}

// DataNotFoundException.java
public class DataNotFoundException extends BaseException {
    public DataNotFoundException(String message) {
        super("Not Found", message, ResponseCode.MISSING_DATA, HttpStatus.NOT_FOUND);
    }
}
```

`GlobalExceptionHandler` catches the single base type and the correct HTTP status, code, and error label are derived automatically:

```java
@ExceptionHandler(BaseException.class)
public ResponseEntity<ErrorDetail> handleBaseException(BaseException ex, ...) {
    // ex.getHttpStatus(), ex.getCode(), ex.getError() — all already set
}
```

### Why it was used
One `@ExceptionHandler` method handles every domain exception. No switch-case or per-type handler is needed. Adding a new exception type costs one file and zero changes to the handler.

---

## 6. Static Factory / Utility Pattern

**Location:**
- `src/main/java/co/clinic/appointment/response/SuccessResponseHandler.java`
- `src/main/java/co/clinic/appointment/response/ResponseCode.java`

### What it is
A `final` class with a private constructor and only `static` methods or constants. It cannot be instantiated — it exists purely as a namespace for related utilities.

### How it is used here

```java
// SuccessResponseHandler.java
public final class SuccessResponseHandler {

    private SuccessResponseHandler() {}  // blocks instantiation

    public static ResponseEntity<SuccessResponse> generateResponse(Object data) { ... }
    public static ResponseEntity<SuccessResponse> generateResponse(Object data, String message) { ... }
    public static ResponseEntity<SuccessResponse> generateCreatedResponse(Object data, String message) { ... }
}
```

```java
// ResponseCode.java
public final class ResponseCode {

    private ResponseCode() {}

    public static final int SUCCESS          = 1000;
    public static final int INVALID_INPUT    = 1001;
    public static final int MISSING_DATA     = 1002;
    public static final int ERROR_OPERATION  = 1003;
    public static final int INVALID_OPERATION = 1004;
    public static final int CONFLICT_DATA    = 1005;
    public static final int DUPLICATE_DATA   = 1006;
}
```

### Why it was used
Centralises response wrapping in one place. Every controller calls `SuccessResponseHandler.generateResponse(...)` instead of building `ResponseEntity` objects manually, keeping controllers consistent and concise.

---

## 7. Repository Pattern

**Location:** `src/main/java/co/clinic/appointment/repository/`

### What it is
Data access is hidden behind an interface. The service layer never deals with query syntax, cursors, or database sessions — it calls a method by name and gets a result.

### How it is used here

```java
// AppointmentRepository.java
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    @Query("{ 'practitionerId': ?0, 'status': 'BOOKED', 'startTime': { $lt: ?2 }, 'endTime': { $gt: ?1 } }")
    List<Appointment> findOverlapping(String practitionerId, Instant start, Instant end);

    Page<Appointment> findByPractitionerId(String practitionerId, Pageable pageable);
    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);
    // ...
}
```

Services call the interface with no MongoDB-specific code:

```java
// AppointmentService.java
List<Appointment> overlaps = appointmentRepository.findOverlapping(
        request.getPractitionerId(), request.getStartTime(), request.getEndTime());
```

### Why it was used
Business logic is fully decoupled from the database. Swapping MongoDB for another store only touches the repository layer. Services and controllers remain unchanged.

---

## 8. Chain of Responsibility Pattern

**Location:** `src/main/java/co/clinic/appointment/security/ApiKeyFilter.java`

### What it is
A request passes through a chain of handlers. Each handler either processes the request and passes it on, or stops the chain by responding directly.

### How it is used here
`ApiKeyFilter` extends `OncePerRequestFilter`. For every incoming HTTP request it makes a decision:

```java
@Override
protected void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ... {

    // Public paths bypass the key check
    if (path.startsWith("/swagger-ui") || path.startsWith("/api-docs")) {
        filterChain.doFilter(request, response);  // pass through
        return;
    }

    // Invalid key — stop the chain, respond with 401
    if (apiKey == null || !apiKey.equals(expectedApiKey)) {
        response.setStatus(401);
        response.getWriter().write("{ ... }");
        return;
    }

    // Valid key — pass to the next filter / controller
    filterChain.doFilter(request, response);
}
```

### Why it was used
Security is enforced at a single entry point before any controller logic runs. Controllers are completely unaware of authentication — they only receive requests that have already been validated.

---

## 9. DTO + Mapper Pattern

**Location:**
- `src/main/java/co/clinic/appointment/dto/`
- `src/main/java/co/clinic/appointment/mapper/`

### What it is
Separates what the API exposes from what the database stores. Mapper classes translate between the two representations.

### How it is used here

```
Request DTOs  ── what arrives from the client ──────► Service
    AppointmentRequest
    PatientRequest
    UpdateNotesRequest

Response DTOs ── what leaves to the client ◄────────  Service
    AppointmentResponse
    PatientResponse
    PageResponse<T>

Entities      ── what is stored in MongoDB ──────────  Repository
    Appointment
    Patient
    Practitioner
```

Mapper classes perform the conversion:

```java
// AppointmentMapper.java
@Component
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequest request) {
        Appointment a = new Appointment();
        a.setPatientId(request.getPatientId());
        a.setStartTime(request.getStartTime());
        a.setStatus(AppointmentStatus.BOOKED);
        // ...
        return a;
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .status(appointment.getStatus())
                // ...
                .build();
    }
}
```

### Why it was used
Database entity fields (e.g., `version`, `createdAt`, `updatedAt`) are never accidentally exposed or overwritten by the client. API shape can change without touching the entity, and vice versa.

---

## 10. Singleton Pattern (via Spring IoC)

**Location:** All `@Service`, `@Component`, `@Repository` classes.

### What it is
One instance of a class is created and reused everywhere. Spring's IoC container manages this lifecycle.

### How it is used here
Every service, mapper, filter, factory, and repository is annotated with a Spring stereotype annotation. Spring creates exactly one instance of each and injects it wherever it is needed via constructor injection:

```java
// AppointmentService.java
@Service
public class AppointmentService {

    // Spring injects the single shared instance of each dependency
    public AppointmentService(AppointmentRepository appointmentRepository,
                               PatientRepository patientRepository,
                               PractitionerService practitionerService,
                               AppointmentMapper appointmentMapper,
                               AuditService auditService,
                               AppointmentHistoryService historyService,
                               NotificationStrategyFactory notificationFactory) { ... }
}
```

Constructor injection (rather than `@Autowired` on fields) is used throughout — this makes dependencies explicit and makes the classes independently testable without a Spring context.

### Why it was used
Services are stateless and safe to share. Creating a new instance per request would waste memory and prevent proper dependency management.

---

## Summary

| # | Pattern | Location | Purpose |
|---|---|---|---|
| 1 | **Strategy** | `notification/` | Swap notification channels without changing callers |
| 2 | **Factory** | `NotificationStrategyFactory` | Auto-discover strategies, dispatch to all channels |
| 3 | **Builder** | `SuccessResponse`, `NotificationEvent`, `*Response` DTOs | Immutable objects, readable step-by-step construction |
| 4 | **Template Method** | `BaseEntity → Person → Patient / Practitioner` | Share common fields, enforce subclass contract |
| 5 | **Template Method** | `BaseException → DataNotFoundException etc.` | Share error structure, one handler covers all types |
| 6 | **Static Factory / Utility** | `SuccessResponseHandler`, `ResponseCode` | Non-instantiable helpers, centralised response building |
| 7 | **Repository** | `*Repository` interfaces | Decouple business logic from database queries |
| 8 | **Chain of Responsibility** | `ApiKeyFilter` | Gate requests at the filter level before controllers run |
| 9 | **DTO + Mapper** | `dto/`, `mapper/` | Decouple API contract from database entity structure |
| 10 | **Singleton** | All `@Service`, `@Component`, `@Repository` | One shared, stateless instance per class via Spring IoC |
