FROM maven:3.9.1-eclipse-temurin-11-alpine
WORKDIR /app
COPY ./jardhotelsontimize-api ./jardhotelsontimize-api
COPY ./jardhotelsontimize-boot ./jardhotelsontimize-boot
COPY ./jardhotelsontimize-model ./jardhotelsontimize-model
COPY ./jardhotelsontimize-ws ./jardhotelsontimize-ws
COPY pom.xml .
EXPOSE 33333
RUN mvn install
WORKDIR /app/jardhotelsontimize-boot
CMD mvn spring-boot:run