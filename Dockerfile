# -------- BUILD STAGE --------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# copy only pom first (better caching)
COPY pom.xml .

# download dependencies first (faster builds)
RUN mvn dependency:go-offline -B

# copy source code
COPY src ./src

# build jar
RUN mvn clean package -DskipTests

# -------- RUN STAGE --------
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# expose port
EXPOSE 8080

# run application
ENTRYPOINT ["java", "-jar", "app.jar"]
