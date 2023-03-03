FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app
ADD target/akraness-wait-list-0.0.1-SNAPSHOT.jar akraness-wait-list.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar","akraness-wait-list.jar"]