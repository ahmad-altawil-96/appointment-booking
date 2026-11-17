# Appointment Booking System

A REST API for booking appointments at a medical practice, built with Spring Boot.
Patients book appointments, doctors manage their working hours, and an admin manages users.

## Tech Stack

- Java 21, Spring Boot
- Spring Data JPA (Hibernate), PostgreSQL
- Spring Security with JWT
- Gradle

## Features

- Registration and login with JWT authentication
- Three roles with separate access: `PATIENT`, `DOCTOR`, `ADMIN`
- Doctors define weekly working hours, slot length and breaks
- Appointment slots are generated automatically from the working hours, without breaks
- Custom availability or days off for specific dates
- Booking checks that the slot is valid and not already booked
- Cancellation rules (no cancelling past or already cancelled appointments)
- Admin can list users, change roles and delete users

## How It Works

1. A new user registers and gets the role `PATIENT`.
2. The admin changes the role of a user to `DOCTOR`.
3. The doctor creates a weekly schedule (working hours, slot length, break).
4. The patient sees the free slots of a doctor and books one.

## Project Structure

```
src/main/java/com/example/Appointment_Booking
├── controller   # REST endpoints (auth, patient, doctor, admin)
├── dto          # Request and response objects
├── exception    # Global exception handler
├── model        # JPA entities and enums
├── Repository   # Spring Data repositories
├── Security     # JWT, security config, admin setup
└── Service      # Business logic
```

## Getting Started

### Requirements

- Java 21
- PostgreSQL

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/ahmad-altawil-96/appointment-booking.git
   cd appointment-booking
   ```
2. Create a PostgreSQL database.
3. Create the file `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/appointment_booking
   spring.datasource.username=YOUR_USERNAME
   spring.datasource.password=YOUR_PASSWORD
   spring.jpa.hibernate.ddl-auto=update

   jwt.secret=YOUR_SECRET_KEY_AT_LEAST_32_CHARACTERS

   admin.email=admin@clinic.com
   admin.password=YOUR_ADMIN_PASSWORD
   ```
   The admin account is created automatically on the first start.
4. Start the application:
   ```bash
   ./gradlew bootRun
   ```

The API runs on `http://localhost:8080`.

## Endpoints

Send the token in the header of protected requests:

```
Authorization: Bearer <token>
```

### Auth (public)

| Method | Endpoint         | Description            |
|--------|------------------|------------------------|
| POST   | `/auth/register` | Register as a patient  |
| POST   | `/auth/login`    | Log in and get a JWT   |

### Patient

| Method | Endpoint                                   | Description                   |
|--------|--------------------------------------------|-------------------------------|
| GET    | `/patient/doctors`                         | List all doctors              |
| GET    | `/patient/slots/{doctorId}`                | Show free slots of a doctor   |
| POST   | `/patient/book`                            | Book an appointment           |
| DELETE | `/patient/cancel/{doctorId}/{appointmentId}` | Cancel an appointment       |
| GET    | `/patient/appointment`                     | Show my appointments          |

### Doctor

| Method | Endpoint                             | Description                          |
|--------|--------------------------------------|--------------------------------------|
| GET    | `/doctor/schedule`                   | Show my weekly schedule              |
| POST   | `/doctor/schedule/create`            | Create working hours for a weekday   |
| POST   | `/doctor/availability/create`        | Add a custom day or a day off        |
| GET    | `/doctor/slots/available`            | Show my free slots                   |
| GET    | `/doctor/slots/booked`               | Show booked appointments (filter by status) |
| POST   | `/doctor/slots/add`                  | Add an appointment for a patient     |
| PUT    | `/doctor/slots/update/{appointmentId}` | Update an appointment              |
| DELETE | `/doctor/slots/delete/{appointmentId}` | Cancel an appointment              |

### Admin

| Method | Endpoint          | Description                        |
|--------|-------------------|------------------------------------|
| GET    | `/admin/userlist` | List users (optional filter by role) |
| PUT    | `/admin/update`   | Change the role of a user          |
| DELETE | `/admin/delete`   | Delete a user                      |

Example requests are in `src/test/test.http`.

## Planned

- Custom exceptions with correct HTTP status codes
- Unit tests for slot generation and booking
- Swagger / OpenAPI documentation
