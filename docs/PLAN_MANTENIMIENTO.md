# Plan de Mantenimiento — Kenpaku Ferretería

El mantenimiento asegura la continuidad, integridad y evolución de la aplicación a lo
largo del tiempo. Se organiza en **backups**, **cron jobs** (tareas programadas) y
**scripts** de apoyo.

## 1. Backups de la base de datos

Scripts incluidos en `scripts/`:

| Script | Plataforma | Función |
|--------|-----------|---------|
| `backup.sh` | Linux / macOS | Backup comprimido de PostgreSQL + rotación |
| `backup.ps1` | Windows | Equivalente en PowerShell |
| `restore.sh` | Linux / macOS | Restaura un backup |

Ejecución manual (Linux):

```bash
export PGPASSWORD=admin123
./scripts/backup.sh
# Genera backups/kenpaku_YYYYMMDD_HHMMSS.sql.gz
```

Restauración:

```bash
export PGPASSWORD=admin123
./scripts/restore.sh backups/kenpaku_20260720_020000.sql.gz
```

**Política de retención**: los backups con más de 14 días se eliminan automáticamente
(configurable con `RETENTION_DAYS`). Los backups semanales se conservan 90 días.

## 2. Cron jobs (tareas programadas)

Definidos en `scripts/crontab.example`:

| Frecuencia | Tarea |
|------------|-------|
| Diario 02:00 | Backup de la base de datos |
| Domingo 03:00 | Backup semanal (retención larga) |
| Lunes 04:00 | Limpieza de logs rotados con +30 días |
| Cada 5 min | Verificación de salud (`/actuator/health`) |

Instalación en Linux:

```bash
crontab -e
# pegar las líneas de scripts/crontab.example ajustando las rutas
```

En **Windows** se usa el *Programador de tareas* apuntando a `backup.ps1`:

```powershell
schtasks /create /tn "KenpakuBackup" /tr "powershell -File C:\opt\kenpaku\scripts\backup.ps1" /sc daily /st 02:00
```

## 3. Mantenimiento de la aplicación

| Actividad | Frecuencia | Comando / Acción |
|-----------|-----------|------------------|
| Actualizar dependencias | Mensual | `mvn versions:display-dependency-updates` |
| Auditoría de seguridad | Mensual | `mvn verify -Psecurity` |
| Revisión de logs de error | Semanal | `grep ERROR logs/kenpaku.log` |
| Prueba de restauración de backup | Trimestral | `restore.sh` en entorno de staging |
| Limpieza de datos inactivos | Según necesidad | Revisar productos/categorías desactivados |

## 4. Mantenimiento de la base de datos

```sql
-- Recuperar espacio y actualizar estadísticas (mensual)
VACUUM ANALYZE;
```

## 5. Gestión de versiones y cambios

- Control de versiones con **Git** (cada cambio en un commit descriptivo).
- Los cambios y correcciones se documentan en `docs/CHANGELOG_CORRECCIONES.md`.
- Estrategia de ramas sugerida: `main` (estable) + ramas de funcionalidad.

## 6. Plan de recuperación ante desastres (resumen)

1. Aprovisionar servidor + PostgreSQL.
2. Restaurar el último backup con `restore.sh`.
3. Desplegar el JAR / `docker-compose up`.
4. Verificar `/actuator/health` = UP.
