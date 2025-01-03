# Maven Build Stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Application Run Stage
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
# Replace the JAR file name with the correct name generated by your project
COPY --from=build /app/target/leetcode-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
