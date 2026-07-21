# Reporte de Pruebas de Seguridad — Kenpaku Ferretería

## 1. Herramientas y técnicas de apoyo

| Herramienta / Técnica | Propósito | Cómo se usa en el proyecto |
|-----------------------|-----------|----------------------------|
| **Spring Security** | Autenticación, autorización, CSRF | Configurada en `SecurityConfig` |
| **BCrypt** | Hash de contraseñas | `BCryptPasswordEncoder` |
| **OWASP Dependency-Check** | Análisis de composición (SCA): detecta librerías con CVE conocidos | `mvn verify -Psecurity` |
| **OWASP ZAP** (manual) | Escaneo dinámico (DAST) de la app en ejecución | Recomendado sobre `http://localhost:8080` |
| **Revisión de código** | Análisis estático manual | Realizada sobre controladores, servicios y config |

## 2. Análisis de dependencias (SCA)

Ejecutar:

```bash
mvn verify -Psecurity
# Reporte: target/dependency-check-report.html
```

El build **falla automáticamente** si se detecta una vulnerabilidad con CVSS ≥ 8
(configurado en el perfil `security` del `pom.xml`). Esto obliga a actualizar las
dependencias afectadas antes de desplegar.

## 3. Controles de seguridad implementados (checklist OWASP)

| Riesgo OWASP Top 10 | Control | Estado |
|---------------------|---------|--------|
| A01 Control de acceso roto | Todas las rutas requieren autenticación salvo login y recursos estáticos; autorización por rol | ✅ |
| A02 Fallos criptográficos | Contraseñas con BCrypt (nunca en texto plano) | ✅ |
| A03 Inyección | Uso de Spring Data JPA con consultas parametrizadas (sin SQL concatenado) | ✅ |
| A05 Configuración incorrecta | Endpoints de Actuator sensibles protegidos; solo `health`/`info` públicos | ✅ |
| A07 Fallos de autenticación | Bloqueo tras 3 intentos fallidos (`LoginAttemptService`) | ✅ |
| CSRF | Tokens CSRF activados por defecto e inyectados en todos los formularios | ✅ |

## 4. Observaciones levantadas y recomendaciones

| # | Observación | Severidad | Recomendación |
|---|-------------|-----------|---------------|
| S1 | Credenciales de BD y usuarios demo (`admin/admin`) escritas en el código/propiedades | Alta | Externalizar a variables de entorno / *secrets*; cambiar contraseñas antes de producción |
| S2 | La aplicación sirve por HTTP sin TLS | Alta | Terminar TLS en un *reverse proxy* (Nginx) o habilitar HTTPS en Spring |
| S3 | Contraseñas de usuarios demo muy débiles (`admin`, `1234`) | Media | Aplicar política de contraseñas fuertes |
| S4 | Cabeceras de seguridad HTTP por defecto | Baja | Añadir HSTS y Content-Security-Policy en el proxy |
| S5 | Actuator expone `env`/`loggers` a usuarios autenticados | Baja | Restringir a rol ADMIN si se despliega públicamente |

> **Nota:** Las credenciales por defecto son adecuadas para el entorno de desarrollo/
> demostración académica, pero **deben cambiarse** en un despliegue real.

## 5. Prueba dinámica recomendada (OWASP ZAP)

1. Levantar la app: `docker-compose up`.
2. Configurar ZAP como proxy y navegar la aplicación autenticado.
3. Ejecutar *Active Scan* sobre `http://localhost:8080`.
4. Registrar hallazgos y contrastar con esta checklist.
