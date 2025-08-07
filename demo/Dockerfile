# Etapa 1: Compilar usando Maven + Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . /app/
RUN mvn clean install -DskipTests -B && \
    mvn dependency:list -DoutputFile=target/mvn-dependency-list.log -B

# Etapa 2: Ejecutar usando Java 21 con bash disponible
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

CMD ["java", "-jar", "/app/app.jar"]
