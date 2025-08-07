FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . /app/

# Verificar que el pom.xml está presente
RUN ls -la /app

RUN chmod +x ./mvnw
RUN ./mvnw -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean dependency:list install
