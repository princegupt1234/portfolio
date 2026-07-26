# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only files needed to resolve dependencies first (better layer caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Now copy source and build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ---------- Run stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render (and most hosts) inject $PORT at runtime; application.properties already
# reads server.port=${PORT:8080}, so this works automatically.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
