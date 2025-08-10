# Используем многоэтапную сборку для уменьшения размера образа
FROM eclipse-temurin:21-jdk as builder
WORKDIR /app
COPY . .
# Собираем JAR без тестов
RUN ./mvnw clean package -DskipTests -pl bot-app

# Финальный образ
FROM eclipse-temurin:21-jre
WORKDIR /app
# Копируем только нужный JAR
COPY --from=builder /app/bot-app/target/*.jar app.jar
# Открываем порт сервиса
EXPOSE 8081
# Команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]