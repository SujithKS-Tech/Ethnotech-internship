# Hospital Appointment Deployment

## What is included

- The backend reads database credentials and the port from environment variables.
- The frontend can call the API from the same origin in production.
- A `docker-compose.yml` file is included to run MySQL and the Spring Boot app together.
- The Spring Boot app serves the frontend from the same deployment.

## Local container deployment

1. Copy `.env.example` to `.env` and set a real `MYSQL_ROOT_PASSWORD`.
2. From `Hospital_appointment_project`, run `docker compose up --build`.
3. Open `http://localhost:8080`.

## Railway deployment

1. Push the current code to your GitHub repository.
2. In Railway, create a new project and choose `Deploy from GitHub repo`.
3. Select this repository: `https://github.com/SujithKS-Tech/Ethnotech-internship.git`.
4. Open the created service and set the Root Directory to `/Hospital_appointment_project/Hospital_appointment-backend`.
5. In the same service, optionally set Watch Paths to `/Hospital_appointment_project/Hospital_appointment-backend/**`.
6. Add a MySQL service to the same Railway project.
7. In the app service Variables tab, add these variables.

```env
SPRING_DATASOURCE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=${{MySQL.MYSQLUSER}}
SPRING_DATASOURCE_PASSWORD=${{MySQL.MYSQLPASSWORD}}
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

8. If your database service is not named `MySQL`, replace `MySQL` in the reference variables with your actual Railway service name.
9. Deploy the staged changes.
10. Open the app service `Settings` -> `Networking` and click `Generate Domain`.

After deployment, your public Railway domain will serve both the UI and API from the same host.

## Environment variables for external hosting

- `PORT`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
