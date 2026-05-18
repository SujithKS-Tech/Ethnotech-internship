# Concept Clarity

Concept Clarity is a fullstack project built with:

- Java + Spring Boot backend
- HTML, CSS, and JavaScript frontend
- MySQL database

Users can ask about any concept and choose:

- Explanation level: Beginner, Intermediate, Advanced
- Explanation type: Definition, Detailed explanation, Step-by-step
- Optional learning context

AI features included:

- Simplify a concept
- Generate real-life examples
- Create practice quiz questions
- Create smart study notes

The backend now calls the OpenAI API when `OPENAI_API_KEY` is configured. If the key is missing or the API request fails, the app falls back to the local explanation service so the project still runs for demos.

## MySQL Setup

Your screenshot shows this existing schema name:

```sql
`concept clarity`
```

The project is configured to connect to that exact schema in:

```text
src/main/resources/application.properties
```

The JDBC URL uses `concept%20clarity` because the database name has a space:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/concept%20clarity?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

Current MySQL credentials are read from environment variables, with local defaults in:

```properties
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:...}
```

Spring Boot will create this table automatically because `spring.jpa.hibernate.ddl-auto=update` is enabled:

```text
concept_explanations
```

If you want to create the table manually in MySQL Workbench, run:

```text
database/schema.sql
```

## Run The Project

In PowerShell, set your AI key and MySQL password before starting Spring Boot:

```powershell
$env:OPENAI_API_KEY="your_openai_api_key"
$env:DB_PASSWORD="your_mysql_password"
```

Optional settings:

```powershell
$env:OPENAI_MODEL="gpt-4o-mini"
$env:DB_USERNAME="root"
```

```bash
mvn spring-boot:run
```

Then open:

```text
http://localhost:8081
```

If port 8081 is already busy, run on another port:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
```

Then open:

```text
http://localhost:8082
```

## API

Create and save an explanation:

```http
POST /api/concepts/explain
```

View saved history:

```http
GET /api/concepts/history
```

Run an AI learning tool:

```http
POST /api/concepts/ai-tool
```

## Important Note About AI

Do not put the OpenAI API key in `index.html`, `app.js`, or any frontend file. The browser calls your Spring Boot API, and Spring Boot calls OpenAI from the server side.
