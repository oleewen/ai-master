# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java-based Spring Boot application implementing an order and appeal management system using Domain-Driven Design (DDD) principles. The system handles order lifecycle management and appeal processing with a clean hexagonal architecture.

## Architecture & Structure

### Domain-Driven Design Layers
- **API Layer**: User interface contracts (ai-master-api)
- **Service Layer**: Web controllers and RPC providers (ai-master-service)
- **Application Layer**: Use case orchestration (ai-master-application)
- **Domain Layer**: Business logic and models (ai-master-domain)
- **Infrastructure Layer**: Persistence and external integrations (ai-master-infrastructure)
- **Boot Layer**: Spring Boot application startup (ai-master-boot)

### Key Aggregates
- **Order Aggregate**: Manages order lifecycle with state machine pattern
- **Appeal Aggregate**: Handles customer appeals and dispute resolution
- **Goods Aggregate**: Product catalog and pricing
- **Inventory Aggregate**: Stock management

### State Machine Patterns
- **Order State Machine**: Created → Ordered → Paid → Deliveried → Received → Finished/Cancelled
- **Appeal State Machine**: Created → Verified → Submitted → Auditing → Completed/Cancelled

## Development Commands

### Build & Package
```bash
# Full project build
mvn clean install

# Skip tests during build
mvn clean install -DskipTests

# Build specific module
mvn clean install -pl ai-master-boot -am
```

### Testing
```bash
# Run all tests
mvn test

# Run tests for specific module
mvn test -pl ai-master-domain

# Run single test class
mvn test -Dtest=OrderDomainServiceTest

# Run specific test method
mvn test -Dtest=OrderDomainServiceTest#testCreateOrder

# Run with debug
mvn -Dmaven.surefire.debug test
```

### Development Server
```bash
# Start application (default dev profile)
mvn spring-boot:run -pl ai-master-boot

# Start with specific profile
mvn spring-boot:run -pl ai-master-boot -Dspring.profiles.active=prod

# Start with custom port
mvn spring-boot:run -pl ai-master-boot -Dserver.port=8083
```

### Database & Configuration
```bash
# Check database configuration
cat ai-master-boot/src/main/resources/application-dev.properties

# Application runs on: http://localhost:8082
# Management endpoint: http://localhost:8888/health
```

### Code Quality
```bash
# Check for compilation issues
mvn compile

# Generate sources (MapStruct, etc.)
mvn generate-sources

# Clean all generated files
mvn clean
```

## Key Implementation Patterns

### Domain Modeling
- **Value Objects**: Id, MonetaryAmount, OrderId, BuyerId, SellerId
- **Aggregate Root**: Order, Appeal with encapsulated state management
- **Factory Pattern**: Static factories for object creation using MapStruct
- **Repository Pattern**: Clean separation between domain and persistence

### API Design
- RESTful endpoints in ai-master-service
- RPC services via Dubbo in ai-master-service
- Input validation using Bean Validation annotations
- Global exception handling with proper HTTP status codes

### Data Access
- MyBatis for ORM with tk.mybatis mapper
- MySQL database with JSON fields for attachments
- Transaction management at application service layer
- Optimistic locking for concurrent updates

## Configuration Files
- **Main Config**: `ai-master-boot/src/main/resources/application.properties`
- **Environment Config**: `ai-master-boot/src/main/resources/application-{env}.properties`
- **Logging**: `ai-master-boot/src/main/resources/logback-boot.xml`
- **Database**: MySQL configuration in dev/prod/test properties

## Module Dependencies
```
ai-master-boot (startup)
├── ai-master-service (web/rpc adapters)
│   ├── ai-master-api (interface contracts)
│   ├── ai-master-application (use cases)
│   └── ai-master-infrastructure (persistence)
├── ai-master-domain (business logic)
├── ai-master-common (shared utilities)
└── ai-master-client (external client APIs)
```

## Project Standards & Guidelines

All development work must follow the guidelines defined in `.claude/` directory:

- **`.claude/rules.md`** - General project rules and architecture
- **`.claude/java-guidelines.md`** - Java coding standards and patterns
- **`.claude/git-guidelines.md`** - Git commit conventions and workflow
- **`.claude/testing-guidelines.md`** - Testing requirements and practices

### Key Constraints
- **Design**: Follow DDD principles with clean hexagonal architecture
- **Coding**: Use Java 17+ features, Spring Boot best practices, proper validation
- **Code Commits**: Follow Conventional Commits format (feature/fix/docs/refactor/test/chore)
- **Project Structure**: Maintain strict DDD layering (API → Service → Application → Domain → Infrastructure)

### Quality Gates
- **Unit Test Coverage**: ≥80%
- **Code Duplication**: ≤5%
- **Critical Issues**: 0
- **Compilation Warnings**: 0
- **Code Style Violations**: ≤10

## Technology Stack
- **Java**: 17
- **Spring Boot**: 2.7.10
- **Database**: MySQL with MyBatis
- **Build**: Maven 3.7.0+
- **Testing**: JUnit 4 with Spring Boot Test
- **Documentation**: Swagger/OpenAPI
- **Validation**: Bean Validation 2.0
- **Object Mapping**: MapStruct 1.5.0+

## Development Workflow
1. **Always** read `.claude/README.md` before starting work
2. **Follow** the specific guidelines for each layer
3. **Write tests first** (TDD approach)
4. **Use conventional commits** for all changes
5. **Ensure quality gates** are met before PR