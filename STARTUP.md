# 🚀 Kenpaku Ferretería - Guía de Inicio

## Requisitos Previos

- **Java 21+** → [Descargar](https://www.oracle.com/java/technologies/downloads/)
- **Maven NO es necesario**: el proyecto incluye el **Maven Wrapper** (`mvnw` / `mvnw.cmd`)
- **PostgreSQL 15** o **Docker** para ejecutar PostgreSQL
- **Puerto 8080** disponible (puedes cambiar en `application.properties`)

---

## Opción 1: Script Batch (Windows CMD)

### Primer inicio:
```cmd
iniciar.bat
```

### Compilar y reiniciar:
```cmd
iniciar.bat
```

---

## Opción 2: Script PowerShell (Recomendado)

### Primer inicio:
```powershell
.\iniciar.ps1
```

### Solo compilar y reiniciar (sin limpiar):
```powershell
.\iniciar.ps1 -skipBuild
```

### Compilación limpia (más lenta, más seguro):
```powershell
.\iniciar.ps1 -rebuild
```

**Nota:** Si es la primera vez, ejecuta sin opciones para una compilación completa.

---

## Opción 3: Manual (Paso a Paso)

### 1. Limpiar compilaciones previas
```bash
# Windows:  mvnw.cmd clean     |    Linux/Mac:  ./mvnw clean
mvnw.cmd clean
```

### 2. Compilar
```bash
mvnw.cmd package -DskipTests
```

### 3. Iniciar
```bash
java -jar target/kenpaku-ferreteria-0.0.1-SNAPSHOT.jar
```

---

## 🗄️ Configurar PostgreSQL

### Opción A: Usar Docker (Recomendado)

```bash
docker-compose up -d
```

Esto inicia PostgreSQL automáticamente con la configuración en `docker-compose.yml`.

### Opción B: PostgreSQL Local

1. Instala PostgreSQL 15
2. Crea la base de datos:
   ```sql
   CREATE DATABASE kenpaku;
   ```
3. Verifica conexión en `localhost:5432`

---

## 🌐 Acceso a la Aplicación

Una vez iniciado el servidor:

- **URL:** http://localhost:8080
- **Usuario:** admin
- **Contraseña:** admin

---

## 📋 Información de Contacto

Si encuentras problemas durante el startup, verifica:

1. **Java instalado:** `java -version`
2. **Maven instalado:** `mvn -version`
3. **PostgreSQL corriendo:** `docker-compose ps`
4. **Puerto 8080 libre:** revisa el log de errores

---

## ⚡ Troubleshooting

### "Maven not found"
- Ya no aplica: usa el wrapper incluido `mvnw.cmd` (Windows) o `./mvnw` (Linux/Mac)
- Solo necesitas Java 21+ en el PATH (`java -version`)

### "PostgreSQL connection refused"
- Ejecuta `docker-compose up -d` para iniciar PostgreSQL
- O verifica que PostgreSQL local esté corriendo

### "Port 8080 already in use"
- Edita `application.properties` y cambia `server.port=8080` a otro puerto
- O detén la aplicación que usa el puerto 8080

---

## 📝 Notas

- Primera ejecución: más lenta (descarga dependencias Maven)
- Compilaciones subsecuentes: más rápidas
- Los logs se muestran en la terminal (presiona Ctrl+C para detener)

---

**¡Listo! Tu aplicación Kenpaku Ferretería está corriendo. 🎉**
