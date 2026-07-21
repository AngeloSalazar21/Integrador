# ============================================================
# Etapa 1: compilación con Maven (JDK 21)
# ============================================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos primero el pom para aprovechar la caché de dependencias
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copiamos el código y empaquetamos (sin ejecutar tests aquí; se corren en CI)
COPY src ./src
RUN mvn -q clean package -DskipTests

# ============================================================
# Etapa 2: imagen de ejecución ligera (solo JRE)
# ============================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario no root por seguridad
RUN addgroup -S kenpaku && adduser -S kenpaku -G kenpaku
USER kenpaku

COPY --from=build /app/target/kenpaku-ferreteria-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Health check usando el endpoint de Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
