# Plan de Monitoreo — Kenpaku Ferretería

El monitoreo permite conocer el estado de la aplicación en tiempo real, detectar
incidencias y analizar el rendimiento. Se apoya en tres pilares: **logs**,
**health checks** y **métricas de rendimiento**.

## 1. Logs

Configurado en `application.properties`:

- Salida a consola y a archivo rotativo `logs/kenpaku.log`.
- Rotación: máximo 10 MB por archivo, 14 días de historial, tope de 200 MB.
- Niveles: `INFO` para la aplicación, `WARN` para Spring Security.
- Todos los errores no controlados se registran en `GlobalExceptionHandler`.

```properties
logging.file.name=logs/kenpaku.log
logging.level.com.kenpaku.ferreteria=INFO
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.max-history=14
```

Consulta rápida de errores:

```bash
grep -i "ERROR" logs/kenpaku.log | tail -50
```

## 2. Health checks (Spring Boot Actuator)

| Endpoint | Uso | Acceso |
|----------|-----|--------|
| `/actuator/health` | Estado global (UP/DOWN), incluye BD | Público |
| `/actuator/health/liveness` | ¿El proceso está vivo? (Kubernetes) | Público |
| `/actuator/health/readiness` | ¿Listo para recibir tráfico? | Público |
| `/actuator/info` | Metadatos de la app (versión) | Público |

```bash
curl http://localhost:8080/actuator/health
# {"status":"UP","components":{"db":{"status":"UP"},...}}
```

El `Dockerfile` y `docker-compose.yml` usan `/actuator/health` como *healthcheck* del
contenedor, y el cron de mantenimiento lo consulta cada 5 minutos.

## 3. Métricas de rendimiento (performance tools)

| Endpoint | Métrica |
|----------|---------|
| `/actuator/metrics` | Lista de métricas disponibles |
| `/actuator/metrics/jvm.memory.used` | Memoria JVM en uso |
| `/actuator/metrics/http.server.requests` | Latencia y volumen de peticiones HTTP |
| `/actuator/metrics/hikaricp.connections.active` | Conexiones activas a la BD |

Estos endpoints (salvo health/info) requieren autenticación.

### Integración con herramientas externas (opcional)

Para monitoreo avanzado se puede añadir **Micrometer + Prometheus + Grafana**:
Actuator ya expone las métricas; solo faltaría el dependency `micrometer-registry-prometheus`
para publicar en `/actuator/prometheus` y visualizarlas en Grafana.

## 4. Umbrales y alertas propuestas

| Métrica | Umbral de alerta | Acción |
|---------|------------------|--------|
| `health` = DOWN | inmediato | Revisar logs y conexión a BD |
| Memoria JVM usada | > 85% sostenido | Aumentar `-Xmx` o investigar fuga |
| Latencia HTTP p95 | > 1 s | Revisar consultas/carga |
| Conexiones BD activas | = máximo del pool | Revisar consultas lentas |

## 5. Rutina de monitoreo

- **Diario**: revisar `logs/kenpaku.log` en busca de errores.
- **Automático (cada 5 min)**: cron verifica `/actuator/health` (ver `scripts/crontab.example`).
- **Semanal**: revisar métricas de memoria y latencia.
