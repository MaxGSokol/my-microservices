FROM eclipse-temurin:21-jdk as builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests -pl bot-core

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/bot-core/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]