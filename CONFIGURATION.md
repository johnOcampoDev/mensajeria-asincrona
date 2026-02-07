# 🔐 Configuración de Usuarios y Variables de Entorno

## 📋 Resumen

Este documento describe la configuración de credenciales, usuarios y variables de entorno para el proyecto de mensajería asíncrona.

---

## 🐳 Docker Compose - Variables de Entorno

El `docker-compose.yml` ahora usa variables de entorno para todas las credenciales sensibles.

### Crear archivo `.env` local

```bash
cp .env.example .env
```

# CONFIGURATION

Documento actualizado para describir la configuración real del proyecto "mensajería-asincrona".

**Servicios principales**
- `message-gateway-service` (puerto 8080)
- `message-processor-service` (puerto 8081)
- `rabbitmq` (AMQP 5672, Management 15672)
- `mysql` (MySQL 3306) — base de datos `authorized_origins`
- `mongodb` (MongoDB 27017) — base de datos `messages_db`

Todos los servicios están orquestados por `docker-compose.yml` en la raíz.

**Archivo con variables de entorno**: usa un `.env` local (no incluido en git). Ejemplo: copia y edita según necesites.

Ejemplo mínimo de variables relevantes (.env):

```
# RabbitMQ
RABBITMQ_DEFAULT_USER=guest
RABBITMQ_DEFAULT_PASS=guest

# MySQL
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=authorized_origins
MYSQL_USER=app_user
MYSQL_PASSWORD=app_pass

# MongoDB
MONGO_INITDB_ROOT_USERNAME=mongo_admin
MONGO_INITDB_ROOT_PASSWORD=mongo_password
MONGO_DATABASE=messages_db

# API key para Gateway
SECURITY_API_KEY=CHANGE_ME_SECURE_KEY
```

**docker-compose** (resumen importante)
- Los servicios y las variables usadas en `docker-compose.yml` son las mostradas arriba. Los valores por defecto están también definidos en el `docker-compose.yml`.
- Los containers exponen puertos locales: `5672`, `15672`, `3306`, `27017`, `8080`, `8081`.
- `message-gateway` depende de `rabbitmq` y `mysql` (healthchecks configurados).
- `message-processor` depende de `rabbitmq` y `mongodb`.

Configuraciones específicas de las aplicaciones

- `message-gateway-service` (archivos: [message-gateway-service/src/main/resources/application-docker.properties](message-gateway-service/src/main/resources/application-docker.properties#L1))
  - Puerto: 8080
  - MySQL: `jdbc:mysql://mysql:3306/authorized_origins`
  - RabbitMQ host: `rabbitmq`, puerto `5672`
  - Exchange: `message.exchange`
  - Routing key: `message.routing.key`
  - Seguridad: propiedad `security.api-key` tomada de `${SECURITY_API_KEY}`. El filtro espera el header `Authorization: Bearer <API_KEY>` (ver `ApiKeyFilter`).
  - Inicialización de la tabla de orígenes en `src/main/resources/schema.sql` y `data.sql`.

- `message-processor-service` (archivos: [message-processor-service/src/main/resources/application-docker.properties](message-processor-service/src/main/resources/application-docker.properties#L1))
  - Puerto: 8081
  - MongoDB URI por defecto apunta a `mongodb:27017` y usa la base `messages_db`.
  - RabbitMQ host: `rabbitmq`, puerto `5672`.
  - Queue: configurada vía `rabbitmq.queue.name` con valor por defecto `message.queue`.
  - Exchange: `message.exchange` y DLX `message.dlx`. Dead letter queue: `message.dlq`.

Seguridad y acceso

- API Key Gateway: el `ApiKeyFilter` valida exactamente el header `Authorization` con el formato `Bearer <clave>` y la compara con la propiedad `security.api-key` cargada desde `application-docker.properties` o la variable `SECURITY_API_KEY` proporcionada por el entorno.
- El `message-processor` mantiene credenciales internas de management en `application.properties` pero está pensado como servicio interno (seguridad mínima en desarrollo).

Colas y routing

- Exchange principal: `message.exchange` (DirectExchange).
- Queue principal: `message.queue` (durable) enlazada con routing key `message.routing.key`.
- Dead letter exchange: `message.dlx` y dead letter queue `message.dlq` con routing key `message.dlq`.

Endpoints útiles

- RabbitMQ management UI: http://localhost:15672
- Gateway Swagger UI: http://localhost:8080/swagger-ui.html
- Processor Swagger UI: http://localhost:8081/swagger-ui.html
- Actuator health Gateway: `http://localhost:8080/actuator/health`

Cómo ejecutar localmente (desarrollo)

1. Crear `.env` a partir de tu plantilla y cambiar secretos.
2. Ejecutar:

```bash
docker-compose up --build
```

O cargar explícitamente el env-file:

```bash
docker-compose --env-file .env up --build
```

Buenas prácticas

- No commitear archivos `.env` con credenciales reales. Añade `.env` a `.gitignore`.
- Para producción, usa un gestor de secretos (Vault, AWS Secrets Manager, etc.) y no variables en texto plano.
- Rotar claves/contraseñas periódicamente y limitar permisos en DBs.

Referencias

- `docker-compose.yml` (raíz)
- `message-gateway-service/src/main/resources/application-docker.properties` (configuración Gateway)
- `message-processor-service/src/main/resources/application-docker.properties` (configuración Processor)
- `message-gateway-service/src/main/java/com/company/messagegateway/config/RabbitConfig.java` (exchange y conversor)
- `message-processor-service/src/main/java/com/company/messageprocessor/config/RabbitMQInfraConfig.java` (queues/bindings)
