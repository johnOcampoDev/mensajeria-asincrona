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

Luego edita `.env` con tus valores reales:

```env
# RabbitMQ
RABBITMQ_DEFAULT_USER=guest
RABBITMQ_DEFAULT_PASS=tu_contraseña_segura_aqui

# MySQL
MYSQL_ROOT_PASSWORD=tu_contraseña_root_aqui
MYSQL_DATABASE=authorized_origins
MYSQL_USER=app_user
MYSQL_PASSWORD=tu_contraseña_app_user_aqui

# MongoDB
MONGO_INITDB_ROOT_USERNAME=mongo_admin
MONGO_INITDB_ROOT_PASSWORD=tu_contraseña_mongo_aqui
MONGO_DATABASE=messages_db

# API Key (Gateway)
SECURITY_API_KEY=tu_api_key_segura_aqui_minimo_32_caracteres

# Logging
LOG_LEVEL=INFO
```

---

## 🔐 Credenciales por Servicio

### RabbitMQ
| Variable | Valor Defecto | Ubicación |
|----------|---|---|
| `RABBITMQ_DEFAULT_USER` | `guest` | docker-compose.yml |
| `RABBITMQ_DEFAULT_PASS` | `guest` | docker-compose.yml |

**Puerto:** 5672 (AMQP), 15672 (Management UI)  
**URL Management:** http://localhost:15672

---

### MySQL (Message Gateway)
| Variable | Valor Defecto | Ubicación |
|----------|---|---|
| `MYSQL_ROOT_PASSWORD` | `root` | docker-compose.yml |
| `MYSQL_USER` | `app_user` | docker-compose.yml |
| `MYSQL_PASSWORD` | `app_pass` | docker-compose.yml |
| `MYSQL_DATABASE` | `authorized_origins` | docker-compose.yml |

**Puerto:** 3306  
**Usuario App:** `app_user` / `${MYSQL_PASSWORD}`  
**Base de datos:** `authorized_origins`

**Conexión desde Gateway:**
```properties
spring.datasource.url=jdbc:mysql://mysql:3306/authorized_origins
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

---

### MongoDB (Message Processor)
| Variable | Valor Defecto | Ubicación |
|----------|---|---|
| `MONGO_INITDB_ROOT_USERNAME` | `mongo_admin` | docker-compose.yml |
| `MONGO_INITDB_ROOT_PASSWORD` | `mongo_password` | docker-compose.yml |
| `MONGO_DATABASE` | `messages_db` | docker-compose.yml |

**Puerto:** 27017  
**Usuario Root:** `mongo_admin` / `${MONGO_INITDB_ROOT_PASSWORD}`  
**Base de datos:** `messages_db`

**Conexión desde Processor:**
```properties
spring.data.mongodb.uri=mongodb://${SPRING_DATA_MONGODB_USERNAME}:${SPRING_DATA_MONGODB_PASSWORD}@mongodb:27017/${SPRING_DATA_MONGODB_DATABASE}?authSource=admin
```

---

## 🔑 API Key (Gateway Security)

### Configuración
| Propiedad | Valor Defecto | Ubicación |
|----------|---|---|
| `security.api-key` | `CHANGE_ME_SECURE_KEY` | application-docker.properties |
| `SECURITY_API_KEY` | Variable de entorno | docker-compose.yml |

### Uso
Para llamar a los endpoints del Gateway, incluye el header:
```bash
curl -X POST http://localhost:8080/api/v1/messages \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{...}'
```

**Cambiar la API Key:**
1. Edita el archivo `.env`
2. Establece `SECURITY_API_KEY=tu_clave_nueva`
3. Reinicia los contenedores: `docker-compose up -d`

---

## 📱 Aplicaciones Spring Boot

### Message Gateway (Puerto 8080)

**Variables de entorno disponibles:**
```env
SPRING_DATASOURCE_USERNAME=app_user
SPRING_DATASOURCE_PASSWORD=app_pass
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest
SECURITY_API_KEY=tu_api_key_aqui
```

**Archivo de configuración:** `application-docker.properties`

---

### Message Processor (Puerto 8081)

**Variables de entorno disponibles:**
```env
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest
SPRING_DATA_MONGODB_USERNAME=mongo_admin
SPRING_DATA_MONGODB_PASSWORD=mongo_password
SPRING_DATA_MONGODB_DATABASE=messages_db
```

**Archivo de configuración:** `application-docker.properties`

---

## 🚀 Ejecutar con variables de entorno

### Opción 1: Con archivo `.env`
```bash
docker-compose up --build
```
Docker Compose cargará automáticamente las variables del archivo `.env`

### Opción 2: Con variables en línea
```bash
export RABBITMQ_DEFAULT_USER=tu_usuario
export RABBITMQ_DEFAULT_PASS=tu_contraseña
export MYSQL_ROOT_PASSWORD=tu_pass_root
export MYSQL_USER=app_user
export MYSQL_PASSWORD=tu_pass_app
export MONGO_INITDB_ROOT_USERNAME=mongo_admin
export MONGO_INITDB_ROOT_PASSWORD=tu_pass_mongo
export SECURITY_API_KEY=tu_api_key_segura

