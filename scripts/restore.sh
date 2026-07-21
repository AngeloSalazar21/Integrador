#!/usr/bin/env bash
# ============================================================
# Restauración de un backup de PostgreSQL de Kenpaku Ferretería
# Uso: ./restore.sh ./backups/kenpaku_20260720_020000.sql.gz
# ============================================================
set -euo pipefail

if [ $# -lt 1 ]; then
    echo "Uso: $0 <archivo_backup.sql.gz>"
    exit 1
fi

BACKUP_FILE="$1"
DB_NAME="${DB_NAME:-kenpaku}"
DB_USER="${DB_USER:-admin}"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"

if [ ! -f "${BACKUP_FILE}" ]; then
    echo "ERROR: no existe el archivo ${BACKUP_FILE}"
    exit 1
fi

echo "[$(date)] Restaurando '${BACKUP_FILE}' en la base '${DB_NAME}'..."
echo "ADVERTENCIA: esto sobrescribe los datos actuales. Ctrl+C para cancelar (5s)."
sleep 5

gunzip -c "${BACKUP_FILE}" | psql -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USER}" -d "${DB_NAME}"

echo "[$(date)] Restauración completada."
