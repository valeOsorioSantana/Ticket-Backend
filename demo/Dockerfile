# Etapa 1: build con Maven y JDK
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . /app/

RUN mvn clean install -DskipTests -B

# Etapa 2: runtime con JDK ligero basado en Ubuntu Jammy para poder instalar bash
FROM ubuntu:22.04

RUN apt-get update && apt-get install -y openjdk-21-jre-headless bash && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

CMD ["java", "-jar", "/app/demo.jar"]
