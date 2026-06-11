FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy the built jar (built by the Render build step or locally with mvn package)
COPY target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
