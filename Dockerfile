FROM amazoncorretto:17-alpine

RUN mkdir /app
RUN apk add --no-cache curl #install curl for health checks

COPY app.jar /app/app.jar
#for local build
#COPY build/libs/chesschat-0.0.1-SNAPSHOT.jar app/app.jar

WORKDIR /app

EXPOSE 8080

HEALTHCHECK --interval=60s --retries=5 CMD curl --fail http://localhost:8080/login || exit 1
# /login because home is protected

ENTRYPOINT ["java", "-jar", "app.jar"]