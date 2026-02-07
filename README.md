# 📩 Message Processing Platform

Plataforma de procesamiento de mensajes asíncronos basada en microservicios, diseñada para recibir solicitudes REST, validarlas contra una base de datos relacional, publicarlas en un broker de mensajería y procesarlas de forma desacoplada con persistencia documental.

---

## 🧠 Arquitectura general

La solución está compuesta por dos microservicios independientes y un broker de mensajería, siguiendo principios de separación de responsabilidades, desacoplamiento y procesamiento asíncrono.

Client
|
v
[ Message Gateway ]
|  (RabbitMQ - exchange)
v
[ Message Processor ]
|
v
[ MongoDB ]

**Componentes:**

- Message Gateway Service  
- Message Processor Service  
- RabbitMQ  
- MySQL  
- MongoDB  

---

## 🧩 Message Gateway Service

**Responsabilidad**

- Exponer una API REST para recepción de mensajes  
- Validar el remitente contra MySQL  
- Publicar mensajes válidos en RabbitMQ  
- Actuar como servicio stateless  

**Tecnologías**

- Spring Boot 3  
- Spring Web  
- Spring Security (API Key)  
- Spring Data JPA  
- MySQL  
- Spring AMQP (RabbitTemplate)  
- Swagger / OpenAPI  

**Flujo**

1. Recibe request REST  
2. Valida API Key  
3. Consulta MySQL para validar remitente  
4. Publica mensaje en RabbitMQ (exchange + routing key)  
5. Retorna respuesta inmediata (procesamiento asíncrono)  

📌 El gateway no declara colas ni DLQ, solo publica mensajes, manteniendo bajo acoplamiento con el consumidor.

---

## 🧩 Message Processor Service

**Responsabilidad**

- Consumir mensajes desde RabbitMQ  
- Procesar lógica de negocio  
- Persistir resultados en MongoDB  
- Manejar errores mediante Dead Letter Queue  

**Tecnologías**

- Spring Boot 3  
- Spring AMQP (@RabbitListener)  
- Spring Data MongoDB  
- MongoDB  
- Jackson  

**Flujo**

1. Consume mensaje desde la cola  
2. Deserializa el payload  
3. Aplica reglas de negocio  
4. Persiste el resultado en MongoDB  
5. ACK manual si es exitoso  
6. NACK → DLQ en caso de error  

---

## 📨 RabbitMQ – Contrato de Mensajería

El **Message Processor** es el dueño de la infraestructura RabbitMQ y declara:

- **Exchange principal:** `message.exchange` (direct)  
- **Queue:** `message.queue`  
- **Routing Key:** `message.routing.key`  
- **Dead Letter Exchange:** `message.dlx`  
- **Dead Letter Queue:** `message.dlq`  

**Decisión arquitectónica**

- Autonomía  
- Arranque independiente  
- Claridad del contrato  

El productor solo publica mensajes al exchange.

---

## 🗄️ Persistencia

**MySQL (Gateway)**

- Usado únicamente para validación de remitentes  
- Acceso en modo lectura  
- `ddl-auto=validate` para evitar modificaciones de esquema  

**MongoDB (Processor)**

- Persistencia de mensajes procesados  
- Documento principal: `MessageDocument`  
- Índice compuesto: `destination + createdDate`  

**Ejemplo de datos almacenados:**

- origen  
- destino  
- tipo de mensaje  
- contenido  
- latencia de procesamiento  
- fecha de creación  
- error (si aplica)  

---

## ⏱️ Métrica de procesamiento

Cada mensaje incluye un header `receivedTimestamp` generado en el gateway.  
El processor calcula la **latencia end-to-end**, desde recepción REST hasta persistencia final.  

➡️ Esto permite auditoría y análisis de rendimiento.

---

## 🐳 Docker y ejecución

**Servicios incluidos en docker-compose**

- RabbitMQ 3.12 (con consola de administración)  
- MySQL 8.0 (con volumen persistente)  
- MongoDB 7 (con volumen persistente)  
- Message Gateway Service (Spring Boot 3)  
- Message Processor Service (Spring Boot 3)  

### ⚙️ Configuración inicial

El archivo `.env` contiene todas las credenciales:  

```dotenv
# RabbitMQ
RABBITMQ_DEFAULT_USER=guest
RABBITMQ_DEFAULT_PASS=guest_secure_password_change_me

# MySQL
MYSQL_ROOT_PASSWORD=root_secure_password_change_me
MYSQL_DATABASE=authorized_origins
MYSQL_USER=app_user
MYSQL_PASSWORD=app_user_secure_password_change_me

# MongoDB
MONGO_INITDB_ROOT_USERNAME=mongo_admin
MONGO_INITDB_ROOT_PASSWORD=mongo_secure_password_change_me
MONGO_DATABASE=messages_db

# API Key (Gateway Security)
SECURITY_API_KEY=your_secure_api_key_change_me_32chars

# Logging
LOG_LEVEL=INFO
```

### 🚀 Ejecutar el entorno completo

```bash
docker-compose up -d --build
```

**Validar que todos los servicios estén saludables:**

```bash
docker-compose ps
```

**Ver logs en tiempo real:**

```bash
docker-compose logs -f message-gateway message-processor
```

### 🌐 Accesos a servicios

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| Gateway API | http://localhost:8080 | Authorization: Bearer `SECURITY_API_KEY` |
| Processor API | http://localhost:8081 | Basic Auth: `processor` / `processor_password` |
| RabbitMQ Console | http://localhost:15672 | `guest` / `guest_secure_password_change_me` |
| MySQL | localhost:3306 | `app_user` / `app_user_secure_password_change_me` |
| MongoDB | localhost:27017 | `mongo_admin` / `mongo_secure_password_change_me` |