docker-compose up --build
```

### Opción 3: Línea de comando completa
```bash
docker-compose --env-file .env up --build
```

---

## 📝 Consideraciones de Seguridad

### ⚠️ Para Desarrollo

Las credenciales en este archivo son adecuadas para **desarrollo local**.

### 🔒 Para Producción

**NO hagas commit del archivo `.env`** con contraseñas reales.

```bash
# .gitignore
.env
.env.local
.env.*.local
```

Recomendaciones para producción:
1. Usa **gestores de secretos** (AWS Secrets Manager, Azure Key Vault, Vault, etc.)
2. Inyecta variables desde CI/CD (GitHub Actions, GitLab CI, etc.)
3. Usa **credenciales aleatorias** generadas por tu plataforma
4. Aplica **rotación periódica** de contraseñas
5. Monitorea accesos a credenciales

---

## 📊 Matriz de Credenciales

```
┌─────────┬──────────────┬─────────────────┬─────────────────────────┐
│ Servicio│ Usuario      │ Contraseña      │ Ubicación               │
├─────────┼──────────────┼─────────────────┼─────────────────────────┤
│ RabbitMQ│ guest        │ guest_secure    │ RABBITMQ_DEFAULT_PASS   │
│ MySQL   │ root         │ root_secure     │ MYSQL_ROOT_PASSWORD     │
│ MySQL   │ app_user     │ app_pass_secure │ MYSQL_PASSWORD          │
│ MongoDB │ mongo_admin  │ mongo_secure    │ MONGO_INITDB_ROOT_PASS  │
│ Gateway │ API Key      │ api_key_secure  │ SECURITY_API_KEY        │
└─────────┴──────────────┴─────────────────┴─────────────────────────┘
```

---

## ✅ Checklist de Seguridad

- [ ] Copié `.env.example` a `.env` y cambié todas las contraseñas
- [ ] Verifiqué que `.env` está en `.gitignore`
- [ ] Cambié `SECURITY_API_KEY` a un valor único
- [ ] Cambié las contraseñas por defecto de RabbitMQ y MySQL
- [ ] Cambié las credenciales de MongoDB
- [ ] Probé la conexión: `docker-compose up --build`
- [ ] Verifiqué que los servicios están saludables
- [ ] Probé la API con la nueva API Key

---

## 🔗 Conexiones de Servicios

```
Client
  │
  ├─→ Gateway (8080) [RabbitMQ guest/pass, MySQL app_user/pass, API Key]
  │      │
  │      └─→ RabbitMQ (5672)
  │           │
  │           └─→ Processor (8081) [RabbitMQ guest/pass, MongoDB mongo_admin/pass]
  │                 │
  │                 └─→ MongoDB (27017)
  │
  └─→ Management UIs
       ├─ RabbitMQ: http://localhost:15672 (guest/pass)
       ├─ Gateway Swagger: http://localhost:8080/swagger-ui.html
       └─ Processor Swagger: http://localhost:8081/swagger-ui.html
```

---

## 📚 Referencias

- [Docker Compose Environment Variables](https://docs.docker.com/compose/environment-variables/)
- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [MySQL Docker Docs](https://hub.docker.com/_/mysql)
- [MongoDB Docker Docs](https://hub.docker.com/_/mongo)
