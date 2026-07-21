# Plan de Despliegue — Kenpaku Ferretería

## 1. Conceptos

El despliegue de una aplicación Java con **Maven** consiste en empaquetar el código en
un artefacto ejecutable (**JAR** con servidor Tomcat embebido) y ponerlo en marcha sobre
un servidor con la base de datos configurada. Spring Boot produce un *fat-jar*
autocontenido, lo que simplifica el despliegue frente a un WAR tradicional.

## 2. Ciclo de vida Maven usado

| Comando | Fase | Resultado |
|---------|------|-----------|
| `mvn clean` | clean | Borra `target/` |
| `mvn compile` | compile | Compila el código |
| `mvn test` | test | Ejecuta las pruebas |
| `mvn package` | package | Genera `target/kenpaku-ferreteria-0.0.1-SNAPSHOT.jar` |
| `mvn verify -Psecurity` | verify | Añade análisis de seguridad de dependencias |

## 3. Opción A — Despliegue con Docker Compose (recomendado)

Levanta la base de datos **y** la aplicación con un solo comando:

```bash
docker-compose up --build -d
```

- `postgres`: base de datos PostgreSQL 15 con volumen persistente.
- `app`: imagen construida desde el `Dockerfile` (build multi-etapa Maven → JRE).
- La app espera a que Postgres esté *healthy* (`depends_on: condition: service_healthy`).
- Acceso: <http://localhost:8080>

Detener: `docker-compose down` (los datos persisten en el volumen `postgres_data`).

## 4. Opción B — Despliegue manual (JAR)

```bash
# 1. Base de datos (si no se usa Docker)
createdb kenpaku

# 2. Empaquetar
mvn clean package

# 3. Ejecutar (configurando la conexión por variables de entorno)
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/kenpaku
export SPRING_DATASOURCE_USERNAME=admin
export SPRING_DATASOURCE_PASSWORD=admin123
java -jar target/kenpaku-ferreteria-0.0.1-SNAPSHOT.jar
```

## 5. Opción C — Servicio systemd (servidor Linux)

Para que la aplicación arranque con el servidor y se reinicie ante fallos:

```ini
# /etc/systemd/system/kenpaku.service
[Unit]
Description=Kenpaku Ferreteria
After=network.target postgresql.service

[Service]
User=kenpaku
WorkingDirectory=/opt/kenpaku
Environment=SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/kenpaku
Environment=SPRING_DATASOURCE_USERNAME=admin
Environment=SPRING_DATASOURCE_PASSWORD=admin123
ExecStart=/usr/bin/java -jar /opt/kenpaku/kenpaku-ferreteria-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now kenpaku
sudo systemctl status kenpaku
```

## 6. Configuración por entornos

La conexión a la BD se controla con variables de entorno (Spring *relaxed binding*),
sin recompilar:

| Variable | Descripción |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuario de BD |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de BD |
| `SERVER_PORT` | Puerto HTTP (por defecto 8080) |

## 7. Verificación post-despliegue

```bash
curl http://localhost:8080/actuator/health   # debe responder {"status":"UP"}
```
