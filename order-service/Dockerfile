# syntax=docker/dockerfile:1.4
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY order-service/pom.xml order-service/pom.xml
COPY audit-service/pom.xml audit-service/pom.xml
RUN mvn -pl order-service -am dependency:go-offline

COPY . .
RUN mvn -pl order-service -am package -DskipTests

FROM eclipse-temurin:17-jre
ENV SPRING_PROFILES_ACTIVE=local
WORKDIR /app
COPY --from=build /workspace/order-service/target/order-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
