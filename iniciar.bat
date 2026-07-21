@echo off
REM Script para compilar e iniciar Kenpaku Ferretería
REM Requiere: Maven 3.9.x, Java 21+, PostgreSQL 15

echo.
echo ========================================
echo   Kenpaku Ferretería - Startup Script
echo ========================================
echo.

REM Usamos el Maven Wrapper (mvnw.cmd) incluido en el proyecto.
REM No se necesita instalar Maven; solo Java 21+.
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Error: Java no está instalado o no está en el PATH
    echo Requiere Java 21+ ^-^> https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

REM Verificar si Docker está corriendo (opcional)
docker ps >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Advertencia: Docker no está corriendo
    echo Asegúrate de que PostgreSQL esté disponible en localhost:5432
    echo.
)

echo [1/3] Limpiando compilaciones anteriores...
call mvnw.cmd clean

echo.
echo [2/3] Compilando proyecto...
call mvnw.cmd package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo Error durante la compilación
    pause
    exit /b 1
)

echo.
echo [3/3] Iniciando aplicación...
echo.
echo ========================================
echo   Accede a: http://localhost:8080
echo   Usuario: admin / Contraseña: admin
echo ========================================
echo.

java -jar target\kenpaku-ferreteria-0.0.1-SNAPSHOT.jar

pause
