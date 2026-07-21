# Script para compilar e iniciar Kenpaku Ferretería
# Uso: .\iniciar.ps1

param(
    [switch]$rebuild = $false,
    [switch]$skipBuild = $false
)

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Kenpaku Ferretería - Startup Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Usamos el Maven Wrapper (mvnw.cmd) incluido en el proyecto: no requiere instalar Maven.
Write-Host "✓ Usando Maven Wrapper (.\mvnw.cmd)" -ForegroundColor Green

# Verificar Java
$javaPath = (Get-Command java -ErrorAction SilentlyContinue).Source
if (-not $javaPath) {
    Write-Host "Error: Java no está instalado" -ForegroundColor Red
    Write-Host "Requiere Java 21+" -ForegroundColor Yellow
    exit 1
}
Write-Host "✓ Java encontrado: $javaPath" -ForegroundColor Green

# Verificar PostgreSQL (opcional)
$dockerRunning = $null -ne (Get-Process docker -ErrorAction SilentlyContinue)
if ($dockerRunning) {
    Write-Host "✓ Docker está corriendo" -ForegroundColor Green
} else {
    Write-Host "⚠ Docker no está corriendo - asegúrate que PostgreSQL esté en localhost:5432" -ForegroundColor Yellow
}

Write-Host ""

# Compilar si es necesario
if (-not $skipBuild) {
    if ($rebuild) {
        Write-Host "[1/3] Limpiando compilaciones anteriores..." -ForegroundColor Cyan
        .\mvnw.cmd clean
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Error durante clean" -ForegroundColor Red
            exit 1
        }
    }

    Write-Host "[2/3] Compilando proyecto..." -ForegroundColor Cyan
    .\mvnw.cmd package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error durante la compilación" -ForegroundColor Red
        exit 1
    }
    Write-Host "✓ Compilación exitosa" -ForegroundColor Green
    Write-Host ""
} else {
    Write-Host "⏭ Saltando compilación (--skipBuild)" -ForegroundColor Yellow
    Write-Host ""
}

# Verificar JAR
$jarPath = "target\kenpaku-ferreteria-0.0.1-SNAPSHOT.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "Error: No se encontró $jarPath" -ForegroundColor Red
    Write-Host "Ejecuta sin --skipBuild para compilar primero" -ForegroundColor Yellow
    exit 1
}

# Iniciar
Write-Host "[3/3] Iniciando aplicación..." -ForegroundColor Cyan
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  ✓ Servidor iniciando..." -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "  📱 Accede a: http://localhost:8080" -ForegroundColor Cyan
Write-Host "  👤 Usuario: admin" -ForegroundColor Cyan
Write-Host "  🔐 Contraseña: admin" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Presiona Ctrl+C para detener el servidor" -ForegroundColor Yellow
Write-Host ""

java -jar $jarPath
