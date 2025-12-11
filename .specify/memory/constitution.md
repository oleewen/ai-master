<!--
Sync Impact Report:
Version change: 1.0.0 → 2.0.0 (Major version: Significant expansion with comprehensive rule integration)
Modified Principles:
- I. Domain-Driven Design → I. Domain-Driven Design (NON-NEGOTIABLE) [expanded with governance details]
- II. Hexagonal Architecture → II. Hexagonal Architecture (NON-NEGOTIABLE) [module structure clarified]
- III. Test-First Development → III. Test-First Development (NON-NEGOTIABLE) [quality metrics specified]
- IV. Rich Domain Models → IV. Rich Domain Models (NON-NEGOTIABLE) [state machine details]
- V. Contract-First API Design → V. Contract-First API Design [versioning clarified]
- VI. Infrastructure Implementation Pattern → VI. Infrastructure Implementation Pattern [transaction boundaries]
- VII. Code Quality Standards → VII. Code Quality Standards [metrics and limits added]

Added Sections:
- VIII. Java Version and Technology Stack (NON-NEGOTIABLE)
- IX. Quality Gates and Metrics (ZERO-TOLERANCE)
- X. Exception Handling Standards
- XI. Documentation Standards
- XII. Versioning and Dependency Management

Removed Sections: None
Templates requiring updates:
  ⚠️ .specify/templates/plan-template.md (Constitution Check gates expanded)
  ⚠️ .specify/templates/spec-template.md (Need technology stack section)
  ⚠️ .specify/templates/tasks-template.md (New task categories for quality gates)
Follow-up TODOs:
  TODO(2025-12-10): Align checkstyle rules with section VII metrics
  TODO(2025-12-10): Create Git workflow section in plan-template.md
  TODO(2025-12-10): Verify JUnit 4/5 version consistency across project
-->

# AI-Master Constitution

## Core Principles

### I. Domain-Driven Design (NON-NEGOTIABLE)
Every feature must align with domain boundaries and business language. Business processes mapped to bounded contexts: Appeal (申诉), Order (订单), Inventory (库存), Goods (商品). Aggregates serve as consistency boundaries, encapsulating business rules and maintaining invariants. Domain models must be rich with behavior, not anemic data structures.

**State Machine Governance**: Appeal lifecycle: CREATED → VERIFIED → SUBMITTED → AUDITING → (ALL_PASSED/PARTIAL_PASSED/ALL_REJECTED/CANCELLED). All state changes through aggregate methods with validation of preconditions and invariants.

### II. Hexagonal Architecture (NON-NEGOTIABLE)
Strict dependency inversion: api → service → application → domain ← infrastructure. Inner layers never depend on outer layers. Infrastructure implements domain-defined repository interfaces. Domain completely isolated from framework details.

**Module Structure** (Mandatory):
- **api**: `{Aggregate}Service` interfaces and DTOs only
- **service**: `{Aggregate}Provider/Controller` I/O conversion
- **application**: `{Aggregate}ApplicationService` use case orchestration
- **domain**: `{Aggregate}`, `{Entity}`, `{ValueObject}` business core
- **infrastructure**: `{Aggregate}Dao/Entity/Mapper` persistence implementation
- **common**: Shared constants and utilities with unified package structure
- **boot**: Application startup and configuration

### III. Test-First Development (NON-NEGOTIABLE)
TDD mandatory for all business logic implementation. Unit tests required before production code. Test coverage metrics enforced as quality gates.

**Coverage Requirements**:
- **Unit Test Coverage**: ≥90% for domain models and services
- **Integration Test Coverage**: ≥80% for application services and repositories
- **Overall Coverage**: ≥80% minimum, >90% target

**Test Organization**:
- Unit tests: `**/*Test.java`, `**/*Tests.java` (Surefire plugin)
- Integration tests: `**/*IntegrationTest.java`, `**/*IT.java` (Failsafe plugin)
- Framework: JUnit 5 (not JUnit 4) + Mockito + Spring Boot Test

### IV. Rich Domain Models (NON-NEGOTIABLE)
Business logic encapsulated in entities and value objects, not services. Aggregates enforce invariants through explicit methods. Value objects for identity and concepts (AppealId, AppealItemId).

**Design Constraints**:
- No anemic models allowed
- Business rules implemented as aggregate methods
- State validation in domain layer, not application
- Explicit state machines with guard clauses

### V. Contract-First API Design
APIs defined through interfaces in api module. DTOs follow `{Aggregate}{Operation}Request/Response` pattern. Identity fields use value object encapsulation. Swagger documentation mandatory for all endpoints.

**API Standards**:
- JSON as primary format
- Bean Validation annotations on DTOs
- Clear error response structure
- Versioning through URL path: `/api/v1/...`

### VI. Infrastructure Implementation Pattern
Repositories implement domain-defined interfaces only. MyBatis for ORM with explicit XML or annotation mappings. Database entities separate from domain models. Transaction boundaries at application service methods.

**Technical Stack**:
- ORM: MyBatis 2.3.2+ with tk.mybatis 4.2.3+
- Connection Pool: Druid 1.2.23+
- Database: MySQL 8.0+
- Transaction: Spring declarative with `@Transactional`

