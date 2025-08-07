FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . /app/

# Usamos mvn directamente (ya está incluido en la imagen base)
RUN mvn clean install -DskipTests -B && \
    mvn dependency:list -DoutputFile=target/mvn-dependency-list.log -B
