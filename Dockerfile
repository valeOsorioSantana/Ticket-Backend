# Usa una imagen base que ya tenga Maven y Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Establece el directorio de trabajo en el contenedor
WORKDIR /app

# Copia todo el contenido del proyecto al contenedor
COPY . /app/

# Da permisos de ejecución al script mvnw
RUN chmod +x ./mvnw

# Ejecuta el build de Maven (sin ejecutar los tests)
RUN ./mvnw -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean dependency:list install

