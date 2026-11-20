# Airline Microservices Application

Backend Airline Management and Booking application built with Spring Boot microservices, event-driven architecture, and cloud-native patterns. The project is organised as
a Maven multi-module application and uses independently deployable
microservices for user management,locations,airline and flight operations,
seats, pricing, ancillary services, bookings, payments, and
notifications.

---

## Table of Contents

1. [Features](#features)
2. [Architecture Overview](#architecture-overview)
3. [Services](#services)
4. [Data Flow](#data-flow)
5. [Technology Stack](#technology-stack)
6. [Design Patterns](#design-patterns)
7. [Security](#security)
8. [API Reference](#api-reference)
9. [Running the Project](#running-the-project)
10. [Configuration](#configuration)
11. [Known Limitations](#known-limitations)

---

## Features

### For Passengers (Booking APIs)

- **Flight Search** — Multi-criteria search with supported filters: route, date, cabin class, airline, price range, departure/arrival time window, maximum duration, and sorting.
- **Seat Selection Support** — Backend APIs for cabin classes, seat maps, seat instances, availability status, and seat pricing.
- **Ancillary Services** — Ancillary and meal pricing integration during booking.
- **Booking Management** — Retrieve booking details, view booking history, and cancel bookings.
- **Payment Processing** — Mock Payment Gateway for payment initiation and verification.
- **Booking Confirmation Email** — HTML email containing flight details, passenger information, ticket numbers, baggage allowance, fare benefits, and payment details.
- **Ticket Management** — Unique ticket number generated for each passenger after successful payment.

### For Airline Administrators

- **Airline Management** — Register airlines and change airline status (ACTIVE, INACTIVE, BANNED).
- **Aircraft Management** — Create, retrieve, update, and delete aircraft associated with an airline.
- **Flight Management** — Create and manage flights.
- **Flight Schedule Management** — Create recurring schedules and generate flight instances for configured operating days.
- **Flight Instance Management** — Manage individual flight instances.
- **Seat Map Management** — Create and manage aircraft seat-map layouts.
- **Cabin Class Management** — Configure cabin classes associated with aircraft.
- **Ancillary Management** — Manage the ancillary data supported by the ancillary service.
- **Pricing Management** — Manage fare information used by flight search and booking.

### For System Administrators
- **Multi-Airline Support** — Airline records are associated with their owners.
- **City & Airport Data** — City and airport CRUD, city search, country lookup, IATA code validation, and airport lookup by city.
---
## Architecture Overview

```
                                    ┌─────────────────────────────────────────────────────┐
                                    │                  CLIENT (Postman / API Client)         │
                                    └───────────────────────────┬─────────────────────────┘
                                                                │ HTTP :5000
                                    ┌───────────────────────────▼─────────────────────────┐
                                    │                    API GATEWAY                      │
                                    │ JWT Auth · Routing Rolebased authorization      │
                                    └──┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬────────┘
                                       │   │   │   │   │   │   │   │   │   │   │
          ┌────────────────────────────┘   │   │   │   │   │   │   │   │   │   └──────────────────┐
          │         ┌──────────────────────┘   │   │   │   │   │   │   │   └─────────────┐        │
          ▼         ▼          ▼               ▼   ▼   ▼   ▼   ▼   ▼   ▼                ▼        ▼
   ┌────────────┐ ┌──────────┐ ┌────────────┐ ...                           ┌───────────┐ ┌──────────────┐
   │    User    │ │ Airline  │ │ Flight-Ops │                               │  Booking  │ │   Payment    │
   │  Service   │ │  Core   │ │  Service  │                               │  Service  │ │   Service    │
   └────────────┘ └──────────┘ └─────┬──────┘                               └─────┬─────┘ └──────┬───────┘
                                     │ Feign                                       │ Feign         │
                                     ├──► Pricing Service                          │               │
                                     ├──► Seat Service                             │               │
                                     └──► Airline Core                             │               │
                                                                                   ▼               ▼
                                                            APACHE KAFKA
                                        ┌─────────────────────────────────────────────────────────────┐
                                        │ flight-instance-created | payment.completed | payment.failed│
                                        │                       booking.confirmed                      │
                                        └──────────────────────────────┬──────────────────────────────┘
                                                                        │
                                                    ┌──────────────────┼─────────────────────┐
                                                    ▼                  ▼                     ▼
                                                Seat Service       Booking Service      Notification Service
                                                Create flight      Confirm booking       Send confirmation
                                                seat instances     Generate tickets      email
                                                Mark seats BOOKED

                     ┌─────────────────────────────────────────────────────────────────────────────────┐
                     │                         INFRASTRUCTURE                                          │
                     │    Eureka (Service Registry) ·  Config Server (Git-backed) · MySQL per service  │
                     └─────────────────────────────────────────────────────────────────────────────────┘
```

---

## Services

### Infrastructure Services

| Service | Port | Role |
|---|---|---|
| **API Gateway** | 5000 (external) | Single entry point — JWT validation, routing and CORS |
| **Config Server** | 8888 | Centralized config pulled from Git for all services |
| **Service Registry** | 8761 | Eureka — service registration and discovery |
| **Kafka** | 9092 | Event streaming (KRaft mode — no Zookeeper) |
| **Redis** | 6379 (optional) | Caching support for services configured to use Redis |
| **MySQL** | varies(3302--3311) | Dedicated database per business service |

### Business Services

| Service | Database | Kafka Role | Responsibility |
|---|---|---|---|
| user-service | airline_user_db | — | Signup, login, JWT and user management |
| airline-core-service | airline_core_db | — | Airline and aircraft management |
| location-service | airline_location_db | — | Cities and airports |
| flight-ops-service | airline_flight_db | Producer | Flights, schedules and flight search |
| seat-service | airline_seat_db | Consumer | Seat maps, cabin classes, seat instances and seat booking updates  |
| pricing-service | airline_pricing_db | — | Fares and baggage policies |
| ancillary-service | airline_ancillary_db | — | Ancillary services and meal management |
| booking-service | airline_booking_db | Consumer + Producer | Bookings, passengers and tickets |
| payment-service | airline_payment_db | Producer | Mock payment processing |
| notification-service | — | Consumer | Booking confirmation emails using Thymeleaf and Gmail SMTP |

### Kafka Producer and Consumer Responsibilities

| Service | Role | Topic | Purpose |
|---|---|---|---|
| flight-ops-service | Producer | flight-instance-created | Publishes event after flight-instance creation |
| seat-service | Consumer | flight-instance-created | Creates flight-specific cabin and seat-instance records |
| payment-service | Producer | payment.completed | Publishes successful payment event |
| payment-service | Producer | payment.failed | Publishes failed payment event |
| booking-service | Consumer | payment.completed | Confirms booking and generates passenger tickets |
| booking-service | Consumer | payment.failed | Updates booking to CANCELLED |
| seat-service | Consumer | payment.completed | Marks selected seat instances as BOOKED |
| booking-service | Producer | booking.confirmed | Publishes confirmed booking details |
| notification-service | Consumer | booking.confirmed | Sends booking confirmation email |

### Consumer Groups

Each consumer service uses its own consumer group.

- booking-service-group
- seat-service-group
- notification-service-group

The separate consumer groups allow Seat and Notification services to consume their respective events independently.

---

## Data Flow

### Flight Search

```
The Flight Ops Service provides a multi-criteria flight search API.

1. Accepts search criteria such as departure airport, arrival airport,
   departure date, passenger count, cabin class, airline, price range,
   departure/arrival time range, and maximum duration.
2. Uses JPA Specification for database-level flight-instance searching.
3. Integrates with Seat Service to resolve cabin-class information.
4. Retrieves fare information from Pricing Service.
5. Applies price-range filtering after fare retrieval.
6. Enriches search results with airline, airport, aircraft, and fare details.
7. Returns paginated flight search results.

Note: Price filtering is performed after the database page is fetched,
so the returned page may contain fewer records than the requested page size.
```

### Booking, Payment and Kafka Event Flow

```
Postman / API Client
       |
       v
API Gateway
       |
       v
Booking Service
       |
       +---- Feign ---> Flight Ops Service
       |                Seat Service
       |                Ancillary Service
       |                Pricing Service
       |
       v
Create Booking (PENDING)
       |
       v
Payment Service
       |
       v
Mock Payment Gateway
       |
       v
Payment Result
       |
       v
Payment Service publishes Kafka event
       |
       +---------------------------+
       |                           |
       v                           v
payment.completed             payment.failed
       |                           |
       v                           v
Booking Service              Booking Service
       |                           |
       v                           v
CONFIRMED                    CANCELLED
Generate tickets
       |
       +---------------------------+
       |
       v
Publish booking.confirmed
       |
       +
       |                           
       v                           
Notification Service         
       |                      
       v
Render HTML Email
       |
       v
Gmail SMTP
       |
       v
Booking Confirmation Email

Separately:

Flight Ops Service
       |
       v
flight-instance-created
       |
       v
Seat Service
       |
       v
Create flight-specific cabin and seat instances

Also:

payment.completed
       |
       v
Seat Service
       |
       v
Mark selected seat instances as BOOKED
```
### Booking Confirmation Email Content

The booking confirmation email is generated using a Thymeleaf HTML template.

It includes:

- **Header** — Airline branding and personalised greeting.
- **Booking Reference** — Unique booking reference and booking date.
- **Flight Details** — Departure/arrival airport codes, city names,
  airport names, dates, times, flight number, duration, cabin class,
  and aircraft model.
- **Passenger Details** — Passenger names, passenger type,
  ticket numbers, and passport numbers.
- **Baggage Allowance** — Cabin and check-in baggage information.
- **Fare Benefits** — Applicable fare benefits and policies.
- **Payment Summary** — Base fare, taxes, seat fees, ancillary fees,
  meal fees, total amount, transaction ID, payment reference,
  payment gateway, and payment date.
- **Travel Information** — Check-in and other travel-related
  information included in the email template.

---

## Technology Stack

### Backend Framework

| Technology | Version | Purpose |
|---|---|---|
| Spring Boot | 4.0.2 | Base application framework |
| Spring Cloud | 2025.1.0 | Microservices toolkit (Config, Eureka, Gateway, Feign) |
| Spring Data JPA | managed | ORM with Criteria API support |
| Spring Security | managed | Authentication and RBAC |
| Spring Kafka | managed | Kafka producer/consumer integration |
| Spring Mail | managed | JavaMailSender (email sending) |
| Thymeleaf | managed | HTML email template rendering |

### Infrastructure

| Technology | Version | Purpose |
|---|---|---|
| Apache Kafka | 4.1.1 | Async event streaming (KRaft — no Zookeeper) |
| MySQL | 8.0 | Primary relational database (one DB per service) |
| Eureka Server | Spring Cloud | Service discovery and registration |
| Spring Cloud Config | Spring Cloud | Git-backed centralised configuration |
| Spring Cloud Gateway | Spring Cloud | API gateway with WebMVC routing |

### Security

| Technology | Purpose |
|---|---|
| JJWT (io.jsonwebtoken) | JWT creation, parsing, and validation |
| BCrypt | Password hashing |

### Payment Gateways

| Gateway | Purpose |
|---|---|
| Mock Payment Gateway | Payments run through a Mock Payment Gateway (no real money, no external keys needed) |

### Notifications

| Technology | Purpose |
|---|---|
| Gmail SMTP (587/TLS) | Email delivery |
| Thymeleaf (HTML) | Responsive HTML email templates with inline CSS |

### Build & Deployment

| Tool | Purpose |
|---|---|
| Maven (multi-module) | Build tool with parent POM for version management |
| Google Jib | Build Docker images via Maven without a Dockerfile |
| Docker Compose | Local container orchestration for microservices and infrastructure |
| Spring Boot DevTools | Live reload during development |
| Lombok | Compile-time boilerplate generation |

---

## Design Patterns

### Microservices Patterns

| Pattern | Where Used |
|---|---|
| **API Gateway** | Single entry point — all traffic routed through `api-gateway` |
| **Service Registry** | Eureka — services register themselves; gateway resolves URLs dynamically |
| **Config Server** | Centralized external configuration loaded by services through Spring Cloud Config |
| **Event-driven workflow** | Booking → Payment → Booking Confirmation → Seat Update and Email Notification |

### Event-Driven Patterns

| Pattern | Where Used |
|---|---|
| Event-Driven Architecture | Payment and booking communication through Kafka |
| Pub/Sub | payment.completed consumed independently by Booking and Seat; booking.confirmed consumed by Notification |
| Producer/Consumer | Payment produces payment events; Booking consumes them |
| Consumer Groups | Independent consumer groups for Booking, Seat and Notification |
| Event Enrichment | Booking gathers required details before publishing booking.confirmed |

### Cross-Cutting Concerns

| Concern | Approach |
|---|---|
| Authentication | JWT validation at API Gateway |
| User Context | User identity and role headers forwarded to backend services |
| Caching | Redis caching in configured services |
| Messaging | Kafka-based asynchronous event communication |
---
## Security

### Roles

| Role | Permissions |
|---|---|
| `ROLE_SYSTEM_ADMIN` | Create/manage airlines, airports, cities; approve/suspend/ban airlines |
| `ROLE_AIRLINE_OWNER` | Manage own airline's flights, schedules, fares, seats, ancillaries |
| `ROLE_CUSTOMER` | Search flights, make bookings, view profile, manage own bookings |

### JWT Authentication Flow

```
1. POST /auth/login  { email, password }
        │
        ▼
   user-service validates credentials
        │
        ▼
Returns JWT token (payload: userId, email, authorities, issued-at, expiry)
        │
        ▼
   Client sends: Authorization: Bearer <token>
        │
        ▼
   API Gateway validates signature + expiry
   Extracts claims → forwards as headers:
     X-User-Id, X-User-Email, X-User-Roles
        │
        ▼
   Backend services trust these headers
   (JWT already verified at gateway)
```
## Running the Project

### Prerequisites

- Java 17+
- Maven 3.9+
- Git
- Docker Desktop
- Docker Compose
- Postman

Kafka, Redis,MySQL and other required infrastructure services are started through Docker Compose.
Separate local installations are not required when running the complete Docker stack.

### Environment Variables

#### Email (Gmail SMTP) Set up
MAIL_USERNAME=your-email@gmail.com

MAIL_APP_PASSWORD=xxxx-xxxx-xxxx-xxxx   # Gmail App Password (not regular password)

##### Step 1 — Enable 2-Step Verification

1. Go to [Google Account Security](https://myaccount.google.com/security).
2. Under **How you sign in to Google**, enable **2-Step Verification**.
3. This is required before you can generate an App Password.

#### Step 2 — Generate an App Password

1. Go to [Google App Passwords](https://myaccount.google.com/apppasswords).
2. Enter an app name, for example: `airline-notification` and Click **Create**.
4. Google will generate a 16-character password, for example: `abcd efgh ijkl mnop`
5. Copy and securely store the generated password. It is displayed only once.
---
### Quick Start (Docker Compose)

```bash
# 1. Clone the repository

    git clone <repo-url>
    cd microservices

# 2. Create a separate Git repository for centralized service configuration and Add the required service YAML configuration files.

    Example: https://github.com/airline-config-server

# 3. Set required environment variables (copy and fill in)

    docker-compose/.env.example docker-compose/.env (with your Gmail address and Gmail App Password.)

# 4. Build all service JARs

    mvn clean package -DskipTests 

Build the service images using Google Jib: 

    mvn jib:dockerBuild

# 5. Start all services , Navigate to the Docker Compose directory:

    cd docker-compose
    docker compose up -d

Import the Postman collection and test the API in sequence. The API is available at `http://localhost:5000`.

### Local Development (individual services)

# Start infrastructure first ,make sure install kakfka and redis sofatwares and also update db credentials in config repo .yml files.

# Then start individual services (# Startup Order: Service Registry → Config Server → Individual Services (User → Location → Airline Core → Flight Ops → Pricing → Seat → Ancillary → Booking → Payment → Notification) → API Gateway).

    cd services/service-registry
    mvn spring-boot:run
```

## Project Structure

```
microservices/
├── pom.xml                          # Root parent POM — version management
├── common-lib/                      # Shared DTOs, enums, events, exceptions
│   └── src/main/java/com/airline/common_lib/
│       ├── dto/                     # Shared data transfer objects
│       ├── embeddable/              # JPA embeddables (ContactInfo, Address, GeoCode)
│       ├── enums/                   # All shared enums (BookingStatus, CabinClassType, etc.)
│       ├── event/                   # Kafka event objects (BookingConfirmedEvent, etc.)
│       └── payload/
│           ├── request/             # Inbound request DTOs
│           └── response/            # Outbound response DTOs
│
├── cloud/                           # Infrastructure services
│   ├── api-gateway/                 # Spring Cloud Gateway — routing + JWT
│   ├── config-server/               # Spring Cloud Config Server
│   └── service-registry/            # Eureka Server
│
├── services/                        # Business microservices
│   ├── user-service/
│   ├── airline-core-service/
│   ├── location-service/
│   ├── flight-ops-service/
│   ├── seat-service/
│   ├── pricing-service/
│   ├── ancillary-service/
│   ├── booking-service/
│   ├── payment-service/
│   ├── notification-service/
│   
│
└── docker-compose/
    ├── docker-compose.yml
    └── .env
```
## Known Limitations

- **Mock Payment Gateway:** Payment processing is simulated. No real payment gateway is integrated.
- **Backend Only:** The project currently exposes REST APIs and does not include a frontend application.
- **Seat Reservation:** Seats are marked BOOKED after successful payment. A temporary seat-hold mechanism before payment is not implemented.
- **Notification:** Booking confirmation is sent through email. SMS notification is not implemented.
- **Authorization:** Ownership validation and endpoint-level authorization need further improvements in some services.
- **Kafka Reliability:** Consumer idempotency and retry/dead-letter handling require further improvements.
- **Booking Updates:** Booking modification functionality is not fully implemented.
---