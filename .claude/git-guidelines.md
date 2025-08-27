# Git Guidelines

## Commit Convention

Follow [Conventional Commits](https://conventionalcommits.org/) specification:

### Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types
- **feature**: New feature or functionality
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, etc.)
- **refactor**: Code refactoring without behavior change
- **test**: Adding or modifying tests
- **chore**: Build process or tooling changes
- **performance**: Performance improvements
- **revert**: Reverting previous commits

### Scopes
- **order**: Order-related changes
- **appeal**: Appeal-related changes
- **goods**: Goods/product changes
- **inventory**: Inventory management changes
- **api**: API layer changes
- **ui**: User interface changes
- **db**: Database changes
- **config**: Configuration changes

### Examples
```bash
# Good commits
feature(order): add order cancellation functionality

- Add new OrderCancelAction
- Update OrderState for cancellation flow
- Add validation for cancellation conditions

fix(appeal): resolve appeal status update issue

- Fix incorrect status transition in AppealState
- Add null check for appeal items
- Update tests for edge cases

refactor(service): simplify order creation flow

- Extract common validation logic
- Reduce cyclomatic complexity
- Improve error messages

# Bad commits
"fix stuff"
"update code"
"changes"
```

## Best Practices

### 1. Commit Size
- Keep commits small and focused
- One logical change per commit
- Avoid mixing unrelated changes

### 2. Commit Messages
- Use present tense ("Add feature" not "Added feature")
- Keep subject line under 50 characters
- Provide meaningful body description
- Reference issues/tickets when applicable

### 3. Branching Strategy
- Use feature branches for new development
- Branch naming: `feature/order-cancellation`, `fix/appeal-status`
- Regular rebase from main/master
- Squash commits before merging

### 4. Pull Requests
- Provide clear PR description
- Include testing instructions
- Request specific reviewers
- Ensure CI passes before merge

### 5. Pre-commit Checks
```bash
# Run before committing
mvn test                    # All tests pass
mvn checkstyle:check        # Code style compliance
mvn spotbugs:check         # Static analysis
```

### 6. Quality Gates
- Unit test coverage ≥80%
- All tests must pass
- Code review required
- Security scan passed