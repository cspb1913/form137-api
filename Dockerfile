# Use OpenJDK 21 to build the project
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace
COPY gradlew settings.gradle build.gradle ./
RUN chmod +x gradlew
COPY gradle ./gradle
COPY src ./src
RUN ./gradlew bootJar --no-daemon --warning-mode all

# Runtime image with Java 21 on Alpine 3.21
FROM alpine:3.21
RUN apk add --no-cache openjdk21-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
