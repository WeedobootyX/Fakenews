# define base Docker image

# FROM openjdk:8-jdk-alpine
FROM openjdk:19-oracle
MAINTAINER Mats Andersson <mats.andersson@centipod.se>
# RUN addgroup -S bootrunner
RUN useradd -rm -d /home/fakenews -s /bin/bash -g root -u 1001 fakenews
# RUN addgroup -S fakenews && adduser -S fakenews -G fakenews
RUN mkdir -p /var/fakenews/logs
RUN mkdir -p /var/fakenews/logs/archived
RUN chmod 777 /var/fakenews/logs
RUN chmod 777 /var/fakenews/logs/archived
# RUN addgroup -S spring && adduser -S spring -G spring
USER fakenews
# set user later if commands below require root
# USER fakenews
# WORKDIR /home/fakenews
# VOLUME /tmp
COPY target/fakenews-0.1.jar fakenews.jar
ENTRYPOINT ["java", "-jar", "/fakenews.jar"]
