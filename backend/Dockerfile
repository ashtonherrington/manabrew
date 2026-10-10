# Build stage: compile and package the Spring Boot jar.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN ./gradlew --no-daemon dependencies > /dev/null
COPY src src
RUN ./gradlew --no-daemon bootJar -x test

# Runtime stage: JRE only, non-root user. USER must be numeric so Kubernetes
# can verify runAsNonRoot.
FROM eclipse-temurin:25-jre
RUN useradd --system --uid 1001 app
USER 1001
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
