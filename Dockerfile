FROM eclipse-temurin:17-jdk-alpine
MAINTAINER bcc-ca.gov.bd

WORKDIR /opt

COPY target/*.jar app.jar

EXPOSE 80

ENTRYPOINT ["java","-jar","app.jar"]