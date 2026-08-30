# ── Etapa 1: build con Maven + JDK 17 ──────────────────────
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Cachear dependencias primero (solo se re-descargan si pom.xml cambia)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw -o dependency:go-offline || ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw package -DskipTests

# ── Etapa 2: imagen final, solo runtime (JRE) ──────────────
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
