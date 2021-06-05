FROM gradle:7.0.2-jdk11 AS builder

COPY build.gradle.kts .
COPY src ./src

RUN gradle clean build -q --no-daemon

FROM openjdk:11-jre-slim
COPY --from=builder /home/gradle/build/libs/showcase-api.jar /app.jar

EXPOSE 8080:8080
CMD [ "java", "-jar", "/app.jar"]