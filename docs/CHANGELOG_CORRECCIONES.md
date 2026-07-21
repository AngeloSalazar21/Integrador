# Registro de Correcciones y Mejoras

Documento de trazabilidad de los defectos encontrados en la revisión/testing y las
mejoras aplicadas para cumplir la rúbrica del proyecto final.

## Errores corregidos

### 1. Creación de productos siempre fallaba (crítico)
- **Causa**: `ProductoDTO.sku` estaba marcado como `@NotBlank`/`@Size(min=3)`, pero el
  SKU se genera automáticamente en el servidor (`ProductoService.generateSku`) y el
  formulario lo enviaba vacío al crear. La validación fallaba en cada alta.
- **Solución**: se retiró la validación del campo `sku` en el DTO (es un valor
  gestionado por el servidor) y se protegió `actualizar()` para no borrar el SKU si
  llega vacío.

### 2. Plantillas de formulario inexistentes (crítico)
- **Causa**: los controladores devolvían las vistas `producto-form`, `categoria-form` y
  `marca-form`, que no existían → error 500 (`TemplateInputException`) en cualquier
  fallo de validación y al visitar `/nuevo` o `/editar`.
- **Solución**: se crearon las tres plantillas con `th:object`/`th:field`, mostrando los
  mensajes de error de validación.

### 3. No se podía desactivar al editar (medio)
- **Causa**: un checkbox HTML sin marcar no envía parámetro; Spring dejaba `activo` en
  `true` (valor por defecto del DTO).
- **Solución**: se añadió el marcador de campo `_activo` (convención de Spring) en los
  tres modales, de modo que al desmarcar el checkbox el valor se resetea a `false`.

### 4. Sin manejo global de errores (medio)
- **Causa**: las excepciones no controladas mostraban páginas de error crudas.
- **Solución**: se añadió `GlobalExceptionHandler` (`@ControllerAdvice`) y una plantilla
  `error.html` amigable, con registro en el log.

## Mejoras añadidas para la rúbrica

| Área | Entregable |
|------|-----------|
| Pruebas de software | 11 pruebas (JUnit/Mockito/H2) + JaCoCo · `docs/REPORTE_PRUEBAS.md` |
| Pruebas de seguridad | Perfil OWASP Dependency-Check + `docs/REPORTE_SEGURIDAD.md` |
| Despliegue | `Dockerfile` multi-etapa, `docker-compose` con app+BD · `docs/PLAN_DESPLIEGUE.md` |
| Monitoreo | Actuator (health/metrics), logging rotativo · `docs/PLAN_MONITOREO.md` |
| Mantenimiento | Scripts de backup/restore, cron jobs · `docs/PLAN_MANTENIMIENTO.md` |
| Documentación | Arquitectura (MVC/SOLID/DAO) + diagramas · `docs/ARQUITECTURA.md`; Javadoc |
