# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Development Commands

### Build and Run
- **Build**: `mvn clean compile` - Clean and compile the project
- **Run**: `mvn spring-boot:run` - Start the Spring Boot application on port 8080
- **Package**: `mvn clean package` - Create JAR file in target/ directory

### Testing
- **Run all tests**: `mvn test`
- **Run specific test**: `mvn test -Dtest=OrderServiceImplTest`
- **Run integration tests**: `mvn test -Dtest=*IntegrationTest`
- **Run with specific profile**: `mvn test -Dspring.profiles.active=test`

### Database Access
- **H2 Console** (dev profile): http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:devdb`
  - Username: `sa`, Password: (empty)

## Architecture Overview

### Package Structure
- `com.example.spring` - Main Spring Boot application
- `com.example.ioc` - IoC (Dependency Injection) examples
- `com.example.patten.strategy` - Strategy pattern implementations

### Core Components

#### Spring Configuration
- **Main Application**: `SpringBookstoreApplication.java` - Entry point with AOP and configuration properties enabled
- **Configuration**: `config/BookstoreConfig.java` - Profile-based configuration (dev/prod/test)
- **Properties**: `config/BookstoreProperties.java` - Custom configuration properties
- **Async/Cache**: `AsyncConfig.java`, `CacheConfig.java` - Additional Spring features

#### AOP Configuration
- **LoggingAspect**: `aop/LoggingAspect.java` - Global logging for controllers and services
  - Controller logging: `[CONTROLLER] ClassName.method 시작/완료 - 실행시간: Xms`
  - Service logging: `[SERVICE] ClassName.method 시작/완료 - 실행시간: Xms`

#### Domain Model
- **Entities**: `entity/` - JPA entities (Book, Member, Order, OrderItem, Loan)
- **Enums**: `MembershipType`, `OrderStatus` - Type safety for domain concepts
- **Exceptions**: `exception/BusinessException.java` - Base business exception class

#### Data Layer
- **Repositories**: Interface-based with JPA implementations
  - Custom interfaces: `BookRepository`, `OrderRepository`, etc.
  - JPA implementations: `impl/JpaBookRepository`, `impl/JpaOrderRepository`, etc.
- **Database**: H2 (dev/test), MySQL (prod)

#### Service Layer
- **Services**: Business logic with Spring transaction management
  - `BookService`/`BookServiceImpl` - Book management
  - `OrderService`/`OrderServiceImpl` - Order processing with status management
  - `MemberService`/`MemberServiceImpl` - Member management
  - `EmailService`, `LoggingService` - Cross-cutting concerns
- **Events**: Spring event system for decoupled components

#### Controller Layer (REST API)
- **BookController**: Complete CRUD operations with pagination, search, and statistics
- **OrderController**: Order management with status workflow, statistics, and reporting
  - Order lifecycle: PENDING → CONFIRMED → SHIPPED → DELIVERED
  - Order cancellation (except DELIVERED status)
  - Advanced queries: status-based, period-based, top-selling books
- **MemberController**: Member registration, profile management, and validation

### Testing Strategy

#### Unit Tests
- **Mockito**: Extensive use of `@Mock`, `@InjectMocks` for isolated testing
- **AssertJ**: Fluent assertions for better test readability
- **Test Data**: `TestDataBuilder` pattern for creating test fixtures

#### Integration Tests
- **Testcontainers**: MySQL container testing for production-like scenarios
- **Spring Boot Test**: `@SpringBootTest` with different profiles
- **Test Configuration**: `config/TestDataConfig.java` for shared test data
- **AOP Integration Tests**: `AopLoggingIntegrationTest.java` - Tests AOP logging functionality

### Profile Configuration
- **dev**: H2 in-memory database, console enabled, detailed logging
- **test**: H2 in-memory database, fast startup
- **prod**: MySQL database, optimized settings

### Key Patterns
- **Repository Pattern**: Abstracted data access with JPA implementations
- **Strategy Pattern**: Demonstrated in `patten.strategy` package
- **Builder Pattern**: Extensive use with Lombok for entity creation
- **Event-Driven**: Spring Events for loose coupling
- **Dependency Injection**: Constructor injection with `@RequiredArgsConstructor`
- **AOP Pattern**: Cross-cutting concerns with AspectJ

### Technology Stack
- **Framework**: Spring Boot 3.5.3, Java 21
- **ORM**: Spring Data JPA with Hibernate
- **AOP**: Spring AOP with AspectJ
- **Testing**: JUnit 5, Mockito, AssertJ, Testcontainers
- **Database**: H2 (development), MySQL (production)
- **Build Tool**: Maven
- **Code Generation**: Lombok for reducing boilerplate

## Development Notes

### Running Tests
Always use the test profile to ensure proper database configuration:
```bash
mvn test -Dspring.profiles.active=test
```

### Database Initialization
The application uses `data.sql` for initial data loading. The `defer-datasource-initialization: true` setting is critical for proper startup sequence.

### Transaction Management
Service methods use `@Transactional` extensively. Read-only operations are marked with `@Transactional(readOnly = true)` for optimization.

### AOP Logging
The LoggingAspect automatically logs method entry, exit, and execution times for:
- All controller methods: `execution(* com.example.spring.controller.*.*(..))`
- All service implementation methods: `execution(* com.example.spring.service.impl.*.*(..))`

Logs include method parameters, execution time, and error details if exceptions occur.
