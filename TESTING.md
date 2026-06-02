# Testing Guide - Auction-App

## Overview
This document describes the testing strategy and execution guidelines for the Auction-App project.

---

## Testing Layers

### 1. Unit Tests
**Purpose:** Test individual components in isolation

**Location:** `**/src/test/java/**/*Test.java`

**Technologies:**
- JUnit 5 (Jupiter)
- Mockito for mocking dependencies

**Examples:**
- `AuctionServiceTest.java` - Tests AuctionService methods
- `BidServiceTest.java` - Tests BidService methods
- `PacketMessageTest.java` - Tests packet serialization

**Run unit tests:**
```bash
mvn test
```

### 2. Integration Tests
**Purpose:** Test interactions between components and external systems

**Location:** `**/src/test/java/**/*IntegrationTest.java` or `**/*IT.java`

**Technologies:**
- JUnit 5
- TestContainers (for database testing)

**Examples:**
- `ServerMainIntegrationTest.java` - Tests server startup
- `ClientHandlerIntegrationTest.java` - Tests client-server communication

**Run integration tests:**
```bash
mvn verify
```

### 3. Code Coverage
**Purpose:** Measure how much code is tested

**Tool:** JaCoCo Maven Plugin

**Configuration:**
- Minimum coverage: 50% per package
- Excludes test classes

**View coverage report:**
```bash
mvn jacoco:report
# Reports available at: target/site/jacoco/index.html
```

### 4. Code Quality Analysis
**Purpose:** Identify potential bugs and code smells

**Tools:**
- **SpotBugs:** Finds potential bugs (NullPointerException, resource leaks, etc.)
- **Checkstyle:** Enforces code style guidelines
- **OWASP Dependency Check:** Identifies vulnerable dependencies

**Run quality checks:**
```bash
# SpotBugs
mvn spotbugs:check

# Checkstyle
mvn checkstyle:check

# OWASP Dependency Check
mvn dependency-check:check

# All checks
mvn clean verify
```

---

## Running Tests

### Option 1: Run All Tests (Unit + Integration)
```bash
mvn clean verify
```

### Option 2: Run Only Unit Tests
```bash
mvn clean test
```

### Option 3: Run Specific Test Class
```bash
mvn test -Dtest=AuctionServiceTest
```

### Option 4: Run Tests Matching Pattern
```bash
mvn test -Dtest=*Service*
```

### Option 5: Run Integration Tests Only
```bash
mvn verify -DskipUnitTests
```

### Option 6: Skip All Tests (Build Only)
```bash
mvn clean install -DskipTests
```

---

## Test Structure (Best Practices)

### Setup Pattern
```java
@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
    // Initialize service with mocked dependencies
}
```

### Arrange-Act-Assert Pattern
```java
@Test
void testFeature() {
    // Arrange: Set up test data and expectations
    when(mockDAO.method()).thenReturn(value);
    
    // Act: Call the method under test
    result = service.method();
    
    // Assert: Verify results
    assertEquals(expected, result);
    verify(mockDAO).method();
}
```

### Test Naming Convention
```
testFeature_ExpectedBehavior_Condition
testAddAuction_ReturnsTrue_WhenValidInput
```

### DisplayName for Clarity
```java
@Test
@DisplayName("Should add auction successfully when DAO returns true")
void testAddAuctionSuccess() { ... }
```

---

## GitHub Actions CI/CD

The project includes automated CI/CD pipeline (`.github/workflows/CI.yml`) that:

### 1. Build-and-Test Job
- Runs on: Ubuntu, Windows, macOS
- Java: 21
- Steps:
  - Compile code
  - Run unit tests
  - Package fat jars
  - Run SpotBugs analysis
  - Upload test reports and artifacts

### 2. Code-Coverage Job
- Generates JaCoCo coverage report
- Uploads to Codecov

### 3. Security-Check Job
- Checks for vulnerable dependencies

### 4. Integration-Tests Job
- Runs integration and E2E tests

**Trigger:** Push to `main` or `develop` branch, or Pull Request

---

## Test Reports

### View Test Reports Locally
```bash
# After running tests
mvn surefire-report:report
# Open: target/site/surefire-report.html
```

### View Coverage Report
```bash
mvn jacoco:report
# Open: target/site/jacoco/index.html
```

### View SpotBugs Report
```bash
mvn spotbugs:gui
```

---

## Coverage Requirements

| Module | Minimum Coverage | Current Status |
|--------|------------------|-----------------|
| Commons | 70% | TBD |
| Server | 60% | TBD |
| Client | 50% | TBD |

---

## Mocking Best Practices

### Use Mockito for Dependencies
```java
@Mock
private AuctionDAO auctionDAO;

@InjectMocks
private AuctionService auctionService;
```

### Verify Method Calls
```java
verify(mockDAO, times(1)).method(arg);
verify(mockDAO, never()).method(arg);
verify(mockDAO, atLeastOnce()).method(arg);
```

### Use ArgumentCaptor for Complex Assertions
```java
ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
verify(mockDAO).method(captor.capture());
assertEquals("expected", captor.getValue());
```

---

## Common Issues & Solutions

### Issue: Tests fail with "Cannot find method"
**Solution:** Ensure mocks are initialized with `MockitoAnnotations.openMocks(this)`

### Issue: SpotBugs checks fail
**Solution:** Add null checks or use Optional; use try-with-resources for closeable objects

### Issue: Coverage report not generated
**Solution:** Run `mvn jacoco:report` after tests

### Issue: Integration tests timeout
**Solution:** Increase timeout in failsafe configuration or optimize test setup/teardown

---

## Continuous Improvement

1. **Increase Coverage:** Aim for 80%+ coverage in critical paths
2. **Add E2E Tests:** Test complete user workflows
3. **Performance Tests:** Monitor response times and throughput
4. **Security Tests:** Regular vulnerability scans and penetration testing

---

**Last Updated:** 03/06/2026
