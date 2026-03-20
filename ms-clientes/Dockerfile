# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copiamos solo el POM primero para aprovechar cache de dependencias
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copiamos el código y compilamos
COPY src ./src
RUN mvn -q -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Usuario no root
RUN useradd -r -u 10001 -m appuser
USER 10001

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8081
ENV SERVER_PORT=8081

ENTRYPOINT ["java","-jar","/app/app.jar"]
