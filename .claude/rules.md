# Claude Rules for ai-master

This file provides Claude-specific rules and guidelines derived from the .cursor/rules directory.

## Project Overview

This is a Java-based Spring Boot application implementing order and appeal management using Domain-Driven Design (DDD) principles.

## Core Guidelines

### 1. Architecture & Design
- **Follow DDD principles** with clean hexagonal architecture
- **Use layered architecture**: API → Service → Application → Domain → Infrastructure
- **Apply SOLID principles** and maintain low coupling/high cohesion
- **Use CQRS pattern** for command/query separation

### 2. Module Structure
```
ai-master/                          # Root directory
├── ai-master-api/                  # API interfaces
├── ai-master-service/              # Service adapters
├── ai-master-application/          # Use cases
├── ai-master-domain/              # Business logic
├── ai-master-infrastructure/      # Persistence layer
├── ai-master-common/              # Shared utilities
├── ai-master-boot/                # Spring Boot application
└── ai-master-client/              # External APIs
```

### 3. Java Coding Standards
- **Java 17+ features** preferred
- **Spring Boot** with auto-configuration
- **Bean Validation** (@Valid annotations)
- **Lombok @Data** for POJOs
- **MapStruct** for object mapping
- **MyBatis** with tk.mybatis for database access
- **JUnit 4** for testing
- **Maven** for build management

### 4. Package Structure
```
com.ai.master.{aggregate}.{layer}
├── api/              # API interfaces
├── service/          # Service implementations
├── application/      # Use case orchestration
├── domain/           # Business logic
├── infrastructure/   # Persistence
├── common/           # Shared utilities
└── boot/             # Application startup
```

### 5. Naming Conventions
- **Classes**: PascalCase (e.g., OrderService, OrderCreateCommand)
- **Methods**: camelCase (e.g., createOrder, validateOrder)
- **Packages**: lowercase (e.g., com.ai.master.order.domain)
- **Constants**: UPPER_CASE with underscores

### 6. Git Commit Standards
Follow Conventional Commits format:
```
type(scope): subject

body

footer
```

**Types**:
- `feature`: New functionality
- `fix`: Bug fixes
- `docs`: Documentation changes
- `refactor`: Code restructuring
- `test`: Test additions/updates
- `chore`: Build/tooling changes

### 7. Quality Gates
- **Unit test coverage**: ≥80%
- **Code duplication**: ≤5%
- **Critical vulnerabilities**: 0
- **Compilation warnings**: 0
- **Code style violations**: ≤10

### 8. Testing Requirements
- Use JUnit 4 for unit tests
- Mock external dependencies
- Include integration tests with @SpringBootTest
- Test both positive and negative scenarios
- Ensure business rules are properly tested

### 9. Security Guidelines
- Validate all inputs
- Use parameterized queries
- Implement proper authentication/authorization
- Encrypt sensitive data
- Follow OWASP security practices

### 10. Documentation
- Use Swagger/OpenAPI for API documentation
- Include JavaDoc for public methods
- Document design decisions
- Update README.md for setup instructions