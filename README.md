# Transport Company Management System

A comprehensive Spring Boot-based REST API for managing a transport company's operations, including vehicle management, trip scheduling, staff management, booking system, and user authentication.

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Project Structure](#project-structure)
3. [Tech Stack](#tech-stack)
4. [Key Features](#key-features)
5. [Major Components](#major-components)
6. [Architecture](#architecture)
7. [Getting Started](#getting-started)
8. [API Endpoints](#api-endpoints)
9. [Database Models](#database-models)
10. [Configuration](#configuration)
11. [Security](#security)
12. [Installation & Setup](#installation--setup)

---

## 🎯 Overview

Transport Company Management System is a full-featured backend API designed to streamline transport company operations. It provides endpoints for managing vehicles, scheduling trips, booking seats, managing staff, and authenticating users. The system supports role-based access control with multiple user types (ADMIN, MANAGER, DRIVER, TICKETER) and implements JWT-based authentication with refresh token rotation.

---

## 📁 Project Structure

```
transport-company-project/
├── pom.xml                                  # Maven build configuration
├── mvnw & mvnw.cmd                          # Maven wrapper scripts
├── README.md                                # Documentation
├── .gitignore & .gitattributes              # Git configuration
└── src/
    └── main/java/com/example/transport/
        ├── TransportApplication.java        # Main Spring Boot entry point
        ├── config/                          # Configuration classes
        │   ├── AppConfig.java               # Application configuration
        │   ├── RedisConfig.java             # Redis configuration
        │   ├── SecurityConfig.java          # Spring Security setup
        │   ├── JwtAuthenticationFilter.java  # JWT authentication filter
        │   └── CorsConfig.java              # CORS configuration
        ├── controller/                      # REST API controllers
        │   ├── AuthController.java          # Authentication endpoints
        │   ├── UserController.java          # User management
        │   ├── TripController.java          # Trip management
        │   ├── BookingController.java       # Booking management
        │   ├── VehicleController.java       # Vehicle management
        │   ├── StaffController.java         # Staff management
        │   └── VerificationController.java  # Email/SMS verification
        ├── service/                         # Business logic layer
        │   ├── AuthService.java             # Authentication logic
        │   ├── UserService.java             # User operations
        │   ├── TripService.java             # Trip operations
        │   ├── BookingService.java          # Booking operations
        │   ├── VehicleService.java          # Vehicle operations
        │   ├── StaffService.java            # Staff operations
        │   ├── JwtService.java              # JWT token management
        │   ├── VerificationTokenService.java# Token verification
        │   ├── EmailService.java            # Email operations
        │   ├── RefreshTokenService.java     # Refresh token management
        │   └── CustomUserDetailsService.java# Custom user details provider
        ├── repository/                      # Data access layer (DAL)
        │   ├── UserRepository.java          # User data operations
        │   ├── TripRepository.java          # Trip data operations
        │   ├── BookingRepository.java       # Booking data operations
        │   ├── VehicleRepository.java       # Vehicle data operations
        │   ├── StaffRepository.java         # Staff data operations
        │   ├── RefreshTokenRepository.java  # Refresh token operations
        │   └── VerificationTokenRepository.java# Verification token operations
        ├── model/                           # Entity models
        │   ├── User.java                    # User entity
        │   ├── Trip.java                    # Trip entity
        │   ├── CustomerTrip.java            # Booking entity (CustomerTrip)
        │   ├── Vehicle.java                 # Vehicle entity
        │   ├── Staff.java                   # Staff entity
        │   ├── RefreshToken.java            # Refresh token entity
        │   ├── VerificationToken.java       # Verification token entity
        │   └── BaseEntity.java              # Base entity with audit fields
        ├── dto/                             # Data Transfer Objects
        │   ├── LoginRequestDTO.java         # Login request DTO
        │   ├── LoginResponseDTO.java        # Login response DTO
        │   ├── RegisterRequestDTO.java      # Registration request DTO
        │   ├── UserResponseDTO.java         # User response DTO
        │   ├── UserSummaryDTO.java          # User summary DTO
        │   ├── TripResponseDTO.java         # Trip response DTO
        │   ├── TripSummaryDTO.java          # Trip summary DTO
        │   ├── CreateTripRequestDTO.java    # Create trip request DTO
        │   ├── BookingResponseDTO.java      # Booking response DTO
        │   ├── CreateBookingDTO.java        # Create booking request DTO
        │   ├── VehicleResponseDTO.java      # Vehicle response DTO
        │   ├── VehicleSummaryDTO.java       # Vehicle summary DTO
        │   ├── CreateVehicleRequestDTO.java # Create vehicle request DTO
        │   ├── StaffResponseDTO.java        # Staff response DTO
        │   ├── StaffSummaryDTO.java         # Staff summary DTO
        │   ├── CreateStaffRequestDTO.java   # Create staff request DTO
        │   ├── VerifyCodeRequestDTO.java    # Verification code DTO
        │   └── SendCodeRequestDTO.java      # Send code request DTO
        ├── enums/                           # Enumeration types
        │   ├── RoleType.java                # User role types (ADMIN, MANAGER, DRIVER, TICKETER)
        │   ├── UserType.java                # User types
        │   ├── UserStatus.java              # User status (ACTIVE, INACTIVE)
        │   ├── VehicleType.java             # Vehicle types (SIENNA, HIACE, LUXURY)
        │   ├── VehicleStatus.java           # Vehicle status (ACTIVE, INACTIVE, MAINTENANCE, DELETED)
        │   ├── TripStatus.java              # Trip status (PENDING, CONFIRMED, CANCELLED, COMPLETED)
        │   └── BookingStatus.java           # Booking status (PENDING, CONFIRMED, CANCELLED)
        ├── payload/                         # Response payload wrappers
        │   ├── ApiResponse.java             # Standard API response wrapper
        │   └── PagedResponse.java           # Pagination response wrapper
        ├── util/                            # Utility classes
        │   ├── CookieUtil.java              # Cookie management utilities
        │   ├── TraceIdUtil.java             # Trace ID generation
        │   └── JwtUtil.java                 # JWT utilities
        ├── exception/                       # Custom exception classes
        │   ├── ResourceNotFoundException.java
        │   ├── UnauthorizedException.java
        │   ├── BadRequestException.java
        │   └── GlobalExceptionHandler.java
        └── mapper/                          # MapStruct mappers for DTO conversion
            └── EntityDTOMapper.java
```

---

## 🛠 Tech Stack

### Backend Framework
- **Spring Boot** `4.0.3` - Modern Java-based web framework
- **Spring Data JPA** - ORM for database operations
- **Spring Security** - Authentication and authorization
- **Spring Web** - REST API development
- **Spring Mail** - Email sending functionality
- **Spring Retry** `2.0.12` - Retry mechanism for failed operations
- **Spring AOP** `4.0.0-M2` - Aspect-oriented programming
- **Spring Data Redis** - Redis integration for caching

### Authentication & Authorization
- **JWT (JSON Web Tokens)** `0.11.5` (jjwt-api, jjwt-impl, jjwt-jackson)
- **Spring Security OAuth2 Client** - OAuth 2.0 support
- **Spring Method Security** - Method-level authorization

### Database
- **MySQL** - Primary relational database
- **MySQL Connector Java** - JDBC driver for MySQL
- **JPA/Hibernate** - ORM framework

### Caching & Rate Limiting
- **Redis** - Distributed cache and session management
- **Bucket4j** `8.10.1` - Token bucket algorithm for rate limiting
- **Bucket4j Redis** `8.10.1` - Redis integration for distributed rate limiting

### Data Validation & Mapping
- **Jakarta Bean Validation** - Input validation
- **Jackson DataType JSR310** - Java 8 date/time support
- **MapStruct** `1.6.3` - DTO and entity mapping

### Logging & Utilities
- **Lombok** - Reduces boilerplate code
- **JSpecify** - Nullable annotations for better null-safety
- **Maven Compiler Plugin** - With Lombok annotation processing

### Testing
- **Spring Boot Test Starter** - Testing framework
- **Spring Security Test** - Security testing utilities
- **Spring WebMVC Test** - Web layer testing

### Java Version
- **Java 21** - Latest long-term support version

---

## ✨ Key Features

### 🔐 Authentication & Authorization
- **User Registration** - Sign up with email verification
- **User Login** - Secure login with JWT tokens
- **JWT Token Management** - Access and refresh tokens with cookie-based storage
- **Role-Based Access Control (RBAC)** - Multiple roles (ADMIN, MANAGER, DRIVER, TICKETER)
- **Email Verification** - Verify user email during signup
- **OAuth 2.0 Integration** - Third-party authentication support
- **Token Refresh** - Automatic token rotation with refresh tokens
- **Logout** - Secure session termination with cookie clearing

### 👥 User Management
- **Create, Read, Update, Delete Users** - Full CRUD operations
- **Paginated User Listing** - Efficient data retrieval with pagination
- **User Status Management** - Active/Inactive status tracking
- **User Type Support** - Multiple user types for different purposes
- **Soft Delete** - Logical deletion of user records

### 🚗 Vehicle Management
- **Vehicle Registration** - Register vehicles with type and plate
- **Vehicle Types** - Support for SIENNA, HIACE, LUXURY vehicles
- **Vehicle Status Tracking** - ACTIVE, INACTIVE, MAINTENANCE, DELETED states
- **Driver Assignment** - Assign drivers to vehicles
- **Vehicle Pagination** - List vehicles with filtering and sorting
- **Vehicle Availability** - Check vehicle availability for trips
- **Vehicle Snapshots** - Store vehicle data snapshots for historical tracking

### 🛣 Trip Management
- **Create Trips** - Schedule new trips with routes and pricing
- **Trip Status Tracking** - PENDING, CONFIRMED, CANCELLED, COMPLETED
- **Trip Pricing** - Dynamic pricing per trip
- **Departure & Destination** - Route management
- **Seat Management** - Track booked seats and capacity
- **Passenger Count** - Manage total passengers per trip
- **Trip History** - Store trip snapshots for auditing
- **Optimized Queries** - Efficient trip retrieval with vehicle data

### 📅 Booking Management
- **Create Bookings** - Customers can book seats on trips
- **Booking Status** - PENDING, CONFIRMED, CANCELLED states
- **Seat Reservation** - Reserve multiple seats per booking
- **Booking Cancellation** - Cancel bookings with status updates
- **Booking History** - Maintain booking records
- **Customer Bookings** - Retrieve bookings per customer
- **Booking Notifications** - Email notifications for bookings

### 👨‍💼 Staff Management
- **Create Staff** - Register employees with role assignments
- **Staff Roles** - DRIVER, TICKETER, MANAGER, ADMIN
- **Staff Details** - NIN, guarantor info, bank details
- **Salary Management** - Track employee salaries
- **Staff Status** - ACTIVE/INACTIVE tracking
- **Driver Assignment** - Assign drivers to vehicles
- **Staff Pagination** - Manage large staff lists

### 📧 Email & Notifications
- **Email Verification** - Send verification codes
- **Booking Confirmations** - Email booking details
- **Customer Sign-up Notifications** - Welcome emails
- **Async Email Sending** - Non-blocking email operations
- **JavaMail Integration** - Standard email functionality

### ⚡ Performance & Scalability
- **Redis Caching** - Distributed caching layer
- **Rate Limiting** - API rate limiting with Bucket4j
- **Pagination** - Handle large datasets efficiently
- **Entity Graphs** - Optimized lazy loading
- **Async Processing** - Asynchronous task execution
- **Retry Mechanism** - Automatic retry for failed operations
- **Connection Pooling** - Efficient database connections

### 🔍 Data Management
- **Soft Deletes** - Logical deletion instead of physical
- **JPA Auditing** - Automatic timestamp tracking (createdAt, updatedAt)
- **Version Control** - Optimistic locking for concurrent updates
- **Query Optimization** - Efficient database queries

### 📊 API Features
- **Standardized Responses** - Consistent API response format
- **Pagination** - Page-based data retrieval
- **Sorting** - Sort by multiple fields
- **Trace IDs** - Request tracing for debugging
- **CORS Support** - Cross-Origin Resource Sharing
- **HTTP Status Codes** - Proper REST conventions

---

## 🏗 Architecture

### Layered Architecture
```
┌─────────────────────────────────────┐
│   REST Controllers (HTTP Layer)     │
├─────────────────────────────────────┤
│   Service Layer (Business Logic)    │
├─────────────────────────────────────┤
│   Repository Layer (Data Access)    │
├─────────────────────────────────────┤
│   Database (MySQL)                  │
└─────────────────────────────────────┘
```

### Components

**Controller Layer**
- Handles HTTP requests/responses
- Request validation
- Authorization checks
- Response formatting

**Service Layer**
- Business logic implementation
- Transaction management
- Email notifications
- JWT token operations

**Repository Layer**
- Database operations
- Custom queries with JPA
- Entity graph optimization
- Pagination support

**Security Layer**
- JWT authentication filter
- Method-level authorization
- Cookie-based token storage
- OAuth 2.0 integration

---

## 🚀 Getting Started

### Prerequisites
- **Java 21** or higher
- **Maven 3.6+** or use included `mvnw`
- **MySQL 8.0+**
- **Redis** (optional, for caching and rate limiting)

### Installation & Setup

#### 1. Clone Repository
```bash
git clone https://github.com/Gideon0123/Transport-company-project.git
cd Transport-company-project
```

#### 2. Configure Database
Update `application.properties` or `application.yml`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/transport_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

#### 3. Configure Email (Optional)
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

#### 4. Configure Redis (Optional)
```properties
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=
```

#### 5. Build Project
```bash
./mvnw clean package
# or
mvn clean package
```

#### 6. Run Application
```bash
./mvnw spring-boot:run
# or
java -jar target/transport-0.0.1-SNAPSHOT.jar
```

Application will start on `http://localhost:8080`

---

## 📡 API Endpoints

### Authentication Endpoints (`/api/v1/auth`)
```
POST   /register              - Register new user
POST   /login                 - Login user
POST   /refresh               - Refresh access token
POST   /logout                - Logout user
POST   /send-code             - Send verification code
POST   /verify-code           - Verify code
```

### User Endpoints (`/api/v1/users`)
```
GET    /                      - Get all users (paginated)
GET    /{id}                  - Get user by ID
PUT    /{id}                  - Update user
DELETE /{id}                  - Delete user
```

### Trip Endpoints (`/api/v1/trips`)
```
POST   /                      - Create trip
GET    /                      - Get all trips (paginated)
GET    /{id}                  - Get trip by ID
PUT    /{id}                  - Update trip
DELETE /{id}                  - Delete trip
```

### Booking Endpoints (`/api/v1/bookings`)
```
POST   /                      - Create booking
GET    /                      - Get all bookings (paginated)
GET    /{id}                  - Get booking by ID
PUT    /{id}/cancel           - Cancel booking
```

### Vehicle Endpoints (`/api/v1/vehicles`)
```
POST   /                      - Create vehicle
GET    /                      - Get all vehicles (paginated)
GET    /{id}                  - Get vehicle by ID
PUT    /{id}                  - Update vehicle
DELETE /{id}                  - Delete vehicle
```

### Staff Endpoints (`/api/v1/staffs`)
```
POST   /                      - Create staff
GET    /                      - Get all staff (paginated)
GET    /{id}                  - Get staff by ID
PUT    /{id}                  - Update staff
DELETE /{id}                  - Delete staff
```

---

## 🗄 Database Models

### Entity Relationships
```
User (1) ─────── (1) Staff
 │
 └─── (1 to Many) ─── CustomerTrip

Vehicle (1) ─────── (Many) Trip
           (1) ←───── (1) Staff (Driver)

Trip (1) ─────── (Many) CustomerTrip

RefreshToken ─────── (Many to 1) User
VerificationToken ─── (No FK)
BaseEntity ─────── (Parent) All entities
```

### Core Models

**User**
- userId, firstName, lastName, email, phoneNo
- userType, roleType, status
- password, deleted flag
- Relationships: Staff (1-to-1), CustomerTrip (1-to-many)

**Trip**
- tripId, vehicle, price, bookingDate, departureDateTime
- departureLocation, destinationLocation, totalNoOfPassengers
- vehiclePlateSnapshot, vehicleTypeSnapshot
- status (PENDING, CONFIRMED, CANCELLED, COMPLETED), deleted flag
- Relationships: Vehicle (many-to-1), CustomerTrip (1-to-many)

**Vehicle**
- vehicleId, vehiclePlate, vehicleType, driver
- status (ACTIVE, INACTIVE, MAINTENANCE, DELETED), deleted flag
- Relationships: Staff (1-to-1), Trip (1-to-many)

**Staff**
- staffId, user, roleType, salary, status
- NIN, guarantor details, bank details
- Relationships: User (1-to-1), Vehicle (1-to-1)

**CustomerTrip** (Booking)
- bookingId, numberOfSeats, totalPrice, status
- Relationships: User (many-to-1), Trip (many-to-1)

**RefreshToken**
- refreshId, token, user, expiryDate, revoked flag

**VerificationToken**
- id, code, email, phone, expiryDate, used flag, attempts, lastSentAt

**BaseEntity** (Abstract)
- createdAt, updatedAt (auto-managed by JPA Auditing)

---

## 🔐 Security

### Authentication Flow
1. User registers/logs in with credentials
2. System validates and generates JWT tokens
3. Access token stored in HTTP-only cookie
4. Refresh token stored in scoped cookie
5. Each request validated using JWT filter
6. Token can be refreshed before expiry

### Authorization
- Method-level security with `@PreAuthorize`
- Role-based access control (RBAC)
- Resource-level permissions
- Soft delete prevents access to deleted resources

### Token Management
- **Access Token**: 15 minutes expiry
- **Refresh Token**: 7 days expiry
- **HTTP-Only Cookies**: Prevent XSS attacks
- **Secure Flag**: HTTPS only (configurable)
- **SameSite**: CSRF protection

### Password Security
- BCrypt hashing with strength 12
- Salted passwords
- No plaintext storage

### CORS Configuration
- Configurable allowed origins
- Support for credentials
- Method and header restrictions

---

## 📝 Configuration

### Application Properties
```properties
# Server
server.port=8080
server.servlet.context-path=/
spring.application.name=transport

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/transport_db
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.open-in-view=false

# Redis
spring.redis.host=localhost
spring.redis.port=6379

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=email@gmail.com
spring.mail.password=app_password

# JWT
jwt.secret=your_secret_key
jwt.expiration=900000

# Async
spring.task.execution.pool.core-size=2
spring.task.execution.pool.max-size=5

# Scheduling
spring.task.scheduling.pool.size=1
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is open source and available under the MIT License.

---

## 👨‍💻 Author

**Gideon0123** - [GitHub Profile](https://github.com/Gideon0123)

---

## 📞 Support

For support, email support@transportcompany.com or open an issue on GitHub.

---

## 🗺 Roadmap

- [ ] Payment integration (Stripe, PayPal)
- [ ] SMS notifications
- [ ] Real-time GPS tracking
- [ ] Mobile app (Android/iOS)
- [ ] Advanced analytics dashboard
- [ ] Multi-language support
- [ ] Two-factor authentication
- [ ] WebSocket support for live updates

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Guide](https://spring.io/guides/gs/securing-web/)
- [JWT Tutorial](https://jwt.io/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Redis Documentation](https://redis.io/documentation)
- [MapStruct Guide](https://mapstruct.org/)

---

**Last Updated**: May 2026
**Version**: 0.0.1-SNAPSHOT
**Java Version**: 21
**Build Tool**: Maven
