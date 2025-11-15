# -------- Stage 1: Build the JAR --------
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY . .

# Give mvnw permission to run
RUN chmod +x mvnw

# Build Spring Boot JAR
RUN ./mvnw clean package -DskipTests


# -------- Stage 2: Run the JAR --------
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
