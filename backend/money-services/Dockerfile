FROM gradle:8.14-jdk21 AS build
ARG SERVICE_NAME
WORKDIR /workspace

COPY . .
RUN chmod +x ./gradlew && ./gradlew :${SERVICE_NAME}:bootJar --no-daemon

FROM eclipse-temurin:21-jre
ARG SERVICE_NAME
WORKDIR /app

COPY --from=build /workspace/${SERVICE_NAME}/build/libs/*.jar app.jar
COPY --from=build /workspace/credit/config/credit-rates-init.json /app/credit/config/credit-rates-init.json
RUN useradd --create-home --shell /bin/bash appuser \
    && chown -R appuser:appuser /app
USER appuser
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
