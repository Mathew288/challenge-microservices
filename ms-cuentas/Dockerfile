## Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache deps
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -DskipTests package

## Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# (opcional) usuario no-root
RUN useradd -ms /bin/bash appuser
USER appuser

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
