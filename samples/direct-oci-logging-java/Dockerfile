FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/direct-oci-logging-java.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
