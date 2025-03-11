FROM eclipse-temurin:21.0.3_9-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew build -x test --parallel

FROM openjdk:21-jdk
LABEL authors="Fivesec"
WORKDIR /app
COPY --from=build /app/build/libs/honeypot-0.0.1-SNAPSHOT.jar .

ARG RUN_AS_USER=1001
USER ${RUN_AS_USER}
EXPOSE 8888
CMD ["java","-Djava.security.egd=file:/dev/./urandom", "-jar", "honeypot-0.0.1-SNAPSHOT.jar"]