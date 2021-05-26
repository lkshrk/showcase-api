FROM openjdk:8-jre-alpine


COPY ./build/install/showcase-api/lib/showcase-api.jar /application.jar
CMD [ "java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "/application.jar" ]