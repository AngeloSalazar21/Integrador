# Documentación — Kenpaku Ferretería

Índice de la documentación del proyecto, organizada según los criterios de la rúbrica.

| Documento | Criterio de la rúbrica |
|-----------|------------------------|
| [ARQUITECTURA.md](ARQUITECTURA.md) | Diagramas de arquitectura (MVC, SOLID, DAO), modelo de datos |
| [REPORTE_PRUEBAS.md](REPORTE_PRUEBAS.md) | Pruebas de software (testing) |
| [REPORTE_SEGURIDAD.md](REPORTE_SEGURIDAD.md) | Pruebas de seguridad y observaciones |
| [PLAN_DESPLIEGUE.md](PLAN_DESPLIEGUE.md) | Despliegue con Maven y servidores |
| [PLAN_MONITOREO.md](PLAN_MONITOREO.md) | Monitoreo (logs, performance, health) |
| [PLAN_MANTENIMIENTO.md](PLAN_MANTENIMIENTO.md) | Mantenimiento (cron jobs, backups, scripts) |
| [CHANGELOG_CORRECCIONES.md](CHANGELOG_CORRECCIONES.md) | Trazabilidad de defectos corregidos |

## Generar la documentación técnica (Javadoc)

```bash
mvn javadoc:javadoc
# Salida: target/reports/apidocs/index.html
```

## Comandos rápidos

```bash
mvn test                 # ejecutar pruebas (+ cobertura JaCoCo)
mvn clean package        # empaquetar el JAR
mvn verify -Psecurity    # análisis de seguridad de dependencias
docker-compose up --build  # desplegar app + base de datos
```
