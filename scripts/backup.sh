#!/usr/bin/env bash
# ============================================================
# Backup de la base de datos PostgreSQL de Kenpaku Ferretería
# Uso:   ./backup.sh
# Cron:  0 2 * * *  /ruta/scripts/backup.sh >> /var/log/kenpaku-backup.log 2>&1
# ============================================================
set -euo pipefail

# --- Configuración (puede sobreescribirse por variables de entorno) ---
DB_NAME="${DB_NAME:-kenpaku}"
DB_USER="${DB_USER:-admin}"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
BACKUP_DIR="${BACKUP_DIR:-./backups}"
RETENTION_DAYS="${RETENTION_DAYS:-14}"   # elimina backups más antiguos que N días

TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
BACKUP_FILE="${BACKUP_DIR}/kenpaku_${TIMESTAMP}.sql.gz"

mkdir -p "${BACKUP_DIR}"

echo "[$(date)] Iniciando backup de '${DB_NAME}' -> ${BACKUP_FILE}"

# pg_dump comprimido con gzip. PGPASSWORD debe estar en el entorno.
pg_dump -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USER}" "${DB_NAME}" \
    | gzip > "${BACKUP_FILE}"

echo "[$(date)] Backup completado: $(du -h "${BACKUP_FILE}" | cut -f1)"

# --- Rotación: elimina backups viejos ---
find "${BACKUP_DIR}" -name "kenpaku_*.sql.gz" -type f -mtime "+${RETENTION_DAYS}" -delete
echo "[$(date)] Rotación aplicada (retención: ${RETENTION_DAYS} días)"
