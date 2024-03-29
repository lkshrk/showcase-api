FROM gradle:8.3.0-jdk11 AS builder

COPY build.gradle.kts .
COPY src ./src

RUN gradle clean build  -x test -q --no-daemon

FROM openjdk:11-jre-slim
COPY --from=builder /home/gradle/build/libs/showcase-api.jar /app.jar

EXPOSE 8000:8000
CMD [ "java", "-jar", "/app.jar", "-Xopt-in=kotlin.RequiresOptIn"]
