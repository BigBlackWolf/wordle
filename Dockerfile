# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
RUN apk add --no-cache wget

WORKDIR /app

COPY gradle/ gradle/
COPY gradlew ./
RUN chmod +x gradlew

# Resolve dependencies in a separate layer for caching
COPY build.gradle.kts settings.gradle.kts ./
RUN ./gradlew dependencies --no-daemon

# Build the fat jar
COPY src/ src/
RUN ./gradlew bootJar --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=builder --chown=app:app /app/build/libs/wordle-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
