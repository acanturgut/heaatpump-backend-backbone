## SpringBoot JWT Backbone Project

This project serves as a foundation for applications requiring **JSON Web Tokens (JWT)** authentication with `refresh token`. 

It is a sample **Spring Boot** application designed for tracking heat pumps and their metrics. The system includes user authentication and automatically generates metrics for each heat pump at regular intervals.

Before running the project, ensure you have the following installed:

- **JDK 11** or higher
- **Gradle**
- **IntelliJ IDEA** or **VSCode** with Spring Boot extensions
- **PostgreSQL**

### Configuration

To configure the application to use PostgreSQL, modify the `application.properties` file by replacing the placeholder values with your actual database details:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db_name
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### Future Tasks

- Add alternatives for gRPC and move DTO's to proto. 
- Connect Grafana: Integrate Grafana to visualize and stream metrics.
- Add Tests: Expand the application’s test coverage with more unit and integration tests.
