# ---- build stage ----
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# ---- runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd -ms /bin/bash appuser
COPY --from=build /workspace/target/*.jar /app/app.jar
EXPOSE 8080
USER appuser
ENTRYPOINT ["java","-jar","/app/app.jar"]
