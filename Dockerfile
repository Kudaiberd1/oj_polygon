FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

RUN chmod +x ./gradlew && ./gradlew --no-daemon dependencies

COPY src/ src/

RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:17-jre AS run
WORKDIR /app

ENV PORT=8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

