FROM openjdk:21-jdk-bullseye AS build
WORKDIR /app
RUN apt-get update && apt-get install -y findutils

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
RUN chmod +x ./gradlew
COPY src ./src
RUN ./gradlew bootJar

FROM openjdk:21-jdk-slim-bullseye
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]