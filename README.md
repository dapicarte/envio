# BookPoint · Microservicio de Envío (`ms-envio`)

Microservicio encargado de la gestión de envíos dentro del sistema **BookPoint**. Expone una API REST y se comunica con el microservicio de pedidos para obtener la dirección de despacho al crear un envío.

---

## 🛠️ Tecnologías

- **Java 24**
- **Spring Boot 4.0.6**
- **Spring Web MVC**
- **Spring Data JPA**
- **MySQL** (entorno de producción/desarrollo)
- **Lombok** — reducción de código boilerplate
- **RestTemplate** — comunicación con otros microservicios
- **JUnit 5** — pruebas unitarias

---

## 🏗️ Rol en la arquitectura

`ms-envio` consulta al microservicio de pedidos para obtener la dirección de despacho al momento de crear un envío.

| Microservicio | Para qué se consulta |
|---------------|----------------------|
| `ms-pedidos` | Obtener dirección de envío del pedido |

```
Cliente → Gateway (8080) → ms-envio (8095)
                                │
                                └── RestTemplate → ms-pedidos (8081)
```

---

## ✅ Requisitos previos

- JDK 24 o superior
- Maven 3.8+
- MySQL en ejecución (para el perfil por defecto)
- MS Pedidos corriendo si vas a probar el flujo completo

---

## ⚙️ Configuración

### `src/main/resources/application.properties`

```properties
spring.application.name=envio
server.port=8095

spring.datasource.url=jdbc:mysql://localhost:3306/enviosdb
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

---

## 🌐 Acceso vía API Gateway

```
GET http://localhost:8080/api/v1/envios
```

Rutas configuradas en el gateway:

```yaml
- id: ms-envio
  uri: http://localhost:8095
  predicates:
    - Path=/api/v1/envios,/api/v1/envios/**
```

---

## 📡 Endpoints

### Envío — Base: `/api/v1/envios`

| Método | Endpoint | Descripción | Código éxito |
|--------|----------|-------------|--------------|
| `GET` | `/api/v1/envios` | Listar todos los envíos | `200 OK` |
| `GET` | `/api/v1/envios/{id}` | Obtener envío por ID | `200 OK` |
| `POST` | `/api/v1/envios` | Crear nuevo envío | `200 OK` |
| `PUT` | `/api/v1/envios/{id}/estado?estadoEnvio={estado}` | Actualizar estado del envío | `200 OK` |
| `DELETE` | `/api/v1/envios/{id}` | Eliminar envío | `200 OK` |

### Estados disponibles

| Estado | Descripción |
|--------|-------------|
| `PREPARANDO` | Envío en preparación — estado inicial |
| `EN_CAMINO` | Envío en camino |
| `ENTREGADO` | Envío entregado — registra `fechaEntrega` automáticamente |

### Ejemplo de respuesta

```json
{
  "idEnvio": 1,
  "idPedido": 2,
  "direccionEnvio": "Av. Libertad 123, Concepción, Concepción",
  "estadoEnvio": "PREPARANDO",
  "fechaEnvio": "2026-05-25",
  "fechaEntrega": null
}
```

### JSON de entrada para POST

```json
{
  "idPedido": 1,
  "estadoEnvio": "PREPARANDO"
}
```

---

## 🗂️ Modelos de datos

### `Envio`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idEnvio` | `Long` | Identificador único (PK) |
| `idPedido` | `Long` | ID del pedido asociado |
| `direccionEnvio` | `String` | Dirección de despacho obtenida desde ms-pedidos |
| `estadoEnvio` | `EstadoEnvio` | Estado actual del envío |
| `fechaEnvio` | `LocalDate` | Fecha de creación del envío |
| `fechaEntrega` | `LocalDate` | Fecha de entrega (null hasta que se entregue) |

---

## 🧪 Tests

- `@ExtendWith(MockitoExtension.class)` con `@Mock` / `@InjectMocks`
- `@SpringBootTest` + `@AutoConfigureMockMvc`

---

## 📁 Estructura del proyecto

```
ms-envio/
├── src/
│   ├── main/
│   │   ├── java/BookPoint/envio/
│   │   │   ├── config/
│   │   │   │   └── RestTemplateConfig.java
│   │   │   ├── controller/
│   │   │   │   └── EnvioController.java
│   │   │   ├── model/
│   │   │   │   ├── Envio.java
│   │   │   │   ├── EstadoEnvio.java
│   │   │   │   └── PedidoDTO.java
│   │   │   ├── repository/
│   │   │   │   └── EnvioRepository.java
│   │   │   ├── service/
│   │   │   │   └── EnvioService.java
│   │   │   └── EnvioApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/BookPoint/envio/
│           └── EnvioApplicationTests.java
└── pom.xml
```

---

## 👤 Autor

Proyecto **BookPoint** — Microservicio de Envío.