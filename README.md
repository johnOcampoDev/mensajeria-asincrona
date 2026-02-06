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

- RabbitMQ (con consola de administración)  
- MySQL (con volumen persistente)  
- MongoDB (con volumen persistente)  
- Message Gateway Service  
- Message Processor Service  

**Ejecutar el entorno completo**

```bash
docker-compose up --build
```

**Accesos**

- Gateway API: <http://localhost:8080>
- Processor API: <http://localhost:8081>
- RabbitMQ UI: <http://localhost:15672>
- user: guest
- pass: guest

---

## 🔐 Seguridad

El gateway utiliza API Key configurable mediante properties para proteger los endpoints REST.

**Ejemplo:**

security.api-key=CHANGE_ME_SECURE_KEY

## 🧪 Manejo de errores

**Errores de serialización o negocio:**

- Se persisten en MongoDB
- Se envía el mensaje a la Dead Letter Queue
- No se pierde información
- El sistema es auditable

## 🎯 Decisiones clave

- Arquitectura asíncrona para desacoplar recepción y procesamiento
- Separación clara entre validación (MySQL) y persistencia de mensajes (MongoDB)
- ACK manual en RabbitMQ para control explícito
- DLQ para manejo de errores
- Microservicios independientes y escalables

## 📌 Consideraciones finales

**Esta solución está diseñada para:**

- Alta extensibilidad
- Facilidad de despliegue
- Claridad arquitectónica
- Escenarios reales de mensajería

---
