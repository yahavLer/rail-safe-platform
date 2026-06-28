# ====== שלב build (בונה את jar של user_service מתוך המונו-רפו) ======
FROM gradle:jdk17 AS builder

WORKDIR /workspace

# מעתיקים את כל הפרויקט (root), כולל settings.gradle, build.gradle וכל המודולים
COPY . .
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

RUN ./gradlew :imageAnalysis-AI_service:bootJar --no-daemon

# ====== שלב ריצה ======
FROM eclipse-temurin:17-jre
WORKDIR /app

# מעתיקים את ה-jar שנבנה בשלב הקודם
COPY --from=builder /workspace/imageAnalysis-AI_service/build/libs/*.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
