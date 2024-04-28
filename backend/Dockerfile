# Build stage with Java 17
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /workspace
COPY pom.xml .
COPY src src
RUN mkdir -p /app/upload/invoices
RUN mvn -B clean package -DskipTest

# Package stage with Eclipse Temurin Java 17
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
COPY --from=build /app/upload/invoices /app/upload/invoices
ENTRYPOINT ["java","-jar","app.jar"]