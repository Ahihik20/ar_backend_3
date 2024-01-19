FROM eclipse-temurin:20.0.1_9-jdk-alpine
VOLUME /tmp
COPY *.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080