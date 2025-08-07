# Etapa 1: Compilación
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . /app/

# Compila el proyecto sin ejecutar tests
RUN mvn clean install -DskipTests -B && \
    mvn dependency:list -DoutputFile=target/mvn-dependency-list.log -B

# Etapa 2: Imagen final liviana solo con Java
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copiamos el .jar desde la etapa de build
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# Comando de arranque
CMD ["java", "-jar", "/app/app.jar"]
