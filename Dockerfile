FROM gradle:latest AS builder

COPY build.gradle.kts .
COPY src ./src

RUN gradle clean build --no-daemon

FROM openjdk:8-jre-alpine
COPY --from=builder /home/gradle/build/libs/gradle.jar /app.jar

EXPOSE 8080:8080
CMD [ "java", "-jar", "/app.jar"]