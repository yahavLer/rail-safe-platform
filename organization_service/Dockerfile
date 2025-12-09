# ====== שלב build (בונה את jar של user_service מתוך המונו-רפו) ======
FROM gradle:8.7-jdk17-alpine AS builder

WORKDIR /workspace

# מעתיקים את כל הפרויקט (root), כולל settings.gradle, build.gradle וכל המודולים
COPY . .

RUN ./gradlew :organization_service:bootJar --no-daemon

# ====== שלב ריצה ======
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# מעתיקים את ה-jar שנבנה בשלב הקודם
COPY --from=builder /workspace/organization_service/build/libs/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
