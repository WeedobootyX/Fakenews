# define base Docker image

FROM openjdk:19
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} fakenews.jar
ENTRYPOINT ["java", "-jar", "/fakenews.jar"]
