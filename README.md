# Auction-App
A big project.

To run project using terminal: 
- Preparation: mvn clean install
- Run Server: mvn exec:java -pl Server
- Run Client GUI: mvn javafx:run -pl Client

How to run tests

Full suite (multi-module):
```bash
# macOS / Linux
./mvnw test

# Windows
.\mvnw.cmd test
```

Server module only:
```bash
.\mvnw.cmd -f Server/pom.xml test
```

Client module only:
```bash
.\mvnw.cmd -f Client/pom.xml test
```

Run a specific test class:
```bash
.\mvnw.cmd -f Server/pom.xml -Dtest=ServerTest test
```

Run Server integration tests only:
```bash
.\mvnw.cmd -f Server/pom.xml -Dtest=app.integration.ClientHandlerIntegrationTest,app.integration.MediaServerIntegrationTest,app.integration.ServerMainIntegrationTest test
```

Or run all Server integration tests with pattern matching:
```bash
.\mvnw.cmd -f Server/pom.xml -Dtest=app.integration.* test
```

Notes
---------------------------

- Requirements: JDK 17+ and Maven, or use the bundled wrapper `mvnw` / `mvnw.cmd`.
- JavaFX tests may need the toolkit initialized via `Platform.startup(...)` and UI actions on the FX thread.
- SQLite tests create files under `target/test-db`; delete that directory if a stale file or permission issue occurs.
- Use `-Dtest=...` to run a focused subset of tests when debugging failures.
