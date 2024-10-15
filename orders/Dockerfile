FROM maven:3-eclipse-temurin-17-alpine AS builder

WORKDIR /application

COPY pom.xml .
COPY src src

RUN mvn clean install -DskipTests
RUN cp target/*.jar application.jar
RUN rm -rf target
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jre-alpine
WORKDIR /application

RUN apk add --no-cache curl

COPY --from=builder application/application ./
COPY --from=builder application/dependencies ./
COPY --from=builder application/snapshot-dependencies ./
COPY --from=builder application/spring-boot-loader ./

ENTRYPOINT [ "java", "org.springframework.boot.loader.launch.JarLauncher" ]