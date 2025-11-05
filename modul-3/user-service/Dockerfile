FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app

# Копируем ВСЕ файлы проекта
COPY . .

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

COPY --from=builder /app/src/main/resources /app/resources

RUN addgroup -S spring && adduser -S spring -G spring
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]