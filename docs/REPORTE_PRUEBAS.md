# Reporte de Pruebas de Software — Kenpaku Ferretería

## 1. Conceptos de Testing aplicados

El testing es el proceso de verificar que el software cumple con los requisitos y no
contiene defectos. En este proyecto se aplican dos niveles de la **pirámide de pruebas**:

| Nivel | Qué prueba | Herramienta | Ejemplo en el proyecto |
|-------|-----------|-------------|------------------------|
| **Pruebas unitarias** | Una clase aislada, con dependencias simuladas (*mocks*) | JUnit 5 + Mockito + AssertJ | `ProductoServiceTest`, `CategoriaServiceTest` |
| **Pruebas de integración** | Varias capas reales trabajando juntas (DAO + BD) | Spring Boot Test + H2 | `UserRepositoryTest`, `contextLoads` |

Otros conceptos aplicados:
- **AAA (Arrange–Act–Assert)**: cada test prepara datos, ejecuta la acción y verifica.
- **Aislamiento**: las pruebas unitarias usan *mocks* (Mockito) para no depender de la BD.
- **BD de pruebas en memoria (H2)**: las pruebas de integración no requieren PostgreSQL.
- **Repetibilidad**: `ddl-auto=create-drop` recrea el esquema en cada ejecución.

## 2. Casos de prueba implementados

### ProductoServiceTest (unitaria)
1. `crearGeneraSkuAutomatico` — verifica que al crear un producto se genera el SKU con
   formato `PROD-#####`.
2. `crearFallaSinCategoria` — verifica que se lanza `ResourceNotFoundException` si la
   categoría no existe.
3. `obtenerPorIdInexistente` — verifica el manejo de producto inexistente.
4. `desactivarProducto` — verifica el borrado lógico (`activo = false`).
5. `filtrarPorCategoria` — verifica el filtrado de la lista por categoría.

### CategoriaServiceTest (unitaria)
6. `crearCategoria` — verifica el alta con estado activo por defecto.
7. `obtenerTodas` — verifica la conversión entidad → DTO.
8. `actualizarInexistente` — verifica la excepción al actualizar un id inexistente.

### UserRepositoryTest (integración, capa DAO con H2)
9. `findByUsernameExistente` — recupera un usuario guardado.
10. `findByUsernameInexistente` — devuelve `Optional.empty()`.

### KenpakuFerreteriaApplicationTests (integración)
11. `contextLoads` — verifica que el contexto de Spring arranca correctamente.

## 3. Cómo ejecutar las pruebas

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar con reporte de cobertura (JaCoCo)
mvn test
# Reporte HTML generado en: target/site/jacoco/index.html
```

## 4. Resultado de la última ejecución

```
[INFO] Results:
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

✅ **11 pruebas ejecutadas, 0 fallos.**

## 5. Cobertura de código

La cobertura se mide con **JaCoCo** (`jacoco-maven-plugin`). Tras `mvn test`, abrir
`target/site/jacoco/index.html`. Las pruebas cubren la lógica de negocio principal de
la capa de servicios y la capa DAO.

## 6. Defectos encontrados y corregidos durante el testing

Durante la fase de pruebas y revisión de código se detectaron y corrigieron los
siguientes defectos (ver `docs/CHANGELOG_CORRECCIONES.md`):

| # | Defecto | Severidad | Estado |
|---|---------|-----------|--------|
| 1 | La creación de productos fallaba siempre (SKU autogenerado marcado como obligatorio en el formulario) | Crítica | ✅ Corregido |
| 2 | Faltaban las plantillas `producto-form`, `categoria-form`, `marca-form` → error 500 en validación | Crítica | ✅ Corregido |
| 3 | El checkbox "activo" no permitía desactivar al editar | Media | ✅ Corregido |
| 4 | Ausencia de manejo global de errores (páginas 500 crudas) | Media | ✅ Corregido |