### VII. Code Quality Standards
Checkstyle enforcement with zero-tolerance for critical violations. Cyclomatic complexity limits. Code duplication elimination. Meaningful business language naming.

**Quality Metrics** (Mandatory):
- **Code Duplication**: ≤5% (ZERO breakthrough allowed)
- **Method Length**: ≤30 lines per method
- **Cyclomatic Complexity**: ≤10 per method
- **Checkstyle Violations**: ≤10 per module
- **No Magic Numbers**: All literals extracted to constants

**Naming Conventions**:
- Classes: PascalCase (`OrderService`)
- Methods: camelCase (`calculateTotal`)
- Packages: all lowercase
- Constants: UPPER_SNAKE_CASE

### VIII. Java Version and Technology Stack (NON-NEGOTIABLE)
JDK 17+ mandatory for all development. LTS version features encouraged: records, sealed classes, pattern matching, text blocks. No deprecated APIs usage. Java module system optional.

**Technology Matrix**:
- **JDK**: 17+ (LTS versions only)
- **Spring Boot**: 2.7.10+ (aligned with JDK 17)
- **Build Tool**: Maven 3.6+
- **Database**: MySQL 8.0+ with utf8mb4
- **Tools**: Lombok 1.18+, MapStruct 1.5+, Swagger 3.0+

### IX. Quality Gates and Metrics (ZERO-TOLERANCE)
Build fails immediately on violation detection. No code proceeds to deployment with failing quality gates.

**Zero-Tolerance Items**:
- ✅ Compilation errors
- ✅ Test failures (unit or integration)
- ✅ Code coverage <80%
- ✅ Security vulnerabilities (OWASP check)
- ✅ Checkstyle critical errors

**Warning Thresholds** (≤10 allowed):
- Code style violations
- Minor code duplication
- Documentation warnings

### X. Exception Handling Standards
Unified exception hierarchy with clear categorization. Business exceptions vs system exceptions clearly separated. All API errors return consistent response format.

**Exception Categories**:
- **BusinessException**: Domain rule violations
- **ValidationException**: Input validation failures
- **DataNotFoundException**: Missing data scenarios
- **SystemException**: Infrastructure failures
- **UnauthorizedException**: Authentication failures

**Error Response Format**:
```json
{
  "code": "ERROR_CODE",
  "message": "User-friendly message",
  "details": "Technical details for debugging",
  "timestamp": "2025-12-10T10:00:00Z"
}
```

### XI. Documentation Standards
All public APIs must have comprehensive JavaDoc. Complex business logic requires inline documentation. README files for each module explaining purpose and usage.

**Documentation Requirements**:
- Public methods: Purpose, parameters, return values, exceptions
- Complex algorithms: Step-by-step explanation with examples
- Module README: Overview, getting started, configuration
- API Documentation: Swagger/OpenAPI 3.0 specification

### XII. Versioning and Dependency Management
Semantic versioning (MAJOR.MINOR.PATCH) for all services. Dependency updates must pass full regression test suite. No SNAPSHOT dependencies in production builds.

**Version Control**:
- Service APIs: URL versioning (`/api/v1/...`)
- Database: Flyway/Liquibase migrations
- Dependencies: Maven `${revision}` property for unified versioning
- Docker: Tag with version and git commit hash

**Dependency Rules**:
- No circular dependencies between modules
- Domain module has zero external dependencies
- Infrastructure depends only on domain abstractions
- Explicit version management, no transitive dependency resolution

## Development Workflow

### Module Creation Pattern
1. Domain model with business rules and state machine
2. Repository interface defining persistence contracts
3. Application service orchestrating use cases
4. API DTOs with validation rules
5. Service implementation handling I/O conversion
6. Infrastructure mapping and persistence

**Validation Steps**: Each phase requires tests before proceeding

### Git Workflow Requirements
Feature branches mandatory (`feature/###-feature-name`). Commit messages follow conventional format. Atomic commits with complete changes including tests.

**Commit Format**:
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**: feature, fix, docs, style, refactor, test, chore
**Scopes**: api, service, application, domain, infrastructure, common, boot

## Governance

### Amendment Process
Constitution amendments require:
1. Written proposal with justification
2. Team review and consensus
3. Impact analysis on existing codebase
4. Migration plan for breaking changes
5. Documentation updates across templates

### Compliance Review
All PRs validated against constitution via automated checks:
- Architecture boundary verification (module dependencies)
- Test coverage enforcement (Jacoco analysis)
- Code quality gates (Checkstyle, PMD, SpotBugs)
- Quality metrics validation (coverage, complexity, duplication)
- Documentation completeness (JavaDoc, Swagger)

### Quality Enforcement
Automated quality gates in CI/CD pipeline:
- Pre-commit: Code style, import organization
- Pre-push: Unit tests, integration tests
- PR validation: Full test suite, coverage report, security scan
- Merge requirements: All gates passed, peer review, constitution compliance

**Automatic Rejection Criteria**:
- Build failures for any reason
- Coverage drop below thresholds
- New security vulnerabilities
- Critical code quality violations
- Missing tests for new features

### Template Updates
Constitution changes require updates to:
- Plan template constitution check section
- Task categorization for new principle types
- Review checklists in PR templates
- Development environment setup guides

**Version**: 2.0.0 | **Ratified**: 2025-12-10 | **Last Amended**: 2025-12-10