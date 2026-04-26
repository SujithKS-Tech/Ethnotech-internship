# Expense Tracker (Spring Boot + MySQL + HTML/CSS/JS)

A full-stack expense tracker with:

- Backend: Java + Spring Boot
- Frontend: HTML, CSS, JavaScript
- Database: MySQL (`expense_tracker` schema)

## Features

- Add, edit, and delete expenses
- Filter expenses by month and category
- Set a monthly budget
- Dashboard summary (spent, remaining, top category)
- Category breakdown and recent activity
- Auto-creates required tables in MySQL on startup

## Tech stack

- Spring Boot 3
- Spring Web
- Spring JDBC
- MySQL Connector/J
- Vanilla HTML/CSS/JS frontend served by Spring Boot

## Project structure

- `src/main/java` - Spring Boot backend
- `src/main/resources/static` - frontend files (`index.html`, `styles.css`, `app.js`)
- `src/main/resources/application.properties` - MySQL and app configuration
- `src/main/resources/schema.sql` - schema creation script
- `run.ps1` / `run.bat` - start scripts

## Database configuration

Configured by default in `src/main/resources/application.properties`:

- URL: `jdbc:mysql://localhost:3306/expense_tracker?createDatabaseIfNotExist=true&serverTimezone=Asia/Kolkata`
- Username: `root`
- Password: `Ajay.1@123`

## Run the app

Requirements:

- Java 17+
- Maven (or Maven Wrapper if you add `mvnw.cmd` later)
- MySQL running locally

PowerShell:

```powershell
.\run.ps1
```

Command Prompt:

```bat
run.bat
```

Or directly with Maven:

```bash
mvn spring-boot:run
```

Open:

`http://localhost:8080`
