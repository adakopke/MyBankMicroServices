# syntax=docker/dockerfile:1

FROM openjdk:11
COPY ./build/libs/ /tmp
WORKDIR /tmp
CMD java -jar AuthService-0.0.1-SNAPSHOT.jar
