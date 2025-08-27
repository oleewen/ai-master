# Claude Code Guidelines

This directory contains Claude-specific rules and guidelines derived from the project's `.cursor/rules` directory.

## Files Overview

| File | Purpose |
|------|---------|
| `rules.md` | General project rules and architecture |
| `java-guidelines.md` | Java-specific coding standards |
| `project-structure.md` | Project structure standards |
| `git-guidelines.md` | Git commit and workflow guidelines |
| `testing-guidelines.md` | Testing standards and practices |

## Quick Start

### 1. Project Structure
```
ai-master/
├── ai-master-api/           # API interfaces
├── ai-master-service/       # Service adapters
├── ai-master-application/   # Use cases
├── ai-master-domain/        # Business logic
├── ai-master-infrastructure/ # Persistence layer
├── ai-master-common/        # Shared utilities
├── ai-master-boot/          # Spring Boot application
└── ai-master-client/        # External APIs
```

### 2. Key Commands
```bash
# Build the project
mvn clean install

# Run tests
mvn test

# Start application
mvn spring-boot:run -pl ai-master-boot

# Check code quality
mvn checkstyle:check
mvn spotbugs:check
```

### 3. Development Workflow
1. **Create feature branch**: `git checkout -b feature/order-cancellation`
2. **Write tests first** (TDD approach)
3. **Follow DDD principles** in package structure
4. **Use conventional commits** for git messages
5. **Ensure quality gates** are met before PR

### 4. Architecture Principles
- **Domain-Driven Design (DDD)**
- **Clean Architecture**
- **CQRS Pattern**
- **Repository Pattern**
- **Factory Pattern**
- **Value Objects**

### 5. Quality Standards
- **Test coverage**: ≥80%
- **Code duplication**: ≤5%
- **Java 17+** features
- **Spring Boot 2.7.10**
- **MyBatis** for database access
- **Maven** for build

## Guidelines Summary

- **Always** follow the layered architecture
- **Never** expose domain objects in API layer
- **Use** MapStruct for object mapping
- **Implement** comprehensive testing
- **Follow** conventional commit messages
- **Maintain** high code quality standards

For detailed guidelines, refer to the individual markdown files in this directory.