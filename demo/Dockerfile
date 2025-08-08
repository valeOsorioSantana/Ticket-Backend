# Etapa 1: Build con Maven y JDK
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . /app/

RUN mvn clean install -DskipTests -B

# Etapa 2: Runtime con JDK ligero y bash instalado
FROM ubuntu:22.04

RUN apt-get update && apt-get install -y openjdk-21-jre-headless bash && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

# Spring Boot detectará variables de entorno pasadas en el run
ENTRYPOINT ["java", "-jar", "/app/demo.jar"]
