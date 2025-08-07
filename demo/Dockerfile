# Etapa 1: Compilación
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . /app/
RUN mvn clean install -DskipTests -B && \
    mvn dependency:list -DoutputFile=target/mvn-dependency-list.log -B

# Etapa 2: Runtime con bash instalado
FROM eclipse-temurin:21-jdk-jammy

# Instala bash
RUN apt-get update && apt-get install -y bash

WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

CMD ["java", "-jar", "/app/app.jar"]
