# ============================================================
# Backup de PostgreSQL de Kenpaku Ferretería (Windows / PowerShell)
# Uso:            .\backup.ps1
# Tarea program.: usar el Programador de tareas de Windows (ver PLAN_MANTENIMIENTO.md)
# ============================================================

$ErrorActionPreference = "Stop"

# --- Configuración ---
$DbName        = if ($env:DB_NAME) { $env:DB_NAME } else { "kenpaku" }
$DbUser        = if ($env:DB_USER) { $env:DB_USER } else { "admin" }
$DbHost        = if ($env:DB_HOST) { $env:DB_HOST } else { "localhost" }
$DbPort        = if ($env:DB_PORT) { $env:DB_PORT } else { "5432" }
$BackupDir     = if ($env:BACKUP_DIR) { $env:BACKUP_DIR } else { ".\backups" }
$RetentionDays = if ($env:RETENTION_DAYS) { [int]$env:RETENTION_DAYS } else { 14 }

$Timestamp  = Get-Date -Format "yyyyMMdd_HHmmss"
$BackupFile = Join-Path $BackupDir "kenpaku_$Timestamp.sql"

if (-not (Test-Path $BackupDir)) { New-Item -ItemType Directory -Path $BackupDir | Out-Null }

Write-Host "[$(Get-Date)] Iniciando backup de '$DbName' -> $BackupFile"

# PGPASSWORD debe estar definido en el entorno antes de ejecutar.
& pg_dump -h $DbHost -p $DbPort -U $DbUser $DbName | Out-File -Encoding utf8 $BackupFile

# Comprimir a .zip y eliminar el .sql plano
Compress-Archive -Path $BackupFile -DestinationPath "$BackupFile.zip" -Force
Remove-Item $BackupFile
Write-Host "[$(Get-Date)] Backup completado: $BackupFile.zip"

# --- Rotación ---
Get-ChildItem -Path $BackupDir -Filter "kenpaku_*.sql.zip" |
    Where-Object { $_.LastWriteTime -lt (Get-Date).AddDays(-$RetentionDays) } |
    Remove-Item -Force
Write-Host "[$(Get-Date)] Rotación aplicada (retención: $RetentionDays días)"
