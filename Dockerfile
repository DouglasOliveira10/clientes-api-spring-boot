FROM openjdk:8-jdk-alpine

LABEL maintainer="Douglas Oliveira <douglasig1@hotmail.com>"

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENV MYSQL_HOST=172.17.0.2
ENV MYSQL_USER=root
ENV MYSQL_PASSWORD=password

EXPOSE 8080/tcp

ENTRYPOINT ["java","-jar","/app.jar"]