# 🎟️ TicketLite Backend

Backend del sistema **TicketLite**, una plataforma diseñada para gestionar la venta y administración de **tickets para eventos**, facilitando la compra, generación y validación de entradas de forma segura y eficiente.

Este proyecto expone una **API REST** desarrollada con **Spring Boot**, permitiendo gestionar usuarios, eventos y tickets, además de integrar autenticación segura, generación de códigos QR y servicios externos.

---

## 📌 Características

- 👤 Gestión de usuarios  
- 🎫 Gestión de tickets  
- 📅 Administración de eventos  
- 🔐 Autenticación y autorización con JWT  
- 📱 Generación de códigos QR para tickets  
- 📧 Envío automático de correos electrónicos  
- ☁️ Almacenamiento de archivos en AWS S3  
- 📖 Documentación automática de API con Swagger / OpenAPI  

---

## 🛠️ Tecnologías Utilizadas

### Backend
- Java 24
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA

### Base de Datos
- PostgreSQL

### Autenticación
- JWT (JSON Web Token)

### Documentación de API
- SpringDoc OpenAPI (Swagger)

### Servicios Externos
- AWS S3 (Amazon Simple Storage Service)

### Librerías adicionales
- ZXing – Generación de códigos QR  
- Spring Mail – Envío de correos electrónicos  
- Thymeleaf – Plantillas de correo  

---

## 📂 Estructura del Proyecto

```
TicketsProyect-Backend
│
├── demo
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com.ticketlite
│   │   │   │       ├── controller
│   │   │   │       ├── service
│   │   │   │       ├── repository
│   │   │   │       ├── model
│   │   │   │       └── security
│   │   │   │
│   │   │   └── resources
│   │   │       ├── application.properties
│   │   │       └── templates
│   │   │
│   │   └── test
│   │
│   └── pom.xml
```

---

## ⚙️ Requisitos

Antes de ejecutar el proyecto debes tener instalado:

- Java JDK 24
- Maven
- PostgreSQL
- Cuenta de AWS (para almacenamiento en S3)

---

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/tuusuario/ticketlite-backend.git
```

### 2. Acceder al proyecto

```bash
cd TicketsProyect-Backend/demo
```

### 3. Configurar `application.properties`

Ejemplo de configuración básica:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ticketlite
spring.datasource.username=postgres
spring.datasource.password=tu_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

También deberás configurar:

- JWT Secret  
- Credenciales de AWS S3  
- Configuración SMTP para envío de correos  

---

### 4. Ejecutar el proyecto

```bash
mvn spring-boot:run
```

También puedes ejecutarlo directamente desde tu IDE (IntelliJ, Eclipse o VSCode).

---

## 📖 Documentación de la API

Una vez iniciado el servidor, puedes acceder a la documentación interactiva en:

```
http://localhost:8080/swagger-ui/index.html
```

Aquí podrás probar todos los endpoints disponibles de la API.

---

## 🔐 Seguridad

La API utiliza **Spring Security junto con JWT** para proteger los endpoints.

### Flujo de autenticación

1. El usuario inicia sesión  
2. El servidor genera un **token JWT**  
3. El cliente envía el token en cada petición protegida  

Ejemplo de encabezado:

```
Authorization: Bearer TOKEN
```

---

## 📧 Funcionalidades adicionales

### Generación de QR

Cada ticket generado incluye un **código QR único**, que permite validar la entrada durante el acceso a los eventos.

### Envío de correos electrónicos

El sistema puede enviar automáticamente:

- Confirmación de compra  
- Información del ticket  
- Notificaciones al usuario  

---

## 👩‍💻 Autor

**Valeria Andrea Osorio Santana**  
Tecnóloga en **Análisis y Desarrollo de Software**

---

## 📄 Licencia

Este proyecto se distribuye bajo la licencia **MIT**.
