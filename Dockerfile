# define base Docker image
FROM openjdk:19-oracle
MAINTAINER Mats Andersson <mats.andersson@centipod.se>
RUN useradd -rm -d /home/fakenews -s /bin/bash -g root -u 1001 fakenews
RUN mkdir -p /var/fakenews/logs
RUN mkdir -p /var/fakenews/logs/archived
RUN chmod 777 /var/fakenews/logs
RUN chmod 777 /var/fakenews/logs/archived
USER fakenews
COPY target/fakenews-1.0.jar fakenews.jar
ENTRYPOINT ["java", "-jar", "/fakenews.jar"]
