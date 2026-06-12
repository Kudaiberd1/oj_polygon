FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/onlinejudge-polygon-*.jar app.jar

RUN addgroup -S polygon && \
    adduser -S polygon -G polygon
USER polygon

EXPOSE 8000

ENTRYPOINT ["java", \
  "-Dspring.profiles.active=prod", \
  "-jar", \
  "app.jar"]