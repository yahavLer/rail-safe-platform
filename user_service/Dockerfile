# ====== שלב build (בונה את jar של user_service מתוך המונו-רפו) ======
FROM gradle:8.7-jdk17-alpine AS builder

WORKDIR /workspace

# מעתיקים את כל הפרויקט (root), כולל settings.gradle, build.gradle וכל המודולים
COPY . .

# בונים רק את המודול של user_service
RUN ./gradlew :user_service:bootJar --no-daemon

# ====== שלב ריצה ======
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# מעתיקים את ה-jar שנבנה בשלב הקודם
COPY --from=builder /workspace/user_service/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