---

## 🔐 Seguridad y autenticación

### Gateway

- **Tipo:** API Key Bearer Token  
- **Header:** `Authorization: Bearer {SECURITY_API_KEY}`  
- **Implementación:** `ApiKeyFilter` configurable via `security.api-key`  

### Processor (Servicio Interno)

- **Tipo:** Basic Authentication  
- **Usuario:** `processor`  
- **Contraseña:** `processor_password`  
- **Uso:** Consultas a endpoints de lectura  

### Orígenes autorizados (BD MySQL)

Origenes preconfigurados en `data.sql`:
- `1111` - Sistema A
- `2222` - Sistema B
- `3333` - Sistema C
- `4444` - Aplicación móvil
- `5555` - API de socios

---

## 📬 Pruebas con curl

### 1️⃣ Enviar un mensaje al gateway

```bash
curl -i -X POST http://localhost:8080/api/v1/messages \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your_secure_api_key_change_me_32chars" \
  -d '{
    "origin": "1111",
    "destination": "2222",
    "messageType": "TEXT",
    "content": "Mensaje de prueba - arquitectura asíncrona en acción 🚀"
  }'
```

**Respuesta esperada:** `202 Accepted`

```json
{
  "status": "ACCEPTED",
  "message": "Message received and queued successfully",
  "timestamp": "2026-02-07T19:23:32Z"
}
```

### 2️⃣ Consultar mensajes procesados por destino

```bash
curl -i -u processor:processor_password \
  http://localhost:8081/api/v1/messages/destination/2222
```

**Respuesta esperada:** `200 OK` con array JSON

```json
[
  {
    "id": "69879135bb3db8718d74068c",
    "origin": "1111",
    "destination": "2222",
    "messageType": "TEXT",
    "content": "Mensaje de prueba - arquitectura asíncrona en acción 🚀",
    "processingTime": 658,
    "createdDate": "2026-02-07T19:23:32.850Z",
    "error": null
  }
]
```

### 3️⃣ Ver salud de los servicios

```bash
# Gateway
curl http://localhost:8080/actuator/health

# Processor
curl -u processor:processor_password http://localhost:8081/actuator/health
```

---

## 🧪 Manejo de errores

**Errores de serialización o negocio:**

- Se persisten en MongoDB con campo `error`  
- Se envía el mensaje a la Dead Letter Queue  
- No se pierde información  
- El sistema es auditable  
- `spring.rabbitmq.listener.simple.retry.max-attempts=3` reintenta automáticamente  

**Configuración de reintentosProcessor:**

```properties
spring.rabbitmq.listener.simple.acknowledge-mode=manual
spring.rabbitmq.listener.simple.retry.enabled=true
spring.rabbitmq.listener.simple.retry.max-attempts=3
spring.rabbitmq.listener.simple.prefetch=10
```

## 🧪 Manejo de errores

**Errores de serialización o negocio:**

- Se persisten en MongoDB
- Se envía el mensaje a la Dead Letter Queue
- No se pierde información
- El sistema es auditable

## 🎯 Decisiones clave

- ✅ **Arquitectura asíncrona:** Desacoplamiento entre recepción y procesamiento  
- ✅ **Separación de responsabilidades:** MySQL (validación) + MongoDB (persistencia)  
- ✅ **ACK manual en RabbitMQ:** Control explícito de confirmaciones  
- ✅ **Dead Letter Queue (DLQ):** Manejo robusto de errores  
- ✅ **Microservicios escalables:** Cada componente puede escalar independientemente  
- ✅ **API Key Bearer Token:** Seguridad en el gateway con autenticación configurable  
- ✅ **Health Checks:** Actuator endpoints para monitoreo  
- ✅ **Swagger/OpenAPI:** Documentación automática de APIs  
- ✅ **Logging estructurado:** Trazabilidad end-to-end  

## 📊 Flujo de datos

```
POST /api/v1/messages (origin=1111, dest=2222)
    ↓
[Gateway] Valida API Key + verifica origen en MySQL
    ↓
[RabbitMQ] Publica en exchange → cola
    ↓
[Processor] Consume, deserializa, procesa
    ↓
[MongoDB] Almacena con timestamp y latencia
    ↓
[Aplicación Cliente] Consulta /api/v1/messages/destination/2222
```

## 📌 Consideraciones finales

**Caso de uso ideal:**

- Sistemas que requieren procesamiento asíncrono de eventos  
- Desacoplamiento entre productor y consumidor  
- Auditoría y trazabilidad de mensajes  
- Escalabilidad horizontal  
- Alta disponibilidad  

**Cambios en esta versión:**

- ✅ Credenciales alineadas en `.env`, `docker-compose.yml` y `application*.properties`  
- ✅ ApiKeyFilter mejorado: establece `SecurityContext` correctamente  
- ✅ Message Processor con autenticación Basic configurable  
- ✅ MongoDB con autenticación habilitada en development  
- ✅ Health checks y reintentros configurables  
- ✅ Documentación completa de curls para pruebas  

**Próximos pasos recomendados:**

1. Implementar rate limiting en el gateway  
2. Agregar métricas Prometheus  
3. Configurar tracing distribuido (Jaeger)  
4. Tests de integración automatizados  
5. Pipeline CI/CD con GitHub Actions

---
