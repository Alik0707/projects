# Stage сборки
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Копируем pom.xml и загружаем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники и собираем
COPY src ./src
RUN mvn clean package -DskipTests -B

# Финальный образ
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Устанавливаем curl для healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    libc6 \
    libsnappy1v5 \
    && rm -rf /var/lib/apt/lists/*

# Копируем JAR из build stage
COPY --from=build /app/target/*.jar app.jar

# Создаем непривилегированного пользователя и группу
RUN groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup -s /bin/false appuser

USER appuser

EXPOSE 8081

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-XX:+UseStringDeduplication", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]

